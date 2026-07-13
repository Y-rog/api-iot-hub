package com.iot.api.mapper;

import com.iot.api.dto.DataPointDTO;
import com.iot.history.model.DataPoint;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DataPointMapper {

    DataPointDTO toDTO (DataPoint dataPoint);

    DataPoint toEntity (DataPointDTO dataPointDTO);

}
