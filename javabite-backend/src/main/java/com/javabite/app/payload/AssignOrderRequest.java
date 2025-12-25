package com.javabite.app.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignOrderRequest {
    @NotNull(message = "Chef ID is required")
    private Long chefId;

    private Long waiterId;  // Optional
}