package com.examtest.demo.mapper;

import com.examtest.demo.config.MapperConfig;
import com.examtest.demo.dto.userorder.UserOrderRequestDto;
import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.model.UserOrder;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserOrderMapper {
    UserOrderResponseDto toDto(UserOrder userorder);
    UserOrder toModel(UserOrderRequestDto requestDto);
}
