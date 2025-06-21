package br.com.gustavoedev.order_processings_service.messaging;

import br.com.gustavoedev.order_processings_service.modules.processings.OrderProcessingEntity;
import br.com.gustavoedev.order_processings_service.modules.processings.OrderProcessingRepository;
import br.com.gustavoedev.order_processings_service.modules.processings.dto.OrderEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderCreatedListener {

    @Autowired
    private OrderProcessingRepository orderProcessingRepository;

    @RabbitListener(queues = "orders.order-created.order-processings")
    public void onOrderCreated(OrderEventDTO event) {
        OrderProcessingEntity processing = orderProcessingRepository.findByOrderId(event.getId())
                .orElse(new OrderProcessingEntity());

        if ("COMPLETED".equals(processing.getStatus())) {
            return;
        }

        if (processing.getId() == null) {
            processing.setOrderId(event.getId());
            processing.setAttempts(0);
        }

        processing.setAttempts(processing.getAttempts() + 1);
        processing.setLastAttemptAt(LocalDateTime.now());
        processing.setStatus("PROCESSING");

        try {
            orderProcessingRepository.save(processing);

            processOrder(event);

            processing.setStatus("COMPLETED");
            processing.setProcessedAt(LocalDateTime.now());
            processing.setLastError(null);
            orderProcessingRepository.save(processing);

        } catch (Exception e) {
            processing.setStatus("FAILED");
            processing.setLastError(e.getMessage());
            orderProcessingRepository.save(processing);

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new RuntimeException("Erro no processamento da ordem " + event.getId(), e);
        }
    }

    private void processOrder(OrderEventDTO event) {
        if (!isExternalServiceAvailable()) {
            throw new RuntimeException("Serviço externo indisponível");
        }

        if (event.getId() == null) {
            throw new RuntimeException("ID do pedido inválido");
        }

        if (isDatabaseConnectionFailed()) {
            throw new RuntimeException("Falha na conexão com banco de dados");
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processamento interrompido", e);
        }
    }

    private boolean isExternalServiceAvailable() {
        return Math.random() > 0.2;
    }

    private boolean isDatabaseConnectionFailed() {
        return Math.random() < 0.05;
    }
}