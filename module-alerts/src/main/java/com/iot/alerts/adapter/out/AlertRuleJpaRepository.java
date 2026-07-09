package com.iot.alerts.adapter.out;

import com.iot.alerts.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRuleJpaRepository extends JpaRepository <AlertRule, String> {
}
