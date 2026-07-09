package com.iot.alerts.port.out;

import com.iot.alerts.model.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {

    Alert saveAlert(Alert alert);

    List<Alert> findAll();

    Optional<Alert> markAsRead(String id);
}
