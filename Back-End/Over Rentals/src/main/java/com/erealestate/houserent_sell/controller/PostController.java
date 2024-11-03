package com.erealestate.houserent_sell.controller;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.erealestate.houserent_sell.model.Property;
import com.erealestate.houserent_sell.service.PropertyService;

@CrossOrigin(origins = "http://localhost:5500") // Allow CORS for the specified origin
@RestController
@RequestMapping("/api/properties") // Adjusting the mapping for API calls
public class PostController {

    private final PropertyService propertyService;

    @Autowired
    public PostController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // Method to create a property using JSON data
    @PostMapping("/post")
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        Property savedProperty = propertyService.createProperty(property);
        return new ResponseEntity<>(savedProperty, HttpStatus.CREATED);
    }

    // Method to create a property with a file upload
    @PostMapping("/postWithFile")
    public ResponseEntity<Property> createPropertyWithFile(
            @RequestParam("title") String title,
            @RequestParam("address") String address,
            @RequestParam("price") double price,
            @RequestParam("bedrooms") int bedrooms,
            @RequestParam("bathrooms") int bathrooms,
            @RequestParam("propertyType") String propertyType,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        // Check if the file is empty
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Validate file type (optional)
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/") && !contentType.equals("application/pdf")) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        try {
            // Define where to save the file
            String filePath = "path/to/your/uploads/directory/" + file.getOriginalFilename();

            // Save the file to the specified location
            file.transferTo(new File(filePath));

            // Create a new Property object
            Property property = new Property();
            property.setTitle(title);
            property.setAddress(address);
            property.setPrice(price);
            property.setBedrooms(bedrooms);
            property.setBathrooms(bathrooms);
            property.setPropertyType(propertyType);
            property.setDescription(description);
            property.setFilePath(filePath); // Save the file path in the Property object

            // Save the property to the database
            Property savedProperty = propertyService.createProperty(property);
            return new ResponseEntity<>(savedProperty, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception stack trace for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
