package com.voxloud.provisioning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class ProvisioningServiceImpl implements ProvisioningService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Value("${provisioning.domain}")
    private String defaultDomain;

    @Value("${provisioning.port}")
    private String defaultPort;

    @Value("${provisioning.codecs}")
    private String defaultCodecs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getProvisioningFile(String macAddress) {
        Optional<Device> optionalDevice = deviceRepository.findById(macAddress);
        if (!optionalDevice.isPresent()) {
            return null;
        }

        Device device = optionalDevice.get();

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("username", device.getUsername());
        config.put("password", device.getPassword());
        config.put("domain", defaultDomain);
        config.put("port", defaultPort);
        config.put("codecs", defaultCodecs);

        if (device.getOverrideFragment() != null && !device.getOverrideFragment().trim().isEmpty()) {
            try {
                if (device.getModel() == Device.DeviceModel.DESK) {

                    Properties overrideProps = new Properties();
                    overrideProps.load(new StringReader(device.getOverrideFragment()));
                    for (String key : overrideProps.stringPropertyNames()) {
                        config.put(key, overrideProps.getProperty(key));
                    }
                } else if (device.getModel() == Device.DeviceModel.CONFERENCE) {

                    Map<String, Object> overrideMap = objectMapper.readValue(
                            device.getOverrideFragment(), new TypeReference<Map<String, Object>>() {});
                    config.putAll(overrideMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (device.getModel() == Device.DeviceModel.DESK) {

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        } else if (device.getModel() == Device.DeviceModel.CONFERENCE) {

            Object codecsObj = config.get("codecs");
            if (codecsObj instanceof String) {
                String codecsStr = (String) codecsObj;
                List<String> codecsList = Arrays.stream(codecsStr.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                config.put("codecs", codecsList);
            }
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
