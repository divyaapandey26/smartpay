package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UPIAppController {

    @Autowired private UPIAppRepository appRepository;
    @Autowired private OfferRepository offerRepository;
    @Autowired private UPIComparisonService comparisonService;

    @GetMapping("/apps")
    public List<UPIApp> getAllApps() {
        return appRepository.findAll();
    }

    @PostMapping("/apps")
    public UPIApp createApp(@RequestBody UPIApp app) {
        return appRepository.save(app);
    }

    @GetMapping("/offers")
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @PostMapping("/offers")
    public Offer createOffer(@RequestBody Offer offer) {
        return offerRepository.save(offer);
    }

    @GetMapping("/compare-apps")
    public java.util.Map<String, Object> compareApps(
            @RequestParam String merchant,
            @RequestParam double amount) {
        return comparisonService.compareApps(merchant, amount);
    }
}