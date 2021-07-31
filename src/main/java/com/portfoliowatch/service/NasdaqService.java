package com.portfoliowatch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.nasdaq.DividendProfile;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class NasdaqService {

    private static final Logger logger = LoggerFactory.getLogger(NasdaqService.class);

    private final Gson GSON = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();

    private final Type responseType = new TypeToken<Map<String, Object>>(){}.getType();

    private final RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(6000)
            .setConnectionRequestTimeout(6000)
            .setSocketTimeout(6000).setCookieSpec(CookieSpecs.STANDARD).build();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public DividendProfile getDividendProfile(String symbol) throws IOException {
        String url = String.format("https://api.nasdaq.com/api/quote/%s/dividends?assetclass=stocks", symbol.toUpperCase());
        DividendProfile dividendProfile = null;

        HttpUriRequest request = RequestBuilder.get()
                .setUri(url)
                .setHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .setHeader(HttpHeaders.HOST, "api.nasdaq.com")
                .setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15")
                .setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-us")
                .setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
                .build();

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                try {
                    Map<String, Object> responseMap = GSON.fromJson(responseStr, responseType);
                    if (responseMap.get("data") instanceof LinkedTreeMap) {
                        @SuppressWarnings("unchecked")
                        LinkedTreeMap<String, Object> data = (LinkedTreeMap<String, Object>) responseMap.get("data");
                        if (data != null) {
                            dividendProfile = new DividendProfile();
                            try {
                                dividendProfile.setAnnualizedDividend(Double
                                        .parseDouble(String.valueOf(data.get("annualizedDividend"))));
                            } catch (NumberFormatException numberFormatException) {
                                logger.error("Unable to parse annualized dividend: " + responseMap.get("annualizedDividend"));
                                return null;
                            }
                            dividendProfile.setYield(String.valueOf(data.get("yield")));
                            try {
                                dividendProfile.setPayoutRatio(Double
                                        .parseDouble(String.valueOf(data.get("payoutRatio"))));
                            } catch (NumberFormatException numberFormatException) {
                                logger.error("Unable to parse payout ratio " + responseMap.get("payoutRatio"));
                                dividendProfile.setPayoutRatio(null);
                            }
                            try {
                                String dateStr = String.valueOf(data.get("exDividendDate"));
                                dividendProfile.setExDividendDate(dateFormat.parse(dateStr));
                            } catch (ParseException parseException) {
                                logger.error("Unable to parse ex-dividend date " + responseMap.get("exDividendDate"));
                                dividendProfile.setExDividendDate(null);
                            }
                            try {
                                String dateStr = String.valueOf(data.get("dividendPaymentDate"));
                                dividendProfile.setDividendPaymentDate(dateFormat.parse(dateStr));
                            } catch (ParseException parseException) {
                                logger.error("Unable to parse dividend payments " + responseMap.get("dividendPaymentDate"));
                                dividendProfile.setDividendPaymentDate(null);
                            }
                        }
                    } else {
                        logger.error("Invalid symbol: " + symbol);
                    }
                } catch (Exception exception) {
                    logger.error("Error parsing dividend information for: " + symbol);
                    logger.error(exception.getLocalizedMessage());
                }
            }
        }
        return dividendProfile;
    }

    public Map<String, DividendProfile> getDividendProfiles(Set<String> symbols) throws IOException {
        Map<String, DividendProfile> map = new HashMap<>();
        for (String s: symbols) {
            DividendProfile profile = this.getDividendProfile(s);
            map.put(s, profile);
        }
        return map;
    }

}
