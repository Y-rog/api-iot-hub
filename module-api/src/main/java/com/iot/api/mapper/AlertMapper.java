package com.iot.api.mapper;

import com.iot.alerts.model.Alert;
import com.iot.api.dto.AlertDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    AlertDTO toDTO(Alert alert);
    Alert toEntity (AlertDTO alertDTO);
}
