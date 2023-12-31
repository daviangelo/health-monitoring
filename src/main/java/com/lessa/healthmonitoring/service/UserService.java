package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User create(User user);

    Page<User> getUsers(Pageable pageable);

    Optional<User> findById(Long id);

    Optional<User> update(Long userId, User user);

    boolean delete(Long userId);

}
