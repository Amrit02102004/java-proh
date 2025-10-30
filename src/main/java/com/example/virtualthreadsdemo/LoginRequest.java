package com.example.virtualthreadsdemo;

import lombok.Data;

@Data // Lombok annotation for getters/setters
public class LoginRequest {
    private String username;
    private String password;
}
