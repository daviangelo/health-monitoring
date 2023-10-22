package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.HeartRateRecord;
import com.lessa.healthmonitoring.persistence.repository.HeartRateRepository;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeartRateServiceImpl implements HeartRateService {

    private final HeartRateRepository heartRateRepository;
    private final UserRepository userRepository;

    @Override
    public HeartRateRecord recordHeartRate(Long userId, HeartRateRecord heartRateRecord) {
        return null;
    }

    @Override
    public List<HeartRateRecord> getHeartRateRecordsPerDay(Long userId, LocalDate date) {
        return null;
    }

    @Override
    public boolean delete(Long heartRateRecordId) {

        return false;
    }
}
