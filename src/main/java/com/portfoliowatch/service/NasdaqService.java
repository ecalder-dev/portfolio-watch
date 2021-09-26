package com.portfoliowatch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.model.nasdaq.DividendProfile;
import com.portfoliowatch.model.nasdaq.Info;
import com.portfoliowatch.model.nasdaq.ResponseData;
import com.portfoliowatch.util.DateGsonTypeAdapter;
import com.portfoliowatch.util.DoubleGsonTypeAdapter;
import com.portfoliowatch.util.LongGsonTypeAdapter;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class NasdaqService {

    private static final Logger logger = LoggerFactory.getLogger(NasdaqService.class);

    private final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Double.class, new DoubleGsonTypeAdapter())
            .registerTypeAdapter(Long.class, new LongGsonTypeAdapter())
            .registerTypeAdapter(Date.class, new DateGsonTypeAdapter())
            .create();

    private final Type dividendResponseType = new TypeToken<ResponseData<DividendProfile>>(){}.getType();

    private final Type companyProfileResponseType = new TypeToken<ResponseData<CompanyProfile>>(){}.getType();

    private final Type infoResponseType = new TypeToken<ResponseData<Info>>(){}.getType();

    private final RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(6000)
            .setConnectionRequestTimeout(6000)
            .setSocketTimeout(6000).setCookieSpec(CookieSpecs.STANDARD).build();

    /**
     *
     * @param symbol The symbol to look up.
     * @return A DividendProfile data object.
     * @throws IOException Throws an exception from REST request.
     */
    @Cacheable("dividend")
    public DividendProfile getDividendProfile(String symbol) throws IOException {
        String url = String.format("https://api.nasdaq.com/api/quote/%s/dividends?assetclass=stocks", symbol.toUpperCase());
        ResponseData<DividendProfile> response = GSON.fromJson(performGet(url), dividendResponseType);
        if (response.getData() == null) {
            // attempt for an etf asset class
            url = String.format("https://api.nasdaq.com/api/quote/%s/dividends?assetclass=etf", symbol.toUpperCase());
            response = GSON.fromJson(performGet(url), dividendResponseType);
        }
        return response.getData();
    }

    /**
     *
     * @param symbol The symbol to look up.
     * @return A CompanyProfile data object.
     * @throws IOException Throws an exception from REST request.
     */
    @Cacheable("company-portfolio")
    public CompanyProfile getCompanyProfile(String symbol) throws IOException {
        String url = String.format("https://api.nasdaq.com/api/company/%s/company-profile", symbol.toUpperCase());
        String responseStr = performGet(url);
        ResponseData<CompanyProfile> response = GSON.fromJson(responseStr, companyProfileResponseType);
        return response.getData();
    }

    /**
     *
     * @param symbol The symbol to look up.
     * @return A Info data object.
     * @throws IOException Throws an exception from REST request.
     */
    @Cacheable("info" )
    public Info getInfo(String symbol) throws IOException {
        String url = String.format("https://api.nasdaq.com/api/quote/%s/info?assetclass=stocks", symbol.toUpperCase());
        ResponseData<Info> response = GSON.fromJson(performGet(url), infoResponseType);
        if (response.getData() == null) {
            // attempt for an etf asset class
            url = String.format("https://api.nasdaq.com/api/quote/%s/info?assetclass=etf", symbol.toUpperCase());
            response = GSON.fromJson(performGet(url), infoResponseType);
        }
        return response.getData();
    }

    public Map<String, DividendProfile> getDividendProfiles(Set<String> symbols) throws IOException {
        Map<String, DividendProfile> map = new HashMap<>();
        for (String s: symbols) {
            DividendProfile profile = this.getDividendProfile(s);
            map.put(s, profile);
        }
        return map;
    }

    public Map<String, CompanyProfile> getCompanyPortfolios(Set<String> symbols) throws IOException {
        Map<String, CompanyProfile> map = new HashMap<>();
        for (String s: symbols) {
            CompanyProfile profile = this.getCompanyProfile(s);
            map.put(s, profile);
        }
        return map;
    }

    public Map<String, Info> getAllInfo(Set<String> symbols) throws IOException {
        Map<String, Info> map = new HashMap<>();
        for (String s: symbols) {
            Info info = this.getInfo(s);
            map.put(s, info);
        }
        return map;
    }

    private String performGet(String url) throws IOException {
        HttpUriRequest request =  RequestBuilder.get()
                .setUri(url)
                .setHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .setHeader(HttpHeaders.HOST, "api.nasdaq.com")
                .setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15")
                .setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-us")
                .setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
                .build();

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
            }
        }
    }

}
