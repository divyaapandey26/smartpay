package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/optimizer")
public class CostOptimizerController {

    @Autowired private CostOptimizerService optimizerService;

    @GetMapping("/optimize")
    public Map<String, Object> optimize(@RequestParam double amount,
                                        @RequestParam int count) {
        return optimizerService.optimize(amount, count);
    }
}