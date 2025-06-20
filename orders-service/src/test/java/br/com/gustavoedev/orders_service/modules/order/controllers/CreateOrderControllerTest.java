package br.com.gustavoedev.orders_service.modules.order.controllers;

import br.com.gustavoedev.orders_service.modules.order.dto.OrderCreateDTO;
import br.com.gustavoedev.orders_service.modules.order.dto.OrderItemDTO;
import br.com.gustavoedev.orders_service.utils.TestUtils;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class CreateOrderControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void should_be_able_to_create_order() throws Exception {
        var item1 = new OrderItemDTO(UUID.randomUUID(), 1, new BigDecimal(5));
        var item2 = new OrderItemDTO(UUID.randomUUID(), 2, new BigDecimal(10));

        var items = List.of(item1, item2);

        var orderCreateDTO = OrderCreateDTO.builder()
                .items(items)
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/order/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.objectToJSON(orderCreateDTO))
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID()))))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

}
