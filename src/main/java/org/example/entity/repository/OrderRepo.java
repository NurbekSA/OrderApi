package org.example.entity.repository;

import org.example.entity.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<OrderModel, String> {
    public Optional<OrderModel> findByUserId(String id);
    public Optional<OrderModel> findByBoxId(String id);
}
