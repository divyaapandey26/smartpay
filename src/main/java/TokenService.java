package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired private CryptoService cryptoService;
    @Autowired private UserRepository userRepository;
    @Autowired private ProcessedTokenRepository tokenRepository;

    public Map<String, Object> generateToken(Long senderId, double amount) throws Exception {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        String payload = senderId + ":" + amount + ":" + uuid + ":" + timestamp;

        String encryptedToken = cryptoService.encrypt(payload);

        Map<String, Object> result = new HashMap<>();
        result.put("token", encryptedToken);
        result.put("senderId", senderId);
        result.put("amount", amount);
        result.put("tokenId", uuid);
        return result;
    }

    @Transactional
    public Map<String, Object> settleToken(String token, Long receiverId) throws Exception {
        String payload = cryptoService.decrypt(token);
        String[] parts = payload.split(":");

        Long senderId = Long.parseLong(parts[0]);
        double amount = Double.parseDouble(parts[1]);
        String uuid = parts[2];

        if (tokenRepository.existsById(uuid)) {
            throw new RuntimeException("DOUBLE-SPEND DETECTED: This token has already been settled");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(receiver);

        tokenRepository.save(new ProcessedToken(uuid, senderId, amount));

        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("tokenId", uuid);
        result.put("senderId", senderId);
        result.put("receiverId", receiverId);
        result.put("amount", amount);
        return result;
    }
}