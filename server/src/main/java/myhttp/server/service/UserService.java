package myhttp.server.service;

import myhttp.server.entity.User;
import myhttp.server.repository.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository repo; // 유저 저장소

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User getUser(Long id) {
        return repo.find(id);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User createUser(User user) {
        return repo.save(user);
    }

    public User updateUser(User user) {
        return repo.update(user);
    }

    public void deleteUser(Long id) {
        repo.delete(id);
    }
}
