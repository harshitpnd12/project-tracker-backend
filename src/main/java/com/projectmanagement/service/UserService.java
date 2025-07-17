package com.projectmanagement.service;

import com.projectmanagement.model.User;

public interface UserService {

    User findUserProfileByJwt(String jwt) throws Exception;

    User findUserByEmail(String email) throws Exception;

    User findUserById(Long UserId) throws Exception;

    User updateProjectSize(User user, int number);
}

