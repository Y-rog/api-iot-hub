package com.iot.alerts.adapter.out;

import com.iot.alerts.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscription, UUID> {
}
