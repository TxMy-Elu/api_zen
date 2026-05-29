package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tx.api_zen.dto.ExerciceCreateDto;
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
class ExerciceControllerIntegrationTest {

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
    void testListExercicesPublic() throws Exception {
        mvc.perform(get("/api/exercice/list")).andExpect(status().isOk());
    }

    @Test
    void testGetExerciceByIdPublic() throws Exception {
        mvc.perform(get("/api/exercice/1")).andExpect(status().isOk());
    }

    @Test
    void testCreateExerciceWithoutAuth() throws Exception {
        ExerciceCreateDto dto = new ExerciceCreateDto();
        dto.setNom("Test");
        dto.setDureeInspiration(4);
        dto.setDureeApnee(4);
        dto.setDureeExpiration(4);
        mvc.perform(post("/api/exercice").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateExerciceAsUser() throws Exception {
        String userToken = authHelper.getUserToken();
        ExerciceCreateDto dto = new ExerciceCreateDto();
        dto.setNom("Test");
        dto.setDureeInspiration(4);
        dto.setDureeApnee(4);
        dto.setDureeExpiration(4);
        mvc.perform(post("/api/exercice")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateExerciceAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        ExerciceCreateDto dto = new ExerciceCreateDto();
        dto.setNom("Nouvel Exercice");
        dto.setDureeInspiration(4);
        dto.setDureeApnee(4);
        dto.setDureeExpiration(4);
        dto.setDescription("Test exercice");
        mvc.perform(post("/api/exercice")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateExerciceAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        ExerciceCreateDto dto = new ExerciceCreateDto();
        dto.setNom("Updated");
        mvc.perform(put("/api/exercice/999999")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteExerciceAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(delete("/api/exercice/999999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
