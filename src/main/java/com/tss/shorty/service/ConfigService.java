package com.tss.shorty.service;

import com.tss.shorty.entity.SystemConfig;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.repository.ConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Integer getIntegerConfig(String code) {
        SystemConfig systemConfig = configRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("cannot find value with the code:" + code));

        String type = systemConfig.getValueTypes()
                .getType().toUpperCase();
        if ("INTEGER".equals(type)) {
            return Integer.parseInt(systemConfig.getValue());
        }
        throw new IllegalArgumentException();
    }

    public double getDecimalConfig(String code) {
        SystemConfig systemConfig = configRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("cannot find value with the code:" + code));

        String type = systemConfig.getValueTypes()
                .getType().toUpperCase();
        if ("DECIMAL".equals(type)) {
            return Double.parseDouble(systemConfig.getValue());
        }
        throw new IllegalArgumentException();
    }

}
