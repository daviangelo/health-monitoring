package com.lessa.healthmonitoring.persistence.repository;

import com.lessa.healthmonitoring.persistence.entity.HeartRateRecordEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface HeartRateRepository extends CrudRepository<HeartRateRecordEntity, Long> {

    List<HeartRateRecordEntity> findAllByUserIdAndRecordDateBetween(Long userId, Instant recordDateStart, Instant recordDateEnd);

}
