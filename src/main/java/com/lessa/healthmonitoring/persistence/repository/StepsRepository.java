package com.lessa.healthmonitoring.persistence.repository;

import com.lessa.healthmonitoring.persistence.entity.StepsRecordEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface StepsRepository extends CrudRepository<StepsRecordEntity, Long> {

    List<StepsRecordEntity> findAllByUserIdAndRecordDateBetween(Long userId, Instant recordDateStart, Instant recordDateEnd);

}
