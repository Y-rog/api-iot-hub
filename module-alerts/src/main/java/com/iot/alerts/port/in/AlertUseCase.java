package com.iot.alerts.port.in;

import com.iot.alerts.model.Alert;
import java.util.List;
import java.util.Optional;

public interface AlertUseCase {
    Alert createAlert(Alert alert);
    List<Alert> getAllAlerts();
    List<Alert> getRecentAlerts(int limit);
    Optional<Alert> markAsRead(String id);
}
