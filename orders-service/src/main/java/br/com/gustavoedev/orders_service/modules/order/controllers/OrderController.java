package br.com.gustavoedev.orders_service.modules.order.controllers;

import br.com.gustavoedev.orders_service.modules.order.dto.OrderCreateDTO;
import br.com.gustavoedev.orders_service.modules.order.entities.OrderEntity;
import br.com.gustavoedev.orders_service.modules.order.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody OrderCreateDTO orderCreateDTO,
                                         @AuthenticationPrincipal Jwt jwt) {
        try {
            String clientId = jwt.getSubject();
            var result = orderService.execute(orderCreateDTO, clientId);
            return ResponseEntity.status(201).body(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
