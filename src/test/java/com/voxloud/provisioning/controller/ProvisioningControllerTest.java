package com.voxloud.provisioning.controller;

import com.voxloud.provisioning.service.ProvisioningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProvisioningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProvisioningService provisioningService;

    @Test
    public void givenExistingDevice_whenGetProvisioning_thenStatus200() throws Exception {
        mockMvc.perform(get("/api/v1/provisioning/aa-bb-cc-dd-ee-ff"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("username=")));
    }

    @Test
    public void givenNonExistingDevice_whenGetProvisioning_thenStatus404() throws Exception {
        mockMvc.perform(get("/api/v1/provisioning/non-existing"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Device not found")));
    }
}
