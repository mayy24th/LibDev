package com.example.LibDev.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinReqDto {

    @NotBlank
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @Size(min = 2, max = 50)
    private String email;

    @NotBlank
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 최대 16자이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[a-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바르지 않은 전화번호 형식입니다.")
    private String phone;

}
