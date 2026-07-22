package com.iot.alerts.adapter.out;

import com.iot.alerts.model.PushSubscription;
import com.iot.alerts.port.out.PushSubscriptionRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PostgresPushSubscriptionRepository implements PushSubscriptionRepository {

    private final PushSubscriptionJpaRepository jpaRepository;

    public PostgresPushSubscriptionRepository(PushSubscriptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PushSubscription save(PushSubscription subscription) {
        return jpaRepository.save(subscription);
    }

    @Override
    public List<PushSubscription> findAll() {
        return jpaRepository.findAll();
    }
}

