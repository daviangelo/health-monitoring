package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.User;
import com.lessa.healthmonitoring.persistence.entity.UserEntity;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @CacheEvict(value = "userCache", allEntries = true)
    @Transactional
    @Override
    public User create(User user) {
        return userRepository.save(UserEntity.toEntity(user)).toDomain();
    }


    @Cacheable(value = "userCache")
    @Override
    public Page<User> getUsers(Pageable pageable) {
        return toPageDomain(userRepository.findAll(pageable));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(UserEntity::toDomain);
    }


    @CacheEvict(value = "userCache", allEntries = true)
    @Transactional
    @Override
    public Optional<User> update(Long userId, User user) {
        var maybeUser = userRepository.findById(userId);

        if (maybeUser.isPresent()) {
            var userEntity = maybeUser.get();

            userEntity.setName(user.name());
            userEntity.setDateOfBirth(user.dateOfBirth());

            var updatedUser = userRepository.save(userEntity).toDomain();
            return Optional.of(updatedUser);
        }

        return Optional.empty();

    }

    @CacheEvict(value = "userCache", allEntries = true)
    @Transactional
    @Override
    public boolean delete(Long userId) {
        var maybeUser = userRepository.findById(userId);
        if(maybeUser.isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    private Page<User> toPageDomain(Page<UserEntity> pageEntity) {
        return pageEntity.map(UserEntity::toDomain);
    }

}
