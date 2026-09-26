# SmartPay

**An Intelligent Offline Payment Settlement System with Cost Optimization and Overcharge Detection**

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

SmartPay is a Spring Boot backend that addresses three real problems in Indian digital payments: fee confusion under RBI's MDR framework, undetected merchant overcharging, and the inability to make digital payments without internet connectivity.

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Web Interface](#web-interface)
- [Security](#security)
- [Test Cases](#test-cases)
- [Project Structure](#project-structure)
- [Limitations & Future Work](#limitations--future-work)
- [Authors](#authors)
- [License](#license)

---

## Problem Statement

Digital payments in India have grown rapidly, but three issues remain unaddressed:

1. **Fee confusion** — Users are unaware of RBI's Merchant Discount Rate (MDR) framework, which mandates a 0.4% fee on UPI P2M transactions above ₹2000, capped at ₹300.
2. **Silent overcharging** — Overcharges by merchants often go undetected because users lack a way to verify the correct fee.
3. **No offline capability** — UPI requires active internet connectivity, making digital payments impossible in low-connectivity areas.

SmartPay provides a Payment Intelligence Engine, an Overcharge Detection module backed by statistical anomaly detection, and an Offline Payment Settlement engine using AES-GCM encryption with idempotent token validation.

---

## Features

### Payment Intelligence Engine
Compares UPI, Cash, and Split payment methods based on current RBI MDR rules and recommends the cheapest option for any transaction amount.

### UPI App Comparison
Compares GPay, PhonePe, Paytm, and BHIM based on merchant-specific cashback offers. Calculates the effective price after cashback and recommends the app that saves the most.

### Overcharge Detection with Explainable Reasons
Flags transactions where the user was charged more than the RBI-mandated fee, and provides specific reasons for each flag (percentage over, threshold breaches, recurring merchant patterns).

### Dual Statistical Anomaly Detection
Uses both z-score analysis and IQR (Interquartile Range) to identify statistically unusual overcharges. A combined weighted score (60% z-score, 40% IQR) provides high-confidence flagging.

### AES-GCM Encrypted Offline Tokens
Generates AES-GCM encrypted tokens (confidentiality + authenticity) that can be settled later when connectivity is available.

### Idempotent Double-Spend Prevention
Uses UUID-based idempotent validation to ensure the same token can never be settled twice.

### Token Expiry
Tokens expire 15 minutes after generation. Expired tokens are rejected to prevent replay attacks.

### Rate Limiting
Token endpoints are protected by a sliding-window rate limiter (5 requests/minute per client IP).

### Offline Transaction Queue
Simulates real offline payment flow: transactions queue locally and sync in a batch when connectivity is restored, with graceful handling of failure cases.

### Settlement Cost Optimizer
Given a batch of transactions, computes three settlement strategies and recommends the optimal one using a weighted multi-objective scoring formula (fee 50%, speed 30%, risk 20%).

---

## Architecture
