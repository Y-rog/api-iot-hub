package com.iot.alerts.port.out;

import com.iot.alerts.model.Alert;

public interface NotificationPort {

    void sendNotification(Alert alert);

}
