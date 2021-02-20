package com.portfoliowatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfoliowatch.model.Quote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    @Value("alpha-vantage.key")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String ALPHA_VANTAGE_URI = "https://www.alphavantage.co/query";

    private final SimpleDateFormat ALPHA_VANTAGE_DATE_FORMAT = new SimpleDateFormat("yyyy-dd-MM");

    @Async
    public CompletableFuture<Quote> getQuote(String ticker) {
        Quote quote = null;
        boolean done = false;
        int retries = 10;
        do {
            try(CloseableHttpClient httpclient = HttpClients.custom().build()) {
                URIBuilder builder = new URIBuilder(ALPHA_VANTAGE_URI);
                builder.setParameter("function", "GLOBAL_QUOTE")
                        .setParameter("symbol", ticker)
                        .setParameter("apikey", apiKey);
                HttpGet get = new HttpGet(builder.build());
                try (CloseableHttpResponse response = httpclient.execute(get)) {
                    String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                    quote = mapQuote(responseString);
                    if (quote != null) {
                        done = true;
                    } else {
                        retries--;
                        if (retries == 0) {
                            logger.info("Retries exceeded: " + ticker);
                            done = true;
                        } else {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                                done = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } while (!done);
        return CompletableFuture.completedFuture(quote);
    }

    @Async
    public CompletableFuture<List<Quote>> getQuotes(List<String> tickers) {
        if (tickers == null) {
            throw new NullPointerException("Tickers list is null.");
        }
        List<Quote> quotes = new ArrayList<>();
        for (String ticker: tickers) {
            boolean done = false;
            int retries = 100;
            do {
                try(CloseableHttpClient httpclient = HttpClients.custom().build()) {
                    URIBuilder builder = new URIBuilder(ALPHA_VANTAGE_URI);
                    builder.setParameter("function", "GLOBAL_QUOTE")
                            .setParameter("symbol", ticker)
                            .setParameter("apikey", apiKey);
                    HttpGet get = new HttpGet(builder.build());
                    try (CloseableHttpResponse response = httpclient.execute(get)) {
                        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Quote quote = mapQuote(responseString);
                        if (quote != null) {
                            quotes.add(quote);
                            done = true;
                        } else {
                            retries--;
                            if (retries == 0) {
                                logger.info("Retries exceeded: " + ticker);
                                done = true;
                            } else {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    logger.error(e.getMessage(), e);
                                    done = true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } while (!done);
        }
        return CompletableFuture.completedFuture(quotes);
    }

    /**
     * Maps and creates Quote object from json.
     * @param response response string
     * @return Quote object
     */
    private Quote mapQuote(String response) {
        Quote quote = null;
        JsonNode responseJson = null;
        try {
            responseJson = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        JsonNode parentJson = responseJson == null ? null : responseJson.get("Global Quote");
        if (parentJson != null) {
            quote = new Quote();
            quote.setTicker(parentJson.get("01. symbol").asText());
            quote.setHighestPrice(parentJson.get("03. high").asDouble());
            quote.setLowestPrice(parentJson.get("04. low").asDouble());
            quote.setOpeningPrice(parentJson.get("02. open").asDouble());
            quote.setClosingPrice(parentJson.get("08. previous close").asDouble());
            quote.setChange(parentJson.get("09. change").asDouble());
            quote.setChangePercent(parentJson.get("10. change percent").asText());
            quote.setVolume(parentJson.get("06. volume").asInt());
            try {
                quote.setDatePulled(ALPHA_VANTAGE_DATE_FORMAT
                        .parse(parentJson.get("07. latest trading day").asText()));
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return quote;
    }
}
