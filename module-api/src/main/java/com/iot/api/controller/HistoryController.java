package com.iot.api.controller;

import com.iot.api.dto.DataPointDTO;
import com.iot.api.mapper.DataPointMapper;
import com.iot.history.port.in.HistoryUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryUseCase historyUseCase;
    private final DataPointMapper dataPointMapper;

    public HistoryController(HistoryUseCase historyUseCase, DataPointMapper dataPointMapper) {
        this.historyUseCase = historyUseCase;
        this.dataPointMapper = dataPointMapper;
    }


    @GetMapping("/{deviceId}")
    public List<DataPointDTO> getHistory(@PathVariable String deviceId) {
        return historyUseCase.getHistory(deviceId)
                .stream()
                .map(dataPointMapper::toDTO)
                .toList();
    }

}
