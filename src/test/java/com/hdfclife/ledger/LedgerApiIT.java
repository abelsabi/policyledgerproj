package com.hdfclife.ledger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LedgerApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void getPoliciesSeedContract() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body).hasSize(6);
        assertThat(body.get(0).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1001");
        assertThat(body.get(5).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1006");
    }

    @Test
    @Order(2)
    void getPolicy1004() throws Exception {
        mockMvc.perform(get("/api/policies/HDFC-LIFE-1004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Vikram Singh"))
                .andExpect(jsonPath("$.email").value("vikram.singh@hdfclife.example"))
                .andExpect(jsonPath("$.basePremium").value(15200))
                .andExpect(jsonPath("$.riderCodes").isEmpty());
    }

    @Test
    @Order(3)
    void getPolicy1001Riders() throws Exception {
        mockMvc.perform(get("/api/policies/HDFC-LIFE-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riderCodes[0]").value("ACCIDENT_COVER"))
                .andExpect(jsonPath("$.riderCodes[1]").value("WAIVER_OF_PREMIUM"));
    }

    @Test
    @Order(4)
    void filtersAndSearch() throws Exception {
        MvcResult active = mockMvc.perform(get("/api/policies").param("status", "Active"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(active.getResponse().getContentAsString())).hasSize(4);

        MvcResult term = mockMvc.perform(get("/api/policies").param("type", "TERM"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode termBody = objectMapper.readTree(term.getResponse().getContentAsString());
        assertThat(termBody.get(0).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1001");
        assertThat(termBody.get(1).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1004");

        MvcResult anita = mockMvc.perform(get("/api/policies").param("customer", "Anita Sharma"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode anitaBody = objectMapper.readTree(anita.getResponse().getContentAsString());
        assertThat(anitaBody).hasSize(2);
        assertThat(anitaBody.get(0).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1001");
        assertThat(anitaBody.get(1).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1006");

        MvcResult search = mockMvc.perform(get("/api/policies/search").param("minPremium", "20000"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode searchBody = objectMapper.readTree(search.getResponse().getContentAsString());
        assertThat(searchBody.get(0).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1002");
        assertThat(searchBody.get(1).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1005");
        assertThat(searchBody.get(2).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1003");
        assertThat(searchBody.get(3).get("policyNo").asText()).isEqualTo("HDFC-LIFE-1006");
    }

    @Test
    @Order(5)
    void missingPolicy404() throws Exception {
        mockMvc.perform(get("/api/policies/HDFC-LIFE-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Policy not found: HDFC-LIFE-9999"))
                .andExpect(jsonPath("$.fields").isEmpty());
    }

    @Test
    @Order(6)
    void validationErrors() throws Exception {
        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "policyNo": "HDFC-LIFE-1099",
                                  "customer": "Kiran Rao",
                                  "email": "not-an-email",
                                  "type": "TERM",
                                  "basePremium": 20000,
                                  "status": "Active"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.email").exists());

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "policyNo": "HDFC-LIFE-1098",
                                  "customer": "Kiran Rao",
                                  "email": "kiran.rao@hdfclife.example",
                                  "type": "FOO",
                                  "basePremium": 20000,
                                  "status": "Active"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.type").exists());
    }

    @Test
    @Order(7)
    void createDuplicateDelete1007() throws Exception {
        String body = """
                {
                  "policyNo": "HDFC-LIFE-1007",
                  "customer": "Kiran Rao",
                  "email": "kiran.rao@hdfclife.example",
                  "type": "TERM",
                  "basePremium": 20000,
                  "status": "Active"
                }
                """;
        mockMvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/policies/HDFC-LIFE-1007")));

        mockMvc.perform(post("/api/policies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("HDFC-LIFE-1007")));

        mockMvc.perform(delete("/api/policies/HDFC-LIFE-1007"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(8)
    void claimsContract() throws Exception {
        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "policyNo": "HDFC-LIFE-1001",
                                  "claimAmount": 25000,
                                  "urgency": "HIGH",
                                  "hospitalName": "Apollo Hospital"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/claims/CLM-01")))
                .andExpect(jsonPath("$.claimNo").value("CLM-01"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "policyNo": "HDFC-LIFE-1001",
                                  "claimAmount": 0,
                                  "urgency": "HIGH"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.claimAmount").exists());

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "policyNo": "HDFC-LIFE-1001",
                                  "claimAmount": 600000,
                                  "urgency": "HIGH"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.claimAmount").exists());

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "policyNo": "HDFC-LIFE-9999",
                                  "claimAmount": 1000,
                                  "urgency": "LOW"
                                }
                                """))
                .andExpect(status().isNotFound());

        MvcResult claims = mockMvc.perform(get("/api/policies/HDFC-LIFE-1001/claims"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(claims.getResponse().getContentAsString());
        assertThat(body).hasSize(1);
        assertThat(body.get(0).get("claimNo").asText()).isEqualTo("CLM-01");
    }

    @Test
    @Order(9)
    void swaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
