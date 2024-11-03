package com.erealestate.houserent_sell.service;

import com.erealestate.houserent_sell.model.User;
import java.util.List;

public interface IUserService {
    User createUser(User user) throws Exception;

    boolean authenticateUser(String email, String password) throws Exception;

    List<User> getUsers();

    User getUser(String email);

    void deleteUser(String email);

    // Add the update method
    User updateUser(String email, User user) throws Exception; // New method for updating user
}
