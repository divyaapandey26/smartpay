# 💰 SmartPay — Smart Payment Intelligence & Offline Settlement System

A Spring Boot backend that combines **payment intelligence** with **secure offline payment settlement**, built as an MCA minor project.

## 🎯 Problem Statement

Digital payments in India (especially UPI) have become ubiquitous, yet:

1. Users are confused by **RBI's MDR rules** (0.4% on UPI transactions above ₹2000, capped at ₹300)
2. **Overcharging** goes unnoticed — users don't know what fee is fair
3. **Offline payments** aren't possible when there's no internet

SmartPay solves all three.

## ✨ Features

### 🧠 Payment Intelligence Engine
Compares **UPI vs Cash vs Split** based on current RBI MDR rules and recommends the cheapest option for any transaction amount.

### ⚠️ Overcharge Detection
Automatically flags transactions where the user was charged more than the expected fee. All alerts are logged and displayed on the dashboard.

### 🔐 Encrypted Offline Payment Tokens
Generates **AES-encrypted tokens** that represent a payment. These tokens can be transferred to a merchant offline (via SMS, Bluetooth, etc.) and settled later when internet is available.

### 🛡️ Double-Spend Prevention
Uses **idempotent token validation** to ensure the same token can never be settled twice — a critical requirement for any offline payment system.

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.x |
| Persistence | Spring Data JPA |
| Database | H2 (dev) |
| Templating | Thymeleaf |
| Styling | Bootstrap 5 |
| Build | Maven |

## 🚀 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | List all users |
| POST | `/api/users` | Create a user |
| GET | `/api/users/{id}` | Get a user by ID |
| GET | `/api/compare?amount={amount}` | Compare payment methods |
| POST | `/api/compare/check-overcharge` | Detect overcharges |
| POST | `/api/token/generate` | Generate an encrypted offline token |
| POST | `/api/token/settle` | Settle a token (transfer money) |

## 🖥️ Web Interface

| URL | Page |
|---|---|
| `/` | Dashboard with users, balances, and alerts |
| `/compare` | Payment method comparison form |
| `/token` | Generate and settle offline tokens |

## 🏃 How to Run

```bash
# Clone the repository
git clone https://github.com/divyaapandey26/smartpay.git
cd smartpay

# Run with Maven wrapper
./mvnw spring-boot:run
