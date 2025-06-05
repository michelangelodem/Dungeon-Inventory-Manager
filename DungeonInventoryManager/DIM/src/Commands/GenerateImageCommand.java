package Commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateImageCommand implements ICommand {
    private final Scanner scanner;
    private static final String API_BASE_URL = "http://localhost:8000";
    private static final String IMAGES_FOLDER = "generated_images";
    private final ObjectMapper objectMapper;
    
    public GenerateImageCommand(Scanner scanner) {
        this.scanner = scanner;
        this.objectMapper = new ObjectMapper();
        
        // Create images folder if it doesn't exist
        File imagesDir = new File(IMAGES_FOLDER);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
    }
    
    @Override
    public void execute() {
        System.out.println("=== Image Generation ===\n");
        
        // Check if API is running
        if (!isApiRunning()) {
            System.out.println("Error: Image generation API is not running!");
            System.out.println("Please start the Python API server first:");
            System.out.println("cd DungeonInventoryManager/DIM && python ImageGeneratorAPI.py");
            return;
        }
        
        try {
            // Get user input for image generation
            ImageGenerationRequest request = getImageGenerationRequest();
            
            if (request == null) {
                System.out.println("Image generation cancelled.");
                return;
            }
            
            System.out.println("Generating image(s)... This may take a while.");
            System.out.println("Please wait...\n");
            
            // Generate images asynchronously to show progress
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return generateImages(request);
                } catch (Exception e) {
                    System.err.println("Error during image generation: " + e.getMessage());
                    return false;
                }
            });
            
            // Show progress while waiting
            showProgress(future);
            
            boolean success = future.get(300, TimeUnit.SECONDS); // 5 minute timeout
            
            if (success) {
                System.out.println("\n✓ Image generation completed successfully!");
                System.out.println("Images saved to: " + IMAGES_FOLDER + "/");
            } else {
                System.out.println("\n✗ Image generation failed. Please check the logs.");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private boolean isApiRunning() {
        try {
            URL url = new URL(API_BASE_URL + "/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    private ImageGenerationRequest getImageGenerationRequest() {
        try {
            System.out.print("Enter your image prompt: ");
            String prompt = scanner.nextLine().trim();
            
            if (prompt.isEmpty()) {
                System.out.println("Prompt cannot be empty.");
                return null;
            }
            
            System.out.print("Enter negative prompt (optional, press Enter to skip): ");
            String negativePrompt = scanner.nextLine().trim();
            
            System.out.print("Enter image width (default 1024): ");
            String widthInput = scanner.nextLine().trim();
            int width = widthInput.isEmpty() ? 1024 : Integer.parseInt(widthInput);
            
            System.out.print("Enter image height (default 1024): ");
            String heightInput = scanner.nextLine().trim();
            int height = heightInput.isEmpty() ? 1024 : Integer.parseInt(heightInput);
            
            System.out.print("Enter number of inference steps (default 20): ");
            String stepsInput = scanner.nextLine().trim();
            int steps = stepsInput.isEmpty() ? 20 : Integer.parseInt(stepsInput);
            
            System.out.print("Enter guidance scale (default 4.5): ");
            String guidanceInput = scanner.nextLine().trim();
            double guidance = guidanceInput.isEmpty() ? 4.5 : Double.parseDouble(guidanceInput);
            
            System.out.print("Enter number of images to generate (default 1): ");
            String numImagesInput = scanner.nextLine().trim();
            int numImages = numImagesInput.isEmpty() ? 1 : Integer.parseInt(numImagesInput);
            
            System.out.print("Enter seed (optional, press Enter for random): ");
            String seedInput = scanner.nextLine().trim();
            Integer seed = seedInput.isEmpty() ? null : Integer.parseInt(seedInput);
            
            return new ImageGenerationRequest(prompt, negativePrompt, width, height, steps, guidance, numImages, seed);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid numbers.");
            return null;
        } catch (Exception e) {
            System.out.println("Error getting input: " + e.getMessage());
            return null;
        }
    }
    
    private boolean generateImages(ImageGenerationRequest request) {
        try {
            // Create JSON payload
            String jsonPayload = createJsonPayload(request);
            
            // Make API call
            URL url = new URL(API_BASE_URL + "/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            // Get response
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                // Read response
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                
                // Parse response and save images
                return parseAndSaveImages(response.toString(), request);
                
            } else {
                // Read error response
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                }
                System.out.println("API Error (" + responseCode + "): " + errorResponse.toString());
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Error calling API: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String createJsonPayload(ImageGenerationRequest request) throws Exception {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"prompt\":\"").append(escapeJson(request.prompt)).append("\",");
        json.append("\"negative_prompt\":\"").append(escapeJson(request.negativePrompt)).append("\",");
        json.append("\"width\":").append(request.width).append(",");
        json.append("\"height\":").append(request.height).append(",");
        json.append("\"num_inference_steps\":").append(request.numInferenceSteps).append(",");
        json.append("\"guidance_scale\":").append(request.guidanceScale).append(",");
        json.append("\"num_images\":").append(request.numImages);
        
        if (request.seed != null) {
            json.append(",\"seed\":").append(request.seed);
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private boolean parseAndSaveImages(String jsonResponse, ImageGenerationRequest request) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode imagesNode = rootNode.get("images");
            int seedUsed = rootNode.get("seed_used").asInt();
            
            if (imagesNode == null || !imagesNode.isArray()) {
                System.out.println("No images found in response");
                return false;
            }
            
            System.out.println("Seed used: " + seedUsed);
            
            // Save each image
            for (int i = 0; i < imagesNode.size(); i++) {
                String base64Image = imagesNode.get(i).asText();
                String filename = generateFilename(request.prompt, i + 1, seedUsed);
                
                if (saveBase64Image(base64Image, filename)) {
                    System.out.println("Saved: " + filename);
                } else {
                    System.out.println("Failed to save image " + (i + 1));
                }
            }
            
            return true;
            
        } catch (Exception e) {
            System.out.println("Error parsing response: " + e.getMessage());
            return false;
        }
    }
    
    private String generateFilename(String prompt, int imageNumber, int seed) {
        // Create a safe filename from the prompt
        String safePrompt = prompt.replaceAll("[^a-zA-Z0-9\\s]", "")
                                .replaceAll("\\s+", "_")
                                .toLowerCase();
        
        // Limit filename length
        if (safePrompt.length() > 30) {
            safePrompt = safePrompt.substring(0, 30);
        }
        
        // Add timestamp to ensure uniqueness
        long timestamp = System.currentTimeMillis();
        
        return String.format("%s_%d_seed%d_%d.png", safePrompt, imageNumber, seed, timestamp);
    }
    
    private boolean saveBase64Image(String base64Image, String filename) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            
            File outputFile = new File(IMAGES_FOLDER, filename);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(imageBytes);
            }
            
            return true;
            
        } catch (Exception e) {
            System.out.println("Error saving image: " + e.getMessage());
            return false;
        }
    }
    
    private void showProgress(CompletableFuture<Boolean> future) {
        String[] spinner = {"|", "/", "-", "\\"};
        int spinnerIndex = 0;
        
        while (!future.isDone()) {
            try {
                System.out.print("\rGenerating... " + spinner[spinnerIndex % spinner.length]);
                Thread.sleep(500);
                spinnerIndex++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.print("\r"); // Clear the progress indicator
    }
    
    @Override
    public String getDescription() {
        return "Generate Images";
    }
    
    @Override
    public int getCommandId() {
        return 7;
    }
    
    // Inner class for request data
    private static class ImageGenerationRequest {
        final String prompt;
        final String negativePrompt;
        final int width;
        final int height;
        final int numInferenceSteps;
        final double guidanceScale;
        final int numImages;
        final Integer seed;
        
        public ImageGenerationRequest(String prompt, String negativePrompt, int width, int height, 
                                    int numInferenceSteps, double guidanceScale, int numImages, Integer seed) {
            this.prompt = prompt;
            this.negativePrompt = negativePrompt;
            this.width = width;
            this.height = height;
            this.numInferenceSteps = numInferenceSteps;
            this.guidanceScale = guidanceScale;
            this.numImages = numImages;
            this.seed = seed;
        }
    }
}