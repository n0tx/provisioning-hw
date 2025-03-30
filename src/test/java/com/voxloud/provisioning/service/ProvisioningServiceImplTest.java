package com.voxloud.provisioning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ProvisioningServiceImplTest {

    @Autowired
    private ProvisioningService provisioningService;

    @Autowired
    private DeviceRepository deviceRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        deviceRepository.deleteAll();

        // Desk phone without override fragment
        Device deskDevice = new Device();
        deskDevice.setMacAddress("aa-bb-cc-dd-ee-ff");
        deskDevice.setModel(Device.DeviceModel.DESK);
        deskDevice.setUsername("john");
        deskDevice.setPassword("doe");
        deviceRepository.save(deskDevice);

        // Conference phone without override fragment
        Device conferenceDevice = new Device();
        conferenceDevice.setMacAddress("f1-e2-d3-c4-b5-a6");
        conferenceDevice.setModel(Device.DeviceModel.CONFERENCE);
        conferenceDevice.setUsername("alice");
        conferenceDevice.setPassword("secret");
        deviceRepository.save(conferenceDevice);

        // Desk phone with override fragment
        Device deskOverride = new Device();
        deskOverride.setMacAddress("a1-b2-c3-d4-e5-f6");
        deskOverride.setModel(Device.DeviceModel.DESK);
        deskOverride.setUsername("john");
        deskOverride.setPassword("doe");
        deskOverride.setOverrideFragment("domain=sip.anotherdomain.com\nport=5161\ntimeout=10");
        deviceRepository.save(deskOverride);

        // Conference phone with override fragment
        Device conferenceOverride = new Device();
        conferenceOverride.setMacAddress("1a-2b-3c-4d-5e-6f");
        conferenceOverride.setModel(Device.DeviceModel.CONFERENCE);
        conferenceOverride.setUsername("alice");
        conferenceOverride.setPassword("secret");
        conferenceOverride.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\", \"port\":\"5161\", \"timeout\":10}");
        deviceRepository.save(conferenceOverride);
    }

    @Test
    public void whenDeskDeviceWithoutOverride_thenReturnPropertyFile() {
        String result = provisioningService.getProvisioningFile("aa-bb-cc-dd-ee-ff");
        assertThat(result).isNotNull();
        assertThat(result).contains("username=john");
        assertThat(result).contains("password=doe");
        assertThat(result).contains("domain=sip.voxloud.com");
        assertThat(result).contains("port=5060");
        assertThat(result).contains("codecs=G711,G729,OPUS");
    }

    @Test
    public void whenConferenceDeviceWithoutOverride_thenReturnJson() throws Exception {
        String result = provisioningService.getProvisioningFile("f1-e2-d3-c4-b5-a6");
        assertThat(result).isNotNull();

        Map<String, Object> config = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
        assertThat(config.get("username")).isEqualTo("alice");
        assertThat(config.get("password")).isEqualTo("secret");
        assertThat(config.get("domain")).isEqualTo("sip.voxloud.com");
        assertThat(config.get("port")).isEqualTo("5060");
        assertThat(config.get("codecs")).isEqualTo(Arrays.asList("G711", "G729", "OPUS"));
    }

    @Test
    public void whenDeskDeviceWithOverride_thenReturnUpdatedProperties() {
        String result = provisioningService.getProvisioningFile("a1-b2-c3-d4-e5-f6");
        assertThat(result).isNotNull();
        assertThat(result).contains("domain=sip.anotherdomain.com");
        assertThat(result).contains("port=5161");
        assertThat(result).contains("timeout=10");
    }

    @Test
    public void whenConferenceDeviceWithOverride_thenReturnUpdatedJson() throws Exception {
        String result = provisioningService.getProvisioningFile("1a-2b-3c-4d-5e-6f");
        assertThat(result).isNotNull();
        Map<String, Object> config = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {});
        assertThat(config.get("domain")).isEqualTo("sip.anotherdomain.com");
        assertThat(config.get("port")).isEqualTo("5161");
        assertThat(config.get("timeout")).isEqualTo(10);
    }

    @Test
    public void whenDeviceNotFound_thenReturnNull() {
        String result = provisioningService.getProvisioningFile("non-existing");
        assertThat(result).isNull();
    }
}
