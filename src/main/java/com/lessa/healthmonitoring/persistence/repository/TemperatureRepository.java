package com.lessa.healthmonitoring.persistence.repository;

import com.lessa.healthmonitoring.persistence.entity.TemperatureRecordEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TemperatureRepository extends CrudRepository<TemperatureRecordEntity, Long> {

    List<TemperatureRecordEntity> findAllByUserIdAndRecordDateBetween(Long userId, Instant recordDateStart, Instant recordDateEnd);
}
