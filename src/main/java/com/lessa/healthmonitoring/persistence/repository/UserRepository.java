package com.lessa.healthmonitoring.persistence.repository;

import com.lessa.healthmonitoring.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
