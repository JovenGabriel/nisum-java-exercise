package com.nisum.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneDTO {

    @NotBlank
    private String number;
    @NotBlank
    private String citycode;
    @NotBlank
    private String countrycode;
}
