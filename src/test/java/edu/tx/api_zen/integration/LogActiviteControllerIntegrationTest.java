package edu.tx.api_zen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class LogActiviteControllerIntegrationTest {

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
    void testListRecentActivityWithoutAuth() throws Exception {
        mvc.perform(get("/api/log-activite/recent")).andExpect(status().isForbidden());
    }

    @Test
    void testListRecentActivityAsUser() throws Exception {
        String userToken = authHelper.getUserToken();
        mvc.perform(get("/api/log-activite/recent")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListRecentActivityAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/log-activite/recent")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testListAllActivityAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/log-activite/list")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByUserIdAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/log-activite/user/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByTableAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/log-activite/table/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByActionTypeAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/log-activite/action/CREATE")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByTableAndIdAsAdmin() throws Exception {
        String adminToken = authHelper.getAdminToken();
        mvc.perform(get("/api/log-activite/table/users/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}

