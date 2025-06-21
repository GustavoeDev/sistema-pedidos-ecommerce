package br.com.gustavoedev.order_processings_service.modules.processings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {

    private UUID id;
    private String status;
    private LocalDateTime createdAt;

}
