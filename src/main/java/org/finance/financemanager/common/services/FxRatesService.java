package org.finance.financemanager.common.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finance.financemanager.common.payloads.CurrencyRateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FxRatesService {

    @Value("${fx.rates.url}")
    private String fxRatesUrl;

    @Value("${fx.rates.access.token}")
    private String fxRatesAccessToken;

    @Transactional
    public List<String> getCurrencies() {
        try {
            String apiResponse = makeApiCall("currencies");
            List<String> currencyCodes = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> currencyMap = objectMapper.readValue(apiResponse, new TypeReference<Map<String, Object>>() {});
            currencyCodes.addAll(currencyMap.keySet());
            return currencyCodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public List<CurrencyRateDto> getExchangeRates() {
        try {
            String apiResponse = makeApiCall("latest?base=EUR");
            List<CurrencyRateDto> currencyRates = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> mainResponse = objectMapper.readValue(apiResponse, new TypeReference<Map<String, Object>>() {});
            Map<String, Object> rates = (Map<String, Object>) mainResponse.get("rates");
            rates.forEach((currency, rate) -> {
                if (rate instanceof Number) {
                    double rateAsDouble = ((Number) rate).doubleValue();
                    CurrencyRateDto dto = new CurrencyRateDto(currency, rateAsDouble);
                    currencyRates.add(dto);
                }
            });
            return currencyRates;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String makeApiCall(String endpoint) throws Exception {
        URL urlForGetRequest = new URL(fxRatesUrl + endpoint);
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + fxRatesAccessToken);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String readLine;
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
            }
        } else {
            throw new Exception("Error in API call: " + connection.getResponseMessage());
        }
        return response.toString();
    }
}
