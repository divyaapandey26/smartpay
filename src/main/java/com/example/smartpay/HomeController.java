package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentIntelligenceService paymentService;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ProcessedTokenRepository processedTokenRepository;

    @Autowired
    private UPIAppRepository upiAppRepository;

    @Autowired
    private OfferRepository offerRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("alerts", alertRepository.findAll());
        model.addAttribute("apps", upiAppRepository.findAll());
        model.addAttribute("offers", offerRepository.findAll());
        return "index";
    }

    @GetMapping("/compare")
    public String comparePage(@RequestParam(required = false) Double amount, Model model) {
        if (amount != null) {
            model.addAttribute("amount", amount);
            model.addAttribute("result", paymentService.compare(amount));
        }
        return "compare";
    }

    @GetMapping("/token")
    public String tokenPage() {
        return "token";
    }

    @GetMapping("/compare-apps")
    public String compareAppsPage() {
        return "compare-apps";
    }

    @GetMapping("/history")
    public String historyPage(Model model) {
        model.addAttribute("tokens", processedTokenRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "history";
    }
}