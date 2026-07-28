package com.example.userservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestCompany {
    private String name;

    private String ceo;
}
