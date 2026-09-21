package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/generate")
    public Map<String, Object> generate(@RequestBody Map<String, Object> request) throws Exception {
        Long senderId = Long.parseLong(request.get("senderId").toString());
        double amount = Double.parseDouble(request.get("amount").toString());
        return tokenService.generateToken(senderId, amount);
    }

    @PostMapping("/settle")
    public Map<String, Object> settle(@RequestBody Map<String, Object> request) throws Exception {
        String token = request.get("token").toString();
        Long receiverId = Long.parseLong(request.get("receiverId").toString());
        return tokenService.settleToken(token, receiverId);
    }
}