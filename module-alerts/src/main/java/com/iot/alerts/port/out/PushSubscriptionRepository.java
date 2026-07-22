package com.iot.alerts.port.out;

import com.iot.alerts.model.PushSubscription;
import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository {
    PushSubscription save(PushSubscription subscription);
    List<PushSubscription> findAll();
    Optional<PushSubscription> findByEndpoint(String endpoint);
}