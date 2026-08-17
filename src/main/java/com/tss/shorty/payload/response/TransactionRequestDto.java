package com.tss.shorty.payload.response;

import com.tss.shorty.entity.enums.TransactionAction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {
    private UUID urlId;
    @NotBlank(message = "Payment method is required")
    String paymentMethod;

    @NotNull(message = "Action is required")
    String action;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity;
}
