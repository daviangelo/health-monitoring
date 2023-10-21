package com.lessa.healthmonitoring.persistence.repository;

import com.lessa.healthmonitoring.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
