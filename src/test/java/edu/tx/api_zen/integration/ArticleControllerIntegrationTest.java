package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tx.api_zen.dto.ArticleCreateDto;
import org.junit.jupiter.api.Assertions;
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
class ArticleControllerIntegrationTest {

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
    void testListArticlesPublic() throws Exception {
        mvc.perform(get("/api/article/list"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Assertions.assertTrue(content.startsWith("["), "Response should be a JSON array");
                });
    }

    @Test
    void testListPublicArticlesPublic() throws Exception {
        mvc.perform(get("/api/article/public"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Assertions.assertTrue(content.startsWith("["), "Response should be a JSON array");
                });
    }

    @Test
    void testGetArticleByIdPublic() throws Exception {
        mvc.perform(get("/api/article/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetArticleByIdIfExists() throws Exception {
        mvc.perform(get("/api/article/1"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 200) {
                        String content = result.getResponse().getContentAsString();
                        JsonNode json = objectMapper.readTree(content);
                        Assertions.assertNotNull(json.get("idArticle"), "Article should have idArticle");
                    } else if (status == 404) {
                        Assertions.assertTrue(true);
                    } else {
                        Assertions.fail("Expected 200 or 404, got " + status);
                    }
                });
    }

    @Test
    void testCreateArticleWithoutAuth() throws Exception {
        ArticleCreateDto dto = new ArticleCreateDto();
        dto.setTitre("Test");
        dto.setTypeMedia("TEXT");
        mvc.perform(post("/api/article").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateArticleAsUser() throws Exception {
        String userToken = authHelper.getUserToken();
        ArticleCreateDto dto = new ArticleCreateDto();
        dto.setTitre("Test User Article");
        dto.setTypeMedia("TEXT");
        dto.setContenu("Contenu test");
        dto.setIdCategorie(1);
        mvc.perform(post("/api/article")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateArticleAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        ArticleCreateDto dto = new ArticleCreateDto();
        String uniqueTitle = "Article Test " + System.currentTimeMillis();
        dto.setTitre(uniqueTitle);
        dto.setTypeMedia("TEXT");
        dto.setContenu("Contenu du nouvel article");
        dto.setIdCategorie(1);

        mvc.perform(post("/api/article")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JsonNode json = objectMapper.readTree(content);
                    Assertions.assertNotNull(json.get("idArticle"), "Article ID (idArticle) should be returned");
                    Assertions.assertEquals(uniqueTitle, json.get("titre").asText(), "Titre should match");
                    Assertions.assertEquals("TEXT", json.get("typeMedia").asText(), "TypeMedia should match");
                });
    }

    @Test
    void testUpdateArticleAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        ArticleCreateDto dto = new ArticleCreateDto();
        String updatedTitle = "Updated Article " + System.currentTimeMillis();
        dto.setTitre(updatedTitle);
        dto.setTypeMedia("TEXT");
        dto.setContenu("Contenu mis à jour");

        mvc.perform(put("/api/article/999999")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteArticleAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(delete("/api/article/999999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
