package com.website.Backend.controller;

import com.website.Backend.dto.RequestResponse;
import com.website.Backend.entity.OurUsers;
import com.website.Backend.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagmentController {

    @Autowired
    private UsersManagementService usersManagementService;


    @PostMapping("/auth/register")
    public ResponseEntity<RequestResponse> register(@RequestBody RequestResponse register)
    {
        return ResponseEntity.ok(usersManagementService.register(register));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RequestResponse> login(@RequestBody RequestResponse login)
    {
        return ResponseEntity.ok(usersManagementService.login(login));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RequestResponse> refreshToken(@RequestBody RequestResponse req)
    {
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<RequestResponse> getAllUsers()
    {
        return  ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<RequestResponse> getUserById(@PathVariable Integer userId)
    {
        return ResponseEntity.ok(usersManagementService.getUserById(userId));
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<RequestResponse> updateUser(@PathVariable Integer userId, @RequestBody OurUsers ourUsers)
    {
        return ResponseEntity.ok(usersManagementService.updateUser(userId,ourUsers));
    }


    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<RequestResponse>getMyProfile()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        RequestResponse response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<RequestResponse> deleteUser(@PathVariable Integer userId)
    {
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

}
