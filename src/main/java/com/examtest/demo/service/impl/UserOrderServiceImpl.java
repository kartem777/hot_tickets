package com.examtest.demo.service.impl;

import com.examtest.demo.dto.userorder.UserOrderRequestDto;
import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.mapper.UserOrderMapper;
import com.examtest.demo.model.UserOrder;
import com.examtest.demo.repository.UserOrderRepository;
import com.examtest.demo.service.UserOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserOrderServiceImpl implements UserOrderService {

    private final UserOrderRepository userOrderRepository;
    private final UserOrderMapper userOrderMapper;

    @Autowired
    public UserOrderServiceImpl(UserOrderRepository userOrderRepository, UserOrderMapper userOrderMapper) {
        this.userOrderRepository = userOrderRepository;
        this.userOrderMapper = userOrderMapper;
    }

    @Override
    public List<UserOrderResponseDto> getAllUserOrders() {
        List<UserOrder> userOrders = userOrderRepository.findAll();

        return userOrders.stream()
                .map(userOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserOrderResponseDto addUserOrder(UserOrderRequestDto userOrderDto) {
        UserOrder userOrder = userOrderMapper.toModel(userOrderDto);

        UserOrder savedUserOrder = userOrderRepository.save(userOrder);

        return userOrderMapper.toDto(savedUserOrder);
    }

    @Override
    public UserOrderResponseDto getUserOrderById(UUID id) {
        UserOrder userOrder = userOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserOrder not found"));

        return userOrderMapper.toDto(userOrder);
    }
    @Override
    public UserOrder getUserOrderByIdBasic(UUID id) {
        UserOrder userOrder = userOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserOrder not found"));

        return userOrder;
    }

    @Override
    @Transactional
    public void deleteUserOrder(UUID id) {
        UserOrder userOrder = userOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserOrder not found"));

        userOrderRepository.delete(userOrder);
    }

    @Override
    public UserOrderResponseDto updateUserOrder(UUID id, UserOrderRequestDto userOrderDto) {
        Optional<UserOrder> userOrderOpt = userOrderRepository.findById(id);

        if (userOrderOpt.isPresent()) {
            UserOrder userOrder = userOrderOpt.get();

            UserOrder updatedUserOrder = userOrderMapper.toModel(userOrderDto);
            updatedUserOrder.setId(userOrder.getId());
            updatedUserOrder.setUser(userOrder.getUser());

            updatedUserOrder = userOrderRepository.save(updatedUserOrder);

            return userOrderMapper.toDto(updatedUserOrder);
        } else {
            throw new RuntimeException("UserOrder not found");
        }
    }
}
