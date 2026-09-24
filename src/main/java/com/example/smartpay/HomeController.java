package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired private UserRepository userRepository;
    @Autowired private PaymentIntelligenceService paymentService;
    @Autowired private AlertRepository alertRepository;
    @Autowired private ProcessedTokenRepository processedTokenRepository;
    @Autowired private UPIAppRepository upiAppRepository;
    @Autowired private OfferRepository offerRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<User> users = userRepository.findAll();
        List<Alert> alerts = alertRepository.findAll();
        List<ProcessedToken> tokens = processedTokenRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("alerts", alerts);
        model.addAttribute("apps", upiAppRepository.findAll());
        model.addAttribute("offers", offerRepository.findAll());

        // Dashboard metrics
        long totalSettlements = tokens.size();
        double totalSettledAmount = tokens.stream().mapToDouble(ProcessedToken::getAmount).sum();
        long totalAlerts = alerts.size();
        double amountFlagged = alerts.stream().mapToDouble(Alert::getExtraCharged).sum();

        model.addAttribute("totalSettlements", totalSettlements);
        model.addAttribute("totalSettledAmount", totalSettledAmount);
        model.addAttribute("totalAlerts", totalAlerts);
        model.addAttribute("amountFlagged", amountFlagged);

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