from diffusers.pipelines.pixart_alpha.pipeline_pixart_sigma import PixArtSigmaPipeline
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
    """Initialize the PixArt-Σ pipeline for CPU usage"""
    global pipeline
    try:
        logger.info("Loading PixArt-Σ pipeline...")
        
        # Load the pipeline with CPU optimization
        pipeline = PixArtSigmaPipeline.from_pretrained(
            "PixArt-alpha/PixArt-Sigma-XL-2-1024-MS",
            torch_dtype=torch.float32,  # Use float32 for CPU
            use_safetensors=True,
        )
        
        # Move to CPU and optimize for CPU inference
        pipeline = pipeline.to("cpu")
        pipeline.enable_attention_slicing()
        
        # Enable memory efficient attention if available
        try:
            pipeline.enable_xformers_memory_efficient_attention()
        except:
            logger.warning("xformers not available, using default attention")
        
        logger.info("Pipeline loaded successfully on CPU")
        
    except Exception as e:
        logger.error(f"Failed to load pipeline: {str(e)}")
        raise e

def pil_to_base64(image: Image.Image) -> str:
    """Convert PIL Image to base64 string"""
    buffer = io.BytesIO()
    image.save(buffer, format="PNG")
    img_str = base64.b64encode(buffer.getvalue()).decode()
    return img_str

@app.on_event("startup")
async def startup_event():
    """Initialize the pipeline when the server starts"""
    initialize_pipeline()

@app.get("/")
async def root():
    return {"message": "PixArt-Σ Image Generation API", "status": "running"}

@app.get("/health")
async def health_check():
    """Health check endpoint"""
    if pipeline is None:
        return {"status": "error", "message": "Pipeline not loaded"}
    return {"status": "healthy", "message": "Pipeline ready"}

@app.post("/generate", response_model=ImageGenerationResponse)
async def generate_image(request: ImageGenerationRequest):
    """Generate images using PixArt-Σ"""
    
    if pipeline is None:
        raise HTTPException(status_code=500, detail="Pipeline not initialized")
    
    try:
        # Set seed for reproducibility
        generator = None
        if request.seed is not None:
            generator = torch.Generator().manual_seed(request.seed)
            seed_used = request.seed
        else:
            # Generate a random seed
            import random
            seed_used = random.randint(0, 2**32 - 1)
            generator = torch.Generator().manual_seed(seed_used)
        
        logger.info(f"Generating image with prompt: {request.prompt[:50]}...")
        
        # Generate images
        with torch.no_grad():  # Disable gradient computation for inference
            result = pipeline(
                prompt=request.prompt,
                negative_prompt=request.negative_prompt if request.negative_prompt else "",
                width=request.width if request.width is not None else 1024,
                height=request.height if request.height is not None else 1024,
                num_inference_steps=request.num_inference_steps if request.num_inference_steps is not None else 20,
                guidance_scale=request.guidance_scale if request.guidance_scale is not None else 4.5,
                num_images_per_prompt=request.num_images if request.num_images is not None else 1,
                generator=generator,
            )
        
        # Convert PIL images to base64
        base64_images = []
        
        # Debug: Log the result type and structure
        logger.info(f"Pipeline result type: {type(result)}")
        try:
            if hasattr(result, '__dict__'):
                logger.info(f"Pipeline result attributes: {list(result.__dict__.keys())}")
            else:
                logger.info(f"Pipeline result dir: {[attr for attr in dir(result) if not attr.startswith('_')]}")
        except Exception as e:
            logger.warning(f"Could not inspect result attributes: {e}")
        
        # Handle different pipeline output formats
        images = None
        
        # Try different ways to extract images from the result
        if hasattr(result, 'images'):
            images_attr = getattr(result, 'images', None)
            if images_attr is not None:
                images = images_attr
                logger.info(f"Found images via .images attribute: {len(images)} images")
            else:
                logger.info("Result has .images attribute but it's None")
        elif isinstance(result, (list, tuple)):
            # Check if it's a direct list/tuple of images
            if len(result) > 0:
                # Check if first element is PIL Image
                if hasattr(result[0], 'save'):  # PIL Image has save method
                    images = result
                    logger.info(f"Direct list of images: {len(images)} images")
                # Check if first element is a list of images
                elif isinstance(result[0], (list, tuple)) and len(result[0]) > 0:
                    if hasattr(result[0][0], 'save'):
                        images = result[0]
                        logger.info(f"Nested list of images: {len(images)} images")
        elif hasattr(result, '__getitem__'):
            # Try to access as indexable (some pipelines return custom objects)
            try:
                potential_images = result[0]
                if isinstance(potential_images, (list, tuple)):
                    if len(potential_images) > 0 and hasattr(potential_images[0], 'save'):
                        images = potential_images
                        logger.info(f"Indexed images: {len(images)} images")
                elif hasattr(potential_images, 'save'):
                    images = [potential_images]
                    logger.info("Single indexed image")
            except (IndexError, TypeError, KeyError) as e:
                logger.warning(f"Could not access result by index: {e}")
        
        # Final fallback - try to find any PIL Images in the result
        if images is None:
            def find_pil_images(obj, depth=0):
                if depth > 3:  # Prevent infinite recursion
                    return []
                found = []
                if hasattr(obj, 'save') and hasattr(obj, 'size'):  # PIL Image
                    found.append(obj)
                elif isinstance(obj, (list, tuple)):
                    for item in obj:
                        found.extend(find_pil_images(item, depth + 1))
                elif hasattr(obj, '__dict__'):
                    for attr_name in dir(obj):
                        if not attr_name.startswith('_'):
                            try:
                                attr_value = getattr(obj, attr_name)
                                found.extend(find_pil_images(attr_value, depth + 1))
                            except:
                                continue
                return found
            
            found_images = find_pil_images(result)
            if found_images:
                images = found_images
                logger.info(f"Found images via deep search: {len(images)} images")
        
        if images is None or len(images) == 0:
            # Log detailed information for debugging
            logger.error(f"Could not extract images from pipeline result.")
            logger.error(f"Result type: {type(result)}")
            logger.error(f"Result content preview: {str(result)[:200]}...")
            if hasattr(result, '__dict__'):
                logger.error(f"Result dict: {result.__dict__}")
            raise ValueError(f"Could not extract images from pipeline result. Result type: {type(result)}, Content preview: {str(result)[:100]}")
        
        for i, image in enumerate(images):
            try:
                if isinstance(image, Image.Image):
                    # Convert PIL Image to base64
                    base64_img = pil_to_base64(image)
                    base64_images.append(base64_img)
                    logger.info(f"Successfully converted image {i+1} to base64")
                else:
                    logger.error(f"Image {i} is not a PIL Image: {type(image)}")
                    # Try to convert if it's a tensor or numpy array
                    if hasattr(image, 'cpu'):  # PyTorch tensor
                        image_np = image.cpu().numpy()
                        if image_np.ndim == 3 and image_np.shape[0] in [1, 3]:
                            # Convert CHW to HWC
                            image_np = image_np.transpose(1, 2, 0)
                        if image_np.max() <= 1.0:
                            image_np = (image_np * 255).astype('uint8')
                        pil_image = Image.fromarray(image_np.squeeze())
                        base64_img = pil_to_base64(pil_image)
                        base64_images.append(base64_img)
                        logger.info(f"Converted tensor image {i+1} to base64")
                    else:
                        raise ValueError(f"Cannot convert image type {type(image)} to PIL Image")
            except Exception as img_error:
                logger.error(f"Error processing image {i}: {str(img_error)}")
                continue
        
        if not base64_images:
            raise ValueError("No valid images generated")
        
        logger.info(f"Successfully generated {len(base64_images)} image(s)")
        
        return ImageGenerationResponse(
            images=base64_images,
            seed_used=seed_used,
            status="success"
        )
        
    except Exception as e:
        logger.error(f"Error generating image: {str(e)}")
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
            img_data = base64.b64decode(base64_img)
            images_data.append({
                "filename": f"generated_image_{i+1}.png",
                "data": base64_img,
                "content_type": "image/png"
            })
        
        return {
            "images": images_data,
            "seed_used": response.seed_used,
            "status": "success"
        }
        
    except Exception as e:
        logger.error(f"Error in generate_image_file: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    
    # Run the server
    uvicorn.run(
        "ImageGeneratorAPI:app",
        host="0.0.0.0",
        port=8000,
        reload=False,  # Disable reload for production
        log_level="info"
    )