# SmartPay — Smart Payment Intelligence & Offline Settlement System

A Spring Boot backend that combines payment intelligence with secure offline payment settlement.

## Features

- **Payment Intelligence Engine** — Compares UPI, Cash, and Split payments based on RBI MDR rules (0.4% on UPI transactions above ₹2000, capped at ₹300) and recommends the cheapest option.
- **Overcharge Detection** — Automatically flags cases where a user was charged more than expected.
- **Encrypted Offline Tokens** — Generates AES-encrypted payment tokens that can be settled later, simulating offline UPI payments.
- **Double-Spend Prevention** — Uses idempotent token validation to prevent the same token from being used more than once.

## Tech Stack

- Java 17
- Spring Boot 4.x
- Spring Data JPA
- H2 (in-memory database for development)
- Maven

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users |
| POST | `/api/users` | Create a user |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/compare?amount={amount}` | Compare payment methods |
| POST | `/api/compare/check-overcharge` | Check if a fee was overcharged |
| POST | `/api/token/generate` | Generate an encrypted offline payment token |
| POST | `/api/token/settle` | Settle a token (transfer funds) |

## How to Run

1. Clone the repo: `git clone https://github.com/divyaapandey26/smartpay.git`
2. Open in IntelliJ
3. Run `SmartPayApplication`
4. Server starts on `http://localhost:8080`

## Author

Divya Pandey — MCA Student
