package com.example.smartpay;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Unique username for login
    private String username;

    // BCrypt-hashed password (never store plaintext)
    private String password;

    private double balance;

    // Role: "ROLE_USER" or "ROLE_ADMIN"
    private String role = "ROLE_USER";

    public User() {}

    public User(String name, String username, String password, double balance) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.role = "ROLE_USER";
    }

    public User(String name, String username, String password, double balance, String role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', username='" + username + "', balance=" + balance + "}";
    }
}