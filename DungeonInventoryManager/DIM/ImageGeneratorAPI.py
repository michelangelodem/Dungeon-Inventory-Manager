from diffusers import PixArtSigmaPipeline
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional, List, Union
import torch
import base64
from PIL import Image
import io
import logging
import random
import numpy as np
import gc

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="PixArt-Σ Image Generation API", version="1.0.0")

# Request model
class ImageGenerationRequest(BaseModel):
    prompt: str
    negative_prompt: Optional[str] = ""
    width: Optional[int] = 1024
    height: Optional[int] = 1024
    num_inference_steps: Optional[int] = 20
    guidance_scale: Optional[float] = 4.5
    num_images: Optional[int] = 1
    seed: Optional[int] = None

# Response model
class ImageGenerationResponse(BaseModel):
    images: List[str]  # Base64 encoded images
    seed_used: int
    status: str

# Global pipeline variable
pipeline = None

def initialize_pipeline():
    """Initialize the PixArt-Σ pipeline for CPU usage with proper error handling"""
    global pipeline
    try:
        logger.info("Loading PixArt-Σ pipeline...")
        
        # Check if CUDA is available (shouldn't be with CPU-only torch)
        device = "cpu"
        logger.info(f"Using device: {device}")
        
        # Load the pipeline with CPU-specific optimizations
        pipeline = PixArtSigmaPipeline.from_pretrained(
            "PixArt-alpha/PixArt-Sigma-XL-2-1024-MS",
            torch_dtype=torch.float32,  # Always use float32 for CPU
            use_safetensors=True,
            variant=None,  # Don't use fp16 variant
            local_files_only=False,
        )
        
        # Move to CPU explicitly
        pipeline = pipeline.to(device)
        
        # CPU-specific optimizations
        try:
            # Enable attention slicing for memory efficiency
            pipeline.enable_attention_slicing(1)
            logger.info("Enabled attention slicing")
        except Exception as e:
            logger.warning(f"Could not enable attention slicing: {e}")
        
        # Try to enable memory efficient attention (only if xformers is available)
        try:
            pipeline.enable_xformers_memory_efficient_attention()
            logger.info("Enabled xformers memory efficient attention")
        except Exception as e:
            logger.warning(f"xformers not available or failed to enable: {e}")
        
        # Enable model CPU offload for memory management
        try:
            pipeline.enable_model_cpu_offload()
            logger.info("Enabled model CPU offload")
        except Exception as e:
            logger.warning(f"Could not enable model CPU offload: {e}")
        
        # Set memory optimization
        try:
            pipeline.enable_sequential_cpu_offload()
            logger.info("Enabled sequential CPU offload")
        except Exception as e:
            logger.warning(f"Could not enable sequential CPU offload: {e}")
        
        logger.info("Pipeline loaded successfully on CPU")
        
        # Test the pipeline with a simple generation
        logger.info("Testing pipeline...")
        test_result = pipeline(
            prompt="a simple test image",
            num_inference_steps=1,
            guidance_scale=1.0,
            width=512,
            height=512,
            generator=torch.Generator().manual_seed(42)
        )
        logger.info(f"Pipeline test successful. Result type: {type(test_result)}")
        
        # Clean up test
        del test_result
        gc.collect()
        
    except Exception as e:
        logger.error(f"Failed to load pipeline: {str(e)}")
        logger.error(f"Error type: {type(e).__name__}")
        raise e

def pil_to_base64(image: Image.Image) -> str:
    """Convert PIL Image to base64 string"""
    try:
        buffer = io.BytesIO()
        # Ensure we're working with RGB mode
        if image.mode != 'RGB':
            image = image.convert('RGB')
        image.save(buffer, format="PNG")
        img_str = base64.b64encode(buffer.getvalue()).decode()
        return img_str
    except Exception as e:
        logger.error(f"Error converting image to base64: {e}")
        raise e

def extract_images_from_result(result):
    """Extract PIL images from diffusers pipeline result"""
    images = []
    
    try:
        # Method 1: Check for .images attribute (most common)
        if hasattr(result, 'images') and result.images is not None:
            images = result.images
            logger.info(f"Found images via .images attribute: {len(images)} images")
            return images
        
        # Method 2: Check if result is directly a list of images
        if isinstance(result, (list, tuple)):
            if len(result) > 0 and hasattr(result[0], 'save'):
                images = result
                logger.info(f"Result is direct list of images: {len(images)} images")
                return images
        
        # Method 3: Check if result has indexable access
        if hasattr(result, '__getitem__'):
            try:
                potential_images = result[0]
                if isinstance(potential_images, (list, tuple)):
                    if len(potential_images) > 0 and hasattr(potential_images[0], 'save'):
                        images = potential_images
                        logger.info(f"Found images via indexing: {len(images)} images")
                        return images
            except (IndexError, TypeError, KeyError):
                pass
        
        # Method 4: Deep search for PIL Images
        def find_pil_images(obj, depth=0, max_depth=3):
            if depth > max_depth:
                return []
            found = []
            
            # Check if current object is a PIL Image
            if hasattr(obj, 'save') and hasattr(obj, 'size') and hasattr(obj, 'mode'):
                found.append(obj)
            elif isinstance(obj, (list, tuple)):
                for item in obj:
                    found.extend(find_pil_images(item, depth + 1, max_depth))
            elif hasattr(obj, '__dict__'):
                for attr_name in ['images', 'image', 'output', 'result']:
                    try:
                        attr_value = getattr(obj, attr_name, None)
                        if attr_value is not None:
                            found.extend(find_pil_images(attr_value, depth + 1, max_depth))
                    except:
                        continue
            
            return found
        
        found_images = find_pil_images(result)
        if found_images:
            logger.info(f"Found images via deep search: {len(found_images)} images")
            return found_images
        
        # If no images found, log debug info
        logger.error(f"Could not extract images from result")
        logger.error(f"Result type: {type(result)}")
        if hasattr(result, '__dict__'):
            logger.error(f"Result attributes: {list(result.__dict__.keys())}")
        
        return []
        
    except Exception as e:
        logger.error(f"Error extracting images from result: {e}")
        return []

@app.on_event("startup")
async def startup_event():
    """Initialize the pipeline when the server starts"""
    try:
        initialize_pipeline()
    except Exception as e:
        logger.error(f"Failed to initialize pipeline on startup: {e}")
        # Don't raise here to allow the server to start even if pipeline fails

@app.get("/")
async def root():
    return {"message": "PixArt-Σ Image Generation API", "status": "running"}

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    if pipeline is None:
        return {"status": "error", "message": "Pipeline not loaded"}
    
    # Check torch installation
    torch_info = {
        "version": torch.__version__,
        "cuda_available": torch.cuda.is_available(),
        "device_count": torch.cuda.device_count() if torch.cuda.is_available() else 0
    }
    
    return {
        "status": "healthy", 
        "message": "Pipeline ready",
        "torch_info": torch_info
    }

@app.post("/generate", response_model=ImageGenerationResponse)
async def generate_image(request: ImageGenerationRequest):
    """Generate images using PixArt-Σ"""
    
    if pipeline is None:
        # Try to reinitialize if it failed on startup
        try:
            initialize_pipeline()
        except Exception as e:
            raise HTTPException(status_code=503, detail=f"Pipeline not available: {str(e)}")
    
    try:
        # Validate input parameters
        if request.width and request.width < 256:
            request.width = 256
        if request.height and request.height < 256:
            request.height = 256
        if request.num_inference_steps and request.num_inference_steps < 1:
            request.num_inference_steps = 1
        if request.num_images and request.num_images > 4:  # Limit for CPU
            request.num_images = 4
        
        # Set seed for reproducibility
        if request.seed is not None:
            seed_used = request.seed
        else:
            seed_used = random.randint(0, 2**31 - 1)  # Use smaller range for compatibility
        
        generator = torch.Generator().manual_seed(seed_used)
        
        logger.info(f"Generating image with prompt: '{request.prompt[:100]}...'")
        logger.info(f"Parameters: {request.width}x{request.height}, steps: {request.num_inference_steps}, guidance: {request.guidance_scale}")
        
        # Clear GPU cache if available (shouldn't be necessary for CPU but good practice)
        if torch.cuda.is_available():
            torch.cuda.empty_cache()
        
        # Generate images with proper error handling
        try:
            with torch.no_grad():
                result = pipeline(
                    prompt=request.prompt,
                    negative_prompt=request.negative_prompt or "",
                    width=request.width,
                    height=request.height,
                    num_inference_steps=request.num_inference_steps,
                    guidance_scale=request.guidance_scale,
                    num_images_per_prompt=request.num_images,
                    generator=generator,
                )
        except RuntimeError as rt_error:
            if "out of memory" in str(rt_error).lower():
                # Try with smaller parameters
                logger.warning("Out of memory, trying with reduced parameters")
                with torch.no_grad():
                    result = pipeline(
                        prompt=request.prompt,
                        negative_prompt=request.negative_prompt or "",
                        width=min(request.width, 512),
                        height=min(request.height, 512),
                        num_inference_steps=min(request.num_inference_steps, 10),
                        guidance_scale=request.guidance_scale,
                        num_images_per_prompt=1,
                        generator=generator,
                    )
            else:
                raise rt_error
        
        # Extract images from result
        images = extract_images_from_result(result)
        
        if not images:
            raise ValueError("No images were generated or extracted from the pipeline result")
        
        # Convert images to base64
        base64_images = []
        for i, image in enumerate(images):
            try:
                if not isinstance(image, Image.Image):
                    logger.warning(f"Image {i} is not a PIL Image, attempting conversion")
                    # Try to convert numpy array or tensor to PIL
                    if hasattr(image, 'cpu'):
                        image = image.cpu().numpy()
                    if isinstance(image, np.ndarray):
                        if image.ndim == 3 and image.shape[0] in [1, 3, 4]:
                            image = image.transpose(1, 2, 0)
                        if image.max() <= 1.0:
                            image = (image * 255).astype(np.uint8)
                        if image.shape[-1] == 4:  # RGBA
                            image = image[:, :, :3]  # Convert to RGB
                        image = Image.fromarray(image.squeeze())
                
                base64_img = pil_to_base64(image)
                base64_images.append(base64_img)
                logger.info(f"Successfully processed image {i+1}/{len(images)}")
                
            except Exception as img_error:
                logger.error(f"Error processing image {i}: {str(img_error)}")
                continue
        
        if not base64_images:
            raise ValueError("Failed to process any generated images")
        
        # Clean up
        if 'result' in locals():
            del result
        if 'images' in locals():
            del images
        gc.collect()
        
        logger.info(f"Successfully generated {len(base64_images)} image(s) with seed {seed_used}")
        
        return ImageGenerationResponse(
            images=base64_images,
            seed_used=seed_used,
            status="success"
        )
        
    except Exception as e:
        logger.error(f"Error generating image: {str(e)}")
        logger.error(f"Error type: {type(e).__name__}")
        
        # Clean up on error
        gc.collect()
        
        raise HTTPException(status_code=500, detail=f"Image generation failed: {str(e)}")

@app.post("/generate-file")
async def generate_image_file(request: ImageGenerationRequest):
    """Generate images and return as downloadable files"""
    
    try:
        # Generate using the main endpoint logic
        response = await generate_image(request)
        
        # Convert base64 back to images for file response
        images_data = []
        for i, base64_img in enumerate(response.images):
            images_data.append({
                "filename": f"generated_image_{response.seed_used}_{i+1}.png",
                "data": base64_img,
                "content_type": "image/png"
            })
        
        return {
            "images": images_data,
            "seed_used": response.seed_used,
            "status": "success",
            "count": len(images_data)
        }
        
    except Exception as e:
        logger.error(f"Error in generate_image_file: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/info")
async def get_info():
    """Get information about the current setup"""
    return {
        "torch_version": torch.__version__,
        "cuda_available": torch.cuda.is_available(),
        "pipeline_loaded": pipeline is not None,
        "model": "PixArt-alpha/PixArt-Sigma-XL-2-1024-MS",
        "device": "cpu",
        "max_recommended_size": "1024x1024",
        "max_images_per_request": 4
    }

if __name__ == "__main__":
    import uvicorn
    
    # Run the server
    uvicorn.run(
        app,  # Use app directly instead of string
        host="0.0.0.0",
        port=8000,
        reload=False,
        log_level="info",
        access_log=True
    )