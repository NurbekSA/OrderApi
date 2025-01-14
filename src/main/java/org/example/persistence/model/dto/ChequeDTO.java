package org.example.persistence.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChequeDTO {
    private String id;
    @NotNull
    private String orderId;
    @NotNull
    private String body;
}