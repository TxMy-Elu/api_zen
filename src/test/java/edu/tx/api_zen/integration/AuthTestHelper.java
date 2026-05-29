package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String adminToken;
    private String userToken;

    public AuthTestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public String getAdminToken() throws Exception {
        if (adminToken == null) {
            adminToken = login("admin@cesizen.fr", "password");
        }
        return adminToken;
    }

    public String getUserToken() throws Exception {
        if (userToken == null) {
            userToken = login("user@cesizen.fr", "password");
        }
        return userToken;
    }

    private String login(String email, String password) throws Exception {
        String loginJson = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                email, password
        );

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }
}
