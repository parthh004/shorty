package com.tss.shorty.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PricingResponseDto {
    BigDecimal renewalFee;
    int visitsPerRenewal;
    int expiryDays;
    BigDecimal slotPrice;
}
