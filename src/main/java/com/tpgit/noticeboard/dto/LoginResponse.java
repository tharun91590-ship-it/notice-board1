package com.tpgit.noticeboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;      // "PRINCIPAL" or "PUBLIC"
}