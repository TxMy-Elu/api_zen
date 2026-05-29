package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tx.api_zen.dto.UserCreateDto;
import edu.tx.api_zen.dto.UserUpdateDto;
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
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private ObjectMapper objectMapper;
    private AuthTestHelper authHelper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        authHelper = new AuthTestHelper(mvc, objectMapper);
    }

    @Test
    void testListUsersWithoutAuth() throws Exception {
        mvc.perform(get("/api/user/list")).andExpect(status().isForbidden());
    }

    @Test
    void testListUsersWithUserToken() throws Exception {
        String token = authHelper.getUserToken();
        mvc.perform(get("/api/user/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListUsersWithAdminToken() throws Exception {
        String token = authHelper.getAdminToken();
        mvc.perform(get("/api/user/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    Assertions.assertTrue(content.startsWith("["), "Response should be a JSON array");
                    Assertions.assertTrue(content.length() > 2, "Array should not be empty");
                });
    }

    @Test
    void testGetUserByIdWithAdminToken() throws Exception {
        String token = authHelper.getAdminToken();
        mvc.perform(get("/api/user/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    Assertions.assertTrue(status == 200 || status == 404,
                        "Expected 200 (user exists) or 404 (user not found), got " + status);
                });
    }

    @Test
    void testCreateUserWithoutAuth() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setNom("Test");
        dto.setPrenom("User");
        dto.setEmail("test.no.auth@test.com");
        dto.setPassword("password123");
        dto.setRoleId(2);
        mvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUserWithUserToken() throws Exception {
        String token = authHelper.getUserToken();
        UserCreateDto dto = new UserCreateDto();
        dto.setNom("Test");
        dto.setPrenom("User");
        dto.setEmail("test.as.user@test.com");
        dto.setPassword("password123");
        dto.setRoleId(2);
        mvc.perform(post("/api/user")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUserWithAdminToken() throws Exception {
        String token = authHelper.getAdminToken();
        UserCreateDto dto = new UserCreateDto();
        String uniqueEmail = "test.user." + System.currentTimeMillis() + "@test.com";
        dto.setNom("TestUser");
        dto.setPrenom("Integration");
        dto.setEmail(uniqueEmail);
        dto.setPassword("SecurePassword123!");
        dto.setRoleId(2);

        mvc.perform(post("/api/user")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JsonNode json = objectMapper.readTree(content);
                    Assertions.assertNotNull(json.get("id"), "User ID should be returned");
                    Assertions.assertEquals(uniqueEmail, json.get("email").asText(), "Email should match");
                    Assertions.assertEquals("TestUser", json.get("nom").asText(), "Nom should match");
                });
    }

    @Test
    void testUpdateUserWithAdminToken() throws Exception {
        String token = authHelper.getAdminToken();
        UserUpdateDto dto = new UserUpdateDto();
        dto.setNom("UpdatedName");
        mvc.perform(put("/api/user/999999")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserWithAdminToken() throws Exception {
        String token = authHelper.getAdminToken();
        mvc.perform(delete("/api/user/999999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
