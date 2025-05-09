package com.task.mapper;

import org.mapstruct.Mapper;
import java.sql.Time;

@Mapper(componentModel = "spring")
public interface TimeMapper {
    default Time map(Time time) {
        return time;
    }
}
