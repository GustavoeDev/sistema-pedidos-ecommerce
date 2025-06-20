package br.com.gustavoedev.orders_service.modules.order.services;

import br.com.gustavoedev.orders_service.modules.order.OrderRepository;
import br.com.gustavoedev.orders_service.modules.order.dto.OrderCreateDTO;
import br.com.gustavoedev.orders_service.modules.order.dto.OrderItemDTO;
import br.com.gustavoedev.orders_service.modules.order.entities.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateOrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Create order")
    public void create_order() {
        var clientId = UUID.randomUUID();

        List<OrderItemDTO> items = new ArrayList<>();
        var orderItem = new OrderItemDTO();
        orderItem.setProductId(UUID.randomUUID());
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal(5));
        items.add(orderItem);

        var orderEntityDTO = OrderCreateDTO.builder()
                .items(items)
                .build();

        var orderEntity = new OrderEntity();
        orderEntity.setId(UUID.randomUUID());
        orderEntity.setClientId(clientId);

        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        var result = orderService.execute(orderEntityDTO, clientId.toString());

        assertThat(result).hasFieldOrProperty("id");
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test total value calculation")
    public void test_total_value_calculation() {
        var clientId = UUID.randomUUID();

        var item1 = new OrderItemDTO(UUID.randomUUID(), 2, new BigDecimal("10")); // 20
        var item2 = new OrderItemDTO(UUID.randomUUID(), 1, new BigDecimal("5"));  // 5

        var items = List.of(item1, item2);

        var orderEntityDTO = OrderCreateDTO.builder()
                .items(items)
                .build();

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> {
                    OrderEntity saved = invocation.getArgument(0);
                    saved.setId(UUID.randomUUID());
                    return saved;
                });

        var result = orderService.execute(orderEntityDTO, clientId.toString());

        assertThat(result.getTotalValue()).isEqualByComparingTo("25");
        assertThat(result.getItems()).hasSize(2);
    }
}
