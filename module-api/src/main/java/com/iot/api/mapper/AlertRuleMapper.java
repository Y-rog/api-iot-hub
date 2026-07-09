package com.iot.api.mapper;

import com.iot.alerts.model.AlertRule;
import com.iot.api.dto.AlertDTO;
import com.iot.api.dto.AlertRuleDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AlertRuleMapper {
    AlertRuleDTO toDTO(AlertRule alertRule);
    AlertRule toEntity(AlertRuleDTO alertRuleDTO);

}
