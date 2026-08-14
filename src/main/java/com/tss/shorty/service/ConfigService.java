package com.tss.shorty.service;

import com.tss.shorty.entity.SystemConfig;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.payload.response.SystemConfigResponseDto;
import com.tss.shorty.repository.ConfigRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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


    @Transactional
    public SystemConfigResponseDto updateConfig(String code, String newValue)
    {
        log.info("Attempting to update configuration {} to new value: {}", code, newValue);

        SystemConfig config = configRepository.findById(code).orElseThrow(() -> {return new ResourceNotFoundException("Cannot find config with the code: " + code);});
        String type = config.getValueTypes().getType().toUpperCase();

        try
        {
            if ("INTEGER".equals(type))
            {
                Integer.parseInt(newValue);
            }
            else if ("DECIMAL".equals(type))
            {
                Double.parseDouble(newValue);
            }
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid value format. Expected a valid " + type + " for config code: " + code);
        }

        config.setValue(newValue);
        SystemConfig savedConfig = configRepository.save(config);
        log.info("Successfully updated configuration {}", code);

        return new SystemConfigResponseDto(
                savedConfig.getCode(),
                savedConfig.getDescription(),
                savedConfig.getValue(),
                type
        );
    }

}
