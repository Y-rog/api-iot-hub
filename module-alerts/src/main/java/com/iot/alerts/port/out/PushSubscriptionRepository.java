package com.iot.alerts.port.out;

import com.iot.alerts.model.PushSubscription;
import java.util.List;

public interface PushSubscriptionRepository {
    PushSubscription save(PushSubscription subscription);
    List<PushSubscription> findAll();
}
