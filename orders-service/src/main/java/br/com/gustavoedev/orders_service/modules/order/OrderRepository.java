package br.com.gustavoedev.orders_service.modules.order;

import br.com.gustavoedev.orders_service.modules.order.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
