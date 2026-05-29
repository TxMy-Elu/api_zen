package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tx.api_zen.dto.CategorieCreateDto;
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
class CategorieControllerIntegrationTest {

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
    void testListCategoriesPublic() throws Exception {
        mvc.perform(get("/api/categorie/list")).andExpect(status().isOk());
    }

    @Test
    void testGetCategoryByIdPublic() throws Exception {
        mvc.perform(get("/api/categorie/1")).andExpect(status().isOk());
    }

    @Test
    void testCreateCategoryAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        CategorieCreateDto dto = new CategorieCreateDto();
        dto.setLibelle("Nouvelle Catégorie Test");
        dto.setDescription("Description test");
        mvc.perform(post("/api/categorie")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateCategoryAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        CategorieCreateDto dto = new CategorieCreateDto();
        dto.setLibelle("Updated");
        mvc.perform(put("/api/categorie/999999")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCategoryAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(delete("/api/categorie/999999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
