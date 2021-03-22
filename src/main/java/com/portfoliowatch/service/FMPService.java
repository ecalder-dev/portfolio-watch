package com.portfoliowatch.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FMPService {

    @Value("${financial-modeling-prep.api-key}")
    private String apiKey;

    private final Gson gson = new Gson();

    private final Type fmpProfileListType = new TypeToken<ArrayList<FMPProfile>>(){}.getType();


    public List<FMPProfile> getCompanyProfile(List<String> symbols) throws URISyntaxException, IOException {
        List<FMPProfile> profiles = new ArrayList<>();
        if (symbols == null || symbols.isEmpty()) {
            return profiles;
        }
        String FMP_URL = "https://financialmodelingprep.com/api/v3/profile/" + String.join(",", symbols);
        URIBuilder builder = new URIBuilder(FMP_URL);
        builder.addParameter("apikey", apiKey);
        HttpGet get = new HttpGet(builder.build());

        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                profiles = gson.fromJson(responseStr, fmpProfileListType);
            }
        }

        return profiles;
    }
}
