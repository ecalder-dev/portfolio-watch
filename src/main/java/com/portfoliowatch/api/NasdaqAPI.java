package com.portfoliowatch.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.model.nasdaq.DividendProfile;
import com.portfoliowatch.model.nasdaq.ResponseData;
import com.portfoliowatch.model.nasdaq.StockInfo;
import com.portfoliowatch.model.nasdaq.Summary;
import com.portfoliowatch.model.nasdaq.SummaryData;
import com.portfoliowatch.util.DateGsonTypeAdapter;
import com.portfoliowatch.util.DoubleGsonTypeAdapter;
import com.portfoliowatch.util.LongGsonTypeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public final class NasdaqAPI {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Double.class, new DoubleGsonTypeAdapter())
            .registerTypeAdapter(Long.class, new LongGsonTypeAdapter())
            .registerTypeAdapter(Date.class, new DateGsonTypeAdapter())
            .create();

    private static final Type dividendResponseType = new TypeToken<ResponseData<DividendProfile>>(){}.getType();

    private static final Type companyProfileResponseType = new TypeToken<ResponseData<CompanyProfile>>(){}.getType();

    private static final Type infoResponseType = new TypeToken<ResponseData<StockInfo>>(){}.getType();

    private static final Type summaryResponseType = new TypeToken<ResponseData<Summary>>(){}.getType();

    private static final RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(6000)
            .setConnectionRequestTimeout(6000)
            .setSocketTimeout(6000).setCookieSpec(CookieSpecs.STANDARD).build();

    /**
     *
     * @param symbol The symbol to look up.
     * @return A DividendProfile data object.
     * @throws IOException Throws an exception from REST request.
     */
    public static DividendProfile getDividendProfile(String symbol) throws IOException {
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
    public static CompanyProfile getCompanyProfile(String symbol) throws IOException {
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
    public static StockInfo getInfo(String symbol) throws IOException {
        String url = String.format("https://api.nasdaq.com/api/quote/%s/info?assetclass=stocks", symbol.toUpperCase());
        ResponseData<StockInfo> response = GSON.fromJson(performGet(url), infoResponseType);
        if (response.getData() == null) {
            // attempt for an etf asset class
            url = String.format("https://api.nasdaq.com/api/quote/%s/info?assetclass=etf", symbol.toUpperCase());
            response = GSON.fromJson(performGet(url), infoResponseType);
        }
        return response.getData();
    }

    /**
     *
     * @param symbol The symbol to look up.
     * @return A Info data object.
     * @throws IOException Throws an exception from REST request.
     */
    @Cacheable("summary" )
    public static Summary getSummary(String symbol) throws IOException {
        String url = String.format("https://api.nasdaq.com/api/quote/%s/summary?assetclass=stocks", symbol.toUpperCase());
        String dataStr = performGet(url);
        ResponseData<Summary> response = GSON.fromJson(dataStr, summaryResponseType);
        if (response.getData() == null) {
            // attempt for an etf asset class
            url = String.format("https://api.nasdaq.com/api/quote/%s/summary?assetclass=etf", symbol.toUpperCase());
            response = GSON.fromJson(performGet(url), summaryResponseType);
        }
        return response.getData();
    }

    /**
     * Gets a list of company dividend profiles give a set of symbols.
     * @param symbols The symbols to return.
     * @return A list of dividend profiles.
     * @throws IOException An exception at service error.
     */
    public static Map<String, DividendProfile> getDividendProfiles(Set<String> symbols) throws IOException {
        Map<String, DividendProfile> map = new HashMap<>();
        for (String s: symbols) {
            DividendProfile profile = getDividendProfile(s);
            map.put(s, profile);
        }
        return map;
    }

    /**
     * Gets a list of company profiles give a set of symbols.
     * @param symbols The symbols to return.
     * @return A list of company profiles.
     * @throws IOException An exception at service error.
     */
    public static List<CompanyProfile> getCompanyProfiles(Set<String> symbols) throws IOException {
        List<CompanyProfile> companyProfiles = new ArrayList<>();
        for (String s: symbols) {
            CompanyProfile profile = getCompanyProfile(s);
            if (profile != null) {
                companyProfiles.add(profile);
            }
        }
        return companyProfiles;
    }

    /**
     * Gets a list of stock info give a set of symbols.
     * @param symbols The symbols to return.
     * @return A list of stock info.
     * @throws IOException An exception at service error.
     */
    public static Map<String, StockInfo> getAllInfo(Set<String> symbols) throws IOException {
        Map<String, StockInfo> map = new HashMap<>();
        for (String s: symbols) {
            StockInfo stockInfo = getInfo(s);
            map.put(s, stockInfo);
        }
        return map;
    }

    /**
     * Performs a get request to Nasdaq site.
     * @param url The url to perform rest request.
     * @return A string representation of return.
     * @throws IOException An exception returned from http client.
     */
    private static String performGet(String url) throws IOException {
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
