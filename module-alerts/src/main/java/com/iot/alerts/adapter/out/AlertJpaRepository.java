package com.iot.alerts.adapter.out;

import com.iot.alerts.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertJpaRepository extends JpaRepository<Alert, String> {
}
