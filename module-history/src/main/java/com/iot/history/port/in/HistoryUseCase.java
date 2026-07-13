package com.iot.history.port.in;

import com.iot.history.model.DataPoint;

import java.util.List;

public interface HistoryUseCase {

    List<DataPoint> getHistory(String deviceId);

    DataPoint registerDataPoint(DataPoint dataPoint);


}
