package br.com.gustavoedev.orders_service.modules.order.services;

import br.com.gustavoedev.orders_service.modules.order.OrderRepository;
import br.com.gustavoedev.orders_service.modules.order.dto.OrderCreateDTO;
import br.com.gustavoedev.orders_service.modules.order.entities.OrderEntity;
import br.com.gustavoedev.orders_service.modules.order.entities.OrderItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public OrderEntity execute(OrderCreateDTO orderCreateDTO) {

        OrderEntity order = new OrderEntity();
        order.setClientId(orderCreateDTO.getClientId());
        order.setStatus("PROCESSING");

        List<OrderItemEntity> items = orderCreateDTO.getItems().stream().map(itemDTO -> {
            OrderItemEntity newItem = new OrderItemEntity();
            newItem.setProductId(itemDTO.getProductId());
            newItem.setQuantity(itemDTO.getQuantity());
            newItem.setUnitPrice(itemDTO.getUnitPrice());
            newItem.setOrder(order);
            return newItem;
        }).collect(Collectors.toList());

        BigDecimal totalValue = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalValue(totalValue);

        order.setItems(items);

        return this.orderRepository.save(order);
    }

}
