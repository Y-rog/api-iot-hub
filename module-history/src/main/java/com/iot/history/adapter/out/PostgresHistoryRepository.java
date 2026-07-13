package com.iot.history.adapter.out;

import com.iot.history.model.DataPoint;
import com.iot.history.port.out.HistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostgresHistoryRepository implements HistoryRepository {

    private final HistoryJpaRepository historyJpaRepository;

    public PostgresHistoryRepository(HistoryJpaRepository historyJpaRepository) {
        this.historyJpaRepository = historyJpaRepository;
    }


    @Override
    public List<DataPoint> findByDeviceId(String deviceId) {
        return historyJpaRepository.findByDeviceId(deviceId);
    }

    @Override
    public DataPoint createDataPoint(DataPoint dataPoint) {
        return historyJpaRepository.save(dataPoint);
    }
}
