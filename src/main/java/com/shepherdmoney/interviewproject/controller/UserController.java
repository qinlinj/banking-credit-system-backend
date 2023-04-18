package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user with the given payload
     * @param payload The payload containing user information
     * @return The ID of the created user and a status of OK (200)
     */
    @PostMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser.getId(), HttpStatus.OK);
    }

    /**
     * Delete the user with the given ID
     * @param userId The ID of the user to delete
     * @return A message indicating whether the user was deleted or not and a status of OK (200) or BAD_REQUEST (400)
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
        }
        // User ID does not exist, return messages and a status of BAD_REQUEST (400)
        return new ResponseEntity<>("User not found.", HttpStatus.BAD_REQUEST);
    }
}
