package br.com.onebrain.couponapi;

import br.com.onebrain.couponapi.dto.CreateCouponRequest;
import br.com.onebrain.couponapi.dto.UpdateCouponRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CouponIntegrationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules();
    }

    @Test
    void createAndGetAndDeleteFlow() throws Exception {
        CreateCouponRequest req = new CreateCouponRequest();
        req.setCode("ab12cd");
        req.setDescription("Test create");
        req.setDiscountValue(java.math.BigDecimal.valueOf(1.0));
        req.setExpirationDate(LocalDate.now().plusDays(10));
        req.setPublished(true);

        String createBody = mvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode created = mapper.readTree(createBody);
        String code = created.get("code").asText();
        assertThat(code).hasSize(6);

        mvc.perform(get("/api/coupons/" + code)).andExpect(status().isOk());

        mvc.perform(delete("/api/coupons/" + code)).andExpect(status().isNoContent());

        mvc.perform(delete("/api/coupons/" + code)).andExpect(status().isConflict());
    }

    @Test
    void createWithPastExpirationShouldFail() throws Exception {
        CreateCouponRequest req = new CreateCouponRequest();
        req.setCode("ABC123");
        req.setDescription("past");
        req.setDiscountValue(java.math.BigDecimal.valueOf(1.0));
        req.setExpirationDate(LocalDate.now().minusDays(1));

        mvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFlow() throws Exception {
        CreateCouponRequest req = new CreateCouponRequest();
        req.setCode("UPD001");
        req.setDescription("to update");
        req.setDiscountValue(java.math.BigDecimal.valueOf(2.0));
        req.setExpirationDate(LocalDate.now().plusDays(20));

        String createBody = mvc.perform(post("/api/coupons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode created = mapper.readTree(createBody);
        String code = created.get("code").asText();

        UpdateCouponRequest up = new UpdateCouponRequest();
        up.setDescription("updated");
        up.setDiscountValue(java.math.BigDecimal.valueOf(5.0));
        up.setExpirationDate(LocalDate.now().plusDays(30));

        String resp = mvc.perform(put("/api/coupons/" + code)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(up)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode updated = mapper.readTree(resp);
        assertThat(updated.get("description").asText()).isEqualTo("updated");
    }

    @Test
    void listCouponsShouldReturnPage() throws Exception {

        CreateCouponRequest req1 = new CreateCouponRequest();
        req1.setCode("LIST01");
        req1.setDescription("coupon one");
        req1.setDiscountValue(java.math.BigDecimal.valueOf(10.0));
        req1.setExpirationDate(LocalDate.now().plusDays(5));
        req1.setPublished(true);

        CreateCouponRequest req2 = new CreateCouponRequest();
        req2.setCode("LIST02");
        req2.setDescription("coupon two");
        req2.setDiscountValue(java.math.BigDecimal.valueOf(20.0));
        req2.setExpirationDate(LocalDate.now().plusDays(10));
        req2.setPublished(true);

        mvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req2)))
                .andExpect(status().isCreated());

        String response = mvc.perform(get("/api/coupons?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode page = mapper.readTree(response);

        assertThat(page.get("content").isArray()).isTrue();
        assertThat(page.get("content").size()).isGreaterThanOrEqualTo(2);

        var codes = page.get("content").findValuesAsText("code");
        assertThat(codes).contains("LIST01", "LIST02");
    }

}

