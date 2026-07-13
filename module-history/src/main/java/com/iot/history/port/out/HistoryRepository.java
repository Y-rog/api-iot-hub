package com.iot.history.port.out;

import com.iot.history.model.DataPoint;

import java.util.List;

public interface HistoryRepository {

    List<DataPoint> findByDeviceId(String deviceId);

    DataPoint createDataPoint(DataPoint dataPoint);

}
