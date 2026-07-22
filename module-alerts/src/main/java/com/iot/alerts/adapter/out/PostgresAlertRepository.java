package com.iot.alerts.adapter.out;

import com.iot.alerts.model.Alert;
import com.iot.alerts.port.out.AlertRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostgresAlertRepository implements AlertRepository {

    private final AlertJpaRepository alertJpaRepository;

    public PostgresAlertRepository(AlertJpaRepository alertJpaRepository) {
        this.alertJpaRepository = alertJpaRepository;
    }

    @Override
    public Alert saveAlert(Alert alert) {
        return alertJpaRepository.save(alert);
    }

    @Override
    public List<Alert> findAll() {
        return alertJpaRepository.findAll();
    }

    @Override
    public List<Alert> findRecent(int limit) {
        return alertJpaRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    @Override
    public Optional<Alert> markAsRead(String id) {
        Optional<Alert> optAlert = alertJpaRepository.findById(id);
        return optAlert.map(alert -> {
            alert.setRead(true);
            return alertJpaRepository.save(alert);
        });
    }

    @Override
    public boolean existsRecentAlert(String deviceId, String sensorType, LocalDateTime since) {
        return alertJpaRepository.existsByDeviceIdAndSensorTypeAndCreatedAtAfter(
                deviceId, sensorType, since
        );
    }
}
