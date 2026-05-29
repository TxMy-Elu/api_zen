package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tx.api_zen.dto.ExercerCreateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ExercerControllerIntegrationTest {

    @Autowired private WebApplicationContext context;
    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private AuthTestHelper authHelper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        objectMapper = new ObjectMapper();
        authHelper = new AuthTestHelper(mvc, objectMapper);
    }

    @Test
    void testListExercersPublic() throws Exception {
        mvc.perform(get("/api/exercer/list"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListExercersWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/exercer/list")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testListPublicExercersWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/exercer/public")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByUserIdWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/exercer/user/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByExerciceIdWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/exercer/exercice/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateExercerWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        ExercerCreateDto dto = new ExercerCreateDto();
        dto.setUserId(1);
        dto.setExerciceId(1);
        mvc.perform(post("/api/exercer")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteExercerPublic() throws Exception {
        mvc.perform(delete("/api/exercer/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteExercerWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(delete("/api/exercer/999999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetByIdWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/exercer/999999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
