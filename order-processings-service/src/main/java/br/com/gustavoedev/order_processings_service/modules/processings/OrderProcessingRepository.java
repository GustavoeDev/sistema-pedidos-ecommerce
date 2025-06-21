package br.com.gustavoedev.order_processings_service.modules.processings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderProcessingRepository extends JpaRepository<OrderProcessingEntity, UUID> {
    Optional<OrderProcessingEntity> findByOrderId(UUID orderId);
}
