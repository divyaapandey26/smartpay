package com.example.smartpay;

import jakarta.persistence.*;

@Entity
@Table(name = "upi_apps")
public class UPIApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String provider;
    private boolean active;

    public UPIApp() {}

    public UPIApp(String name, String provider, boolean active) {
        this.name = name;
        this.provider = provider;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getProvider() { return provider; }
    public boolean isActive() { return active; }
}
