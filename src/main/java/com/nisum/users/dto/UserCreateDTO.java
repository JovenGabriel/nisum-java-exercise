package com.nisum.users.dto;


import com.nisum.users.annotations.ValidPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateDTO {

    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @ValidPassword
    private String password;
    @NotEmpty
    @Valid
    private List<PhoneDTO> phones;

}
