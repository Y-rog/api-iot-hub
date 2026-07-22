package com.iot.alerts.adapter.out;

import com.iot.alerts.model.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertJpaRepository extends JpaRepository<Alert, String> {

    boolean existsByDeviceIdAndSensorTypeAndCreatedAtAfter(
            String deviceId,
            String sensorType,
            LocalDateTime since
    );

    List<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
