package com.projectmanagement.service;

import com.projectmanagement.config.JwtProvider;
import com.projectmanagement.model.User;
import com.projectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {

        String email = JwtProvider.getEmailFromToken(jwt);
        return findUserByEmail(email);

    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User Not Found");
        }
        return user;
    }

    @Override
    public User findUserById(Long UserId) throws Exception {

        Optional<User> optionaluser = userRepository.findById(UserId);
        if (optionaluser.isEmpty()) {
            throw new Exception("User Not Found");
        }
        return optionaluser.get();
    }

    @Override
    public User updateProjectSize(User user, int number) {
        user.setProjectSize(user.getProjectSize() + number);
        return userRepository.save(user);
    }

}

