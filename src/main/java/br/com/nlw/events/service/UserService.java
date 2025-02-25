package br.com.nlw.events.service;

import br.com.nlw.events.model.User;
import br.com.nlw.events.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User addNewUser(User user) {
        return userRepo.save(user);
    }

    public Optional<User> getUserById(Integer userId) {
        return userRepo.findById(userId);
    }
}
