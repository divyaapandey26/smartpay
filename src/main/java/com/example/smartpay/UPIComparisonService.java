package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UPIComparisonService {

    @Autowired private UPIAppRepository appRepository;
    @Autowired private OfferRepository offerRepository;

    public Map<String, Object> compareApps(String merchantName, double amount) {
        List<UPIApp> apps = appRepository.findAll();
        List<Offer> allOffers = offerRepository.findByMerchantName(merchantName);

        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> best = null;
        double bestEffectivePrice = amount;

        for (UPIApp app : apps) {
            if (!app.isActive()) continue;

            Offer applicable = null;
            for (Offer o : allOffers) {
                if (o.getAppId().equals(app.getId())
                        && o.isCurrentlyValid()
                        && amount >= o.getMinAmount()) {
                    applicable = o;
                    break;
                }
            }

            double cashback = 0;
            if (applicable != null) {
                cashback = Math.min(amount * applicable.getCashbackPercent() / 100.0,
                        applicable.getMaxCashback());
            }
            double effectivePrice = amount - cashback;

            Map<String, Object> row = new HashMap<>();
            row.put("appId", app.getId());
            row.put("appName", app.getName());
            row.put("provider", app.getProvider());
            row.put("cashback", Math.round(cashback * 100.0) / 100.0);
            row.put("effectivePrice", Math.round(effectivePrice * 100.0) / 100.0);
            row.put("offerDetails", applicable != null
                    ? applicable.getCashbackPercent() + "% up to ₹" + applicable.getMaxCashback()
                    : "No offer available");
            results.add(row);

            if (effectivePrice < bestEffectivePrice) {
                bestEffectivePrice = effectivePrice;
                best = row;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("merchantName", merchantName);
        response.put("amount", amount);
        response.put("results", results);
        response.put("bestApp", best != null ? best.get("appName") : "None");
        response.put("bestEffectivePrice", Math.round(bestEffectivePrice * 100.0) / 100.0);
        response.put("savings", Math.round((amount - bestEffectivePrice) * 100.0) / 100.0);
        return response;
    }
}