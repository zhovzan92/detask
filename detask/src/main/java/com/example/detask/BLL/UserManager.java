package com.example.detask.BLL;

import com.example.detask.BE.User;
import com.example.detask.DLL.UserDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManager {

    private final UserDAO repo;

    public UserManager(UserDAO repo) {
        this.repo = repo;
    }

    public User login(String username, String password) {
        User u = repo.getByUsername(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    public User getById(int id) {
        return repo.getById(id);
    }

    // NEW
    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }
}