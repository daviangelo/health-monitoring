package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.User;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
