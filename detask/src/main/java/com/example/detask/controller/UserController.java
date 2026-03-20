package com.example.detask.controller;

import com.example.detask.BE.User;
import com.example.detask.BLL.UserManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userManager.login(username, password);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "UNAUTHORIZED", "message", "Invalid credentials"));
        }

        return ResponseEntity.ok(Map.of(
                "accessToken", "dev-token-" + user.getId(),
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                )
        ));
    }


    @GetMapping("/ping")
    public String ping() { return "OK"; }



    // GET /api/v1/auth/users/1
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        User user = userManager.getById(id);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "NOT_FOUND", "message", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    // GET /api/v1/auth/users  ← NEW — returns all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userManager.getAllUsers());
    }

}