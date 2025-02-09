package com.examtest.demo;

import com.examtest.demo.dto.userorder.UserOrderRequestDto;
import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.mapper.UserOrderMapper;
import com.examtest.demo.model.UserOrder;
import com.examtest.demo.repository.UserOrderRepository;
import com.examtest.demo.service.impl.UserOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserOrderServiceImplTest {

    @InjectMocks
    private UserOrderServiceImpl userOrderService;

    @Mock
    private UserOrderRepository userOrderRepository;

    @Mock
    private UserOrderMapper userOrderMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUserOrders_ShouldReturnListOfUserOrders() {
        List<UserOrder> userOrders = Stream.of(new UserOrder(), new UserOrder()).collect(Collectors.toList());
        when(userOrderRepository.findAll()).thenReturn(userOrders);
        when(userOrderMapper.toDto(any())).thenReturn(new UserOrderResponseDto());

        List<UserOrderResponseDto> result = userOrderService.getAllUserOrders();

        assertEquals(2, result.size());
        verify(userOrderRepository).findAll();
    }

    @Test
    void addUserOrder_ShouldSaveUserOrder() {
        UserOrderRequestDto userOrderRequestDto = new UserOrderRequestDto();
        UserOrder userOrder = new UserOrder();
        when(userOrderMapper.toModel(userOrderRequestDto)).thenReturn(userOrder);
        when(userOrderRepository.save(userOrder)).thenReturn(userOrder);
        when(userOrderMapper.toDto(userOrder)).thenReturn(new UserOrderResponseDto());

        UserOrderResponseDto result = userOrderService.addUserOrder(userOrderRequestDto);

        assertNotNull(result);
        verify(userOrderRepository).save(userOrder);
    }

    @Test
    void getUserOrderById_ShouldReturnUserOrder_WhenFound() {
        UUID id = UUID.randomUUID();
        UserOrder userOrder = new UserOrder();
        when(userOrderRepository.findById(id)).thenReturn(Optional.of(userOrder));
        when(userOrderMapper.toDto(userOrder)).thenReturn(new UserOrderResponseDto());

        UserOrderResponseDto result = userOrderService.getUserOrderById(id);

        assertNotNull(result);
        verify(userOrderRepository).findById(id);
    }

    @Test
    void getUserOrderById_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userOrderService.getUserOrderById(id));
        verify(userOrderRepository).findById(id);
    }

    @Test
    void deleteUserOrder_ShouldDeleteUserOrder_WhenFound() {
        UUID id = UUID.randomUUID();
        UserOrder userOrder = new UserOrder();
        when(userOrderRepository.findById(id)).thenReturn(Optional.of(userOrder));

        userOrderService.deleteUserOrder(id);

        verify(userOrderRepository).delete(userOrder);
    }

    @Test
    void updateUserOrder_ShouldUpdateUserOrder_WhenFound() {
        UUID id = UUID.randomUUID();
        UserOrderRequestDto userOrderRequestDto = new UserOrderRequestDto();
        UserOrder existingUserOrder = new UserOrder();
        UserOrder updatedUserOrder = new UserOrder();

        when(userOrderRepository.findById(id)).thenReturn(Optional.of(existingUserOrder));
        when(userOrderMapper.toModel(userOrderRequestDto)).thenReturn(updatedUserOrder);
        when(userOrderRepository.save(updatedUserOrder)).thenReturn(updatedUserOrder);
        when(userOrderMapper.toDto(updatedUserOrder)).thenReturn(new UserOrderResponseDto());

        UserOrderResponseDto result = userOrderService.updateUserOrder(id, userOrderRequestDto);

        assertNotNull(result);
        verify(userOrderRepository).save(updatedUserOrder);
    }

    @Test
    void updateUserOrder_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        UserOrderRequestDto userOrderRequestDto = new UserOrderRequestDto();

        when(userOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userOrderService.updateUserOrder(id, userOrderRequestDto));
        verify(userOrderRepository, never()).save(any());
    }
}
