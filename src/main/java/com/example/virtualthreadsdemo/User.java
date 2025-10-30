package com.example.virtualthreadsdemo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data // Lombok annotation for getters, setters, toString, etc.
@Entity
@Table(name = "app_users") // "user" is a reserved keyword in Postgres, so we use "app_users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
}
