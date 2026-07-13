package com.iot.history.adapter.out;

import com.iot.history.model.DataPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryJpaRepository extends JpaRepository <DataPoint, String> {

    List<DataPoint> findByDeviceId(String deviceId);

}
