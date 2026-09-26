package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired private UserRepository userRepository;
    @Autowired private PaymentIntelligenceService paymentService;
    @Autowired private AlertRepository alertRepository;
    @Autowired private ProcessedTokenRepository processedTokenRepository;
    @Autowired private UPIAppRepository upiAppRepository;
    @Autowired private OfferRepository offerRepository;
    @Autowired private OfflineQueueService offlineQueueService;

    @GetMapping("/")
    public String home(Model model) {
        List<User> users = userRepository.findAll();
        List<Alert> alerts = alertRepository.findAll();
        List<ProcessedToken> tokens = processedTokenRepository.findAll();
        List<UPIApp> apps = upiAppRepository.findAll();
        List<Offer> offers = offerRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("alerts", alerts);

        long totalSettlements = tokens.size();
        double totalSettledAmount = tokens.stream().mapToDouble(ProcessedToken::getAmount).sum();
        long totalAlerts = alerts.size();
        double amountFlagged = alerts.stream().mapToDouble(Alert::getExtraCharged).sum();

        model.addAttribute("totalSettlements", totalSettlements);
        model.addAttribute("totalSettledAmount", totalSettledAmount);
        model.addAttribute("totalAlerts", totalAlerts);
        model.addAttribute("amountFlagged", amountFlagged);

        Map<String, Long> alertMethodCounts = new HashMap<>();
        for (Alert a : alerts) {
            alertMethodCounts.merge(a.getMethod(), 1L, Long::sum);
        }
        model.addAttribute("alertMethodCounts", alertMethodCounts);

        Map<String, Long> appOfferCounts = new HashMap<>();
        for (UPIApp app : apps) {
            appOfferCounts.put(app.getName(), 0L);
        }
        for (Offer o : offers) {
            for (UPIApp app : apps) {
                if (app.getId().equals(o.getAppId())) {
                    appOfferCounts.merge(app.getName(), 1L, Long::sum);
                    break;
                }
            }
        }
        model.addAttribute("appOfferCounts", appOfferCounts);

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

    @GetMapping("/offline-queue")
    public String offlineQueuePage(Model model) {
        model.addAttribute("queue", offlineQueueService.getAll());
        return "offline-queue";
    }

    @GetMapping("/optimizer")
    public String optimizerPage() {
        return "optimizer";
    }
}