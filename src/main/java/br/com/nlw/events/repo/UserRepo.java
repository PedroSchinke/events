package br.com.nlw.events.repo;

import br.com.nlw.events.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Integer> {
    public User findByEmail(String email);
    public Optional<User> findById(Integer id);
}
