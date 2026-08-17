package com.tss.shorty.service;

import com.tss.shorty.entity.SystemConfig;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.payload.response.PricingResponseDto;
import com.tss.shorty.payload.response.SystemConfigResponseDto;
import com.tss.shorty.repository.ConfigRepository;
import com.tss.shorty.repository.projection.ConfigProjection;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public PricingResponseDto getPricing() {
        // 1. Ask for all pricing rows in one single DB query
        List<String> pricingCodes = List.of(
                "RENEWAL_FEE_RS",
                "EXTRA_SLOT_PRICE_RS",
                "VISITS_PER_RENEWAL",
                "URL_EXPIRY_DAYS"
        );

        List<ConfigProjection> configs = configRepository.findSpecificConfigsWithTypes(pricingCodes);

        // 2. Setup temporary variables to hold the parsed data
        BigDecimal renewalFee = BigDecimal.ZERO;
        BigDecimal slotPrice = BigDecimal.ZERO;
        int visitsPerRenewal = 0;
        int expiryDays = 0;

        // 3. Loop through the rows and assign the correct values
        for (ConfigProjection config : configs) {
            String code = config.getCode();
            String value = config.getValue();

            if ("RENEWAL_FEE_RS".equals(code)) {
                renewalFee = new BigDecimal(value);
            } else if ("EXTRA_SLOT_PRICE_RS".equals(code)) {
                slotPrice = new BigDecimal(value);
            } else if ("VISITS_PER_RENEWAL".equals(code)) {
                visitsPerRenewal = Integer.parseInt(value);
            } else if ("URL_EXPIRY_DAYS".equals(code)) {
                expiryDays = Integer.parseInt(value);
            }
        }

        // 4. Return the fully packed DTO
        return new PricingResponseDto(renewalFee, visitsPerRenewal, expiryDays, slotPrice);
    }

}
