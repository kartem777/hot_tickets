package com.examtest.demo.repository;

import com.examtest.demo.model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserOrderRepository extends JpaRepository<UserOrder, UUID> {}