package com.erealestate.houserent_sell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erealestate.houserent_sell.model.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Example custom query method to find a property by its title
    Property findByTitle(String title);

    // You can add more custom query methods if needed, for example:
    Property findByAddress(String address);
}
