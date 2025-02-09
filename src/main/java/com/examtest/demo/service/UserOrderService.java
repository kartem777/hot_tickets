package com.examtest.demo.service;

import com.examtest.demo.dto.userorder.UserOrderRequestDto;
import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.model.UserOrder;

import java.util.List;
import java.util.UUID;

public interface UserOrderService {
    List<UserOrderResponseDto> getAllUserOrders();
    UserOrderResponseDto addUserOrder(UserOrderRequestDto userOrderDto);
    UserOrderResponseDto getUserOrderById(UUID id);
    void deleteUserOrder(UUID id);
    UserOrderResponseDto updateUserOrder(UUID id, UserOrderRequestDto userOrderDto);
    UserOrder getUserOrderByIdBasic(UUID id);
}
