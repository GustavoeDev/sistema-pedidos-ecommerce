package br.com.gustavoedev.orders_service.modules.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {

    @NotEmpty(message = "A lista de itens do pedido não pode estar vazia.")
    private List<OrderItemDTO> items = new ArrayList<>();

}
