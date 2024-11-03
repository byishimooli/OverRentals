package com.erealestate.houserent_sell.controller;

import java.io.File;
import java.util.List; // Correct import for List
import java.util.Optional; // Import for Optional

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; // Import for handling file uploads

import com.erealestate.houserent_sell.dto.PropertyDTO;
import com.erealestate.houserent_sell.model.Property;
import com.erealestate.houserent_sell.service.PropertyService;

@CrossOrigin(origins = "http://localhost:5500") // Allow CORS for the specified origin
@RestController // Use RestController for API endpoints
@RequestMapping("/api/properties") // Adjusting the mapping for API calls
public class PropertyController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // API endpoint to get all properties in JSON format
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties(); // This will automatically be converted to JSON
    }

    // Show properties on the home page
    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "home"; // Ensure there is a home.html in the templates directory
    }

    // Show a specific property by ID
    @GetMapping("/{id}")
    public String getPropertyById(@PathVariable Long id, Model model) {
        Optional<Property> property = propertyService.getPropertyById(id);
        property.ifPresent(p -> model.addAttribute("property", p)); // Use ifPresent to add property to the model
        return "property-details"; // Ensure there's a property-details.html template
    }

    // Show the property posting page
    @GetMapping("/post")
    public String showPostingPage() {
        return "post"; // Ensure there's a post.html template for posting properties
    }

    // API endpoint to post a new property
    @PostMapping("/upload")
    public ResponseEntity<String> postProperty(@ModelAttribute PropertyDTO propertyDTO,
            @RequestParam("file") MultipartFile file) {
        try {
            // Define where to save the file
            String filePath = "path/to/your/uploads/directory/" + file.getOriginalFilename();

            // Save the file to the specified location
            file.transferTo(new File(filePath));

            // Create a new Property object
            Property property = new Property();
            property.setTitle(propertyDTO.getTitle());
            property.setAddress(propertyDTO.getAddress());
            property.setPrice(propertyDTO.getPrice());
            property.setBedrooms(propertyDTO.getBedrooms());
            property.setBathrooms(propertyDTO.getBathrooms());
            property.setPropertyType(propertyDTO.getPropertyType());
            property.setDescription(propertyDTO.getDescription());
            property.setFilePath(filePath); // Save the file path in the Property object

            // Save the property to the database
            propertyService.createProperty(property);

            return ResponseEntity.ok("Property posted successfully"); // Return success response
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to post property");
        }
    }
}
