package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tx.api_zen.dto.ConsulterCreateDto;
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
class ConsulterControllerIntegrationTest {

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
    void testListConsultationsPublic() throws Exception {
        mvc.perform(get("/api/consulter/list")).andExpect(status().isForbidden());
    }

    @Test
    void testListConsultationsWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/consulter/list")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    org.junit.jupiter.api.Assertions.assertTrue(content.startsWith("["), "Response should be a JSON array");
                });
    }

    @Test
    void testGetByUserIdPublic() throws Exception {
        mvc.perform(get("/api/consulter/user/1")).andExpect(status().isForbidden());
    }

    @Test
    void testGetByUserIdWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/consulter/user/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByArticleIdPublic() throws Exception {
        mvc.perform(get("/api/consulter/article/1")).andExpect(status().isForbidden());
    }

    @Test
    void testGetByArticleIdWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/consulter/article/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCountViewsByArticlePublic() throws Exception {
        mvc.perform(get("/api/consulter/article/1/count")).andExpect(status().isForbidden());
    }

    @Test
    void testCountViewsByArticleWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/consulter/article/1/count")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCountByUserPublic() throws Exception {
        mvc.perform(get("/api/consulter/user/1/count")).andExpect(status().isForbidden());
    }

    @Test
    void testCountByUserWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/consulter/user/1/count")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateConsultationPublic() throws Exception {
        ConsulterCreateDto dto = new ConsulterCreateDto();
        dto.setIdUtilisateur(1);
        dto.setIdArticle(1);
        mvc.perform(post("/api/consulter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateConsultationWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        ConsulterCreateDto dto = new ConsulterCreateDto();
        dto.setIdUtilisateur(1);
        dto.setIdArticle(1);
        mvc.perform(post("/api/consulter")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    com.fasterxml.jackson.databind.JsonNode json = objectMapper.readTree(content);
                    org.junit.jupiter.api.Assertions.assertNotNull(json.get("idConsulter"), "Consultation ID (idConsulter) should be returned");
                });
    }

    @Test
    void testDeleteConsultationPublic() throws Exception {
        mvc.perform(delete("/api/consulter/1")).andExpect(status().isForbidden());
    }

    @Test
    void testDeleteConsultationWithAuth() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(delete("/api/consulter/999999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
