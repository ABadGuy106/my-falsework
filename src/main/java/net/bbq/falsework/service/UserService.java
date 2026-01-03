package net.bbq.falsework.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import net.bbq.falsework.entity.User;
import net.bbq.falsework.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Transactional
    public User createUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        return user;
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    public Optional<User> getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Transactional
    public User updateUser(Long id, String username, String email, String password) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.updateById(user);
        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}
