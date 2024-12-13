package com.portfoliowatch.service.third;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.model.nasdaq.DividendProfile;
import com.portfoliowatch.model.nasdaq.ResponseData;
import com.portfoliowatch.model.nasdaq.StockInfo;
import com.portfoliowatch.model.nasdaq.Summary;
import com.portfoliowatch.util.adapter.DateGsonTypeAdapter;
import com.portfoliowatch.util.adapter.DoubleGsonTypeAdapter;
import com.portfoliowatch.util.adapter.LongGsonTypeAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

@Slf4j
public final class NasdaqAPI {

  private static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(Double.class, new DoubleGsonTypeAdapter())
          .registerTypeAdapter(Long.class, new LongGsonTypeAdapter())
          .registerTypeAdapter(Date.class, new DateGsonTypeAdapter())
          .create();

  private static final Type dividendResponseType =
      new TypeToken<ResponseData<DividendProfile>>() {}.getType();

  private static final Type companyProfileResponseType =
      new TypeToken<ResponseData<CompanyProfile>>() {}.getType();

  private static final Type infoResponseType =
      new TypeToken<ResponseData<StockInfo>>() {}.getType();

  private static final Type summaryResponseType =
      new TypeToken<ResponseData<Summary>>() {}.getType();

  private static final RequestConfig config =
      RequestConfig.custom()
          .setConnectTimeout(6000)
          .setConnectionRequestTimeout(6000)
          .setSocketTimeout(6000)
          .setCookieSpec(CookieSpecs.STANDARD)
          .build();

  private static final Map<String, StockInfo> cachedStockInfo = new HashMap<>();

  private static final Map<String, CompanyProfile> cachedCompanyProfile = new HashMap<>();

  private static final Map<String, DividendProfile> cachedDividendProfile = new HashMap<>();

  private static final Map<String, Summary> cachedSummary = new HashMap<>();

  /**
   * @param symbol The symbol to look up.
   * @return A DividendProfile data object.
   * @throws IOException Throws an exception from REST request.
   */
  public static DividendProfile getDividendProfile(String symbol)
      throws IOException, InterruptedException {
    if (cachedDividendProfile.containsKey(symbol)) {
      return cachedDividendProfile.get(symbol);
    }
    String url =
        String.format(
            "https://api.nasdaq.com/api/quote/%s/dividends?assetclass=stocks",
            symbol.toUpperCase());
    ResponseData<DividendProfile> response = GSON.fromJson(performGet(url), dividendResponseType);
    if (response.getData() == null) {
      // attempt for an etf asset class
      url =
          String.format(
              "https://api.nasdaq.com/api/quote/%s/dividends?assetclass=etf", symbol.toUpperCase());
      response = GSON.fromJson(performGet(url), dividendResponseType);
    }
    cachedDividendProfile.put(symbol, response.getData());
    return response.getData();
  }

  /**
   * @param symbol The symbol to look up.
   * @return A CompanyProfile data object.
   * @throws IOException Throws an exception from REST request.
   */
  public static CompanyProfile getCompanyProfile(String symbol) throws IOException {
    if (cachedCompanyProfile.containsKey(symbol)) {
      return cachedCompanyProfile.get(symbol);
    }
    String url =
        String.format(
            "https://api.nasdaq.com/api/company/%s/company-profile", symbol.toUpperCase());
    String responseStr = performGet(url);
    ResponseData<CompanyProfile> response = GSON.fromJson(responseStr, companyProfileResponseType);
    cachedCompanyProfile.put(symbol, response.getData());
    return response.getData();
  }

  /**
   * @param symbol The symbol to look up.
   * @return A Info data object.
   */
  public static StockInfo getInfo(String symbol) throws InterruptedException {
    if (cachedStockInfo.containsKey(symbol)) {
      return cachedStockInfo.get(symbol);
    }
    String url =
        String.format(
            "https://api.nasdaq.com/api/quote/%s/info?assetclass=stocks", symbol.toUpperCase());
    ResponseData<StockInfo> response = GSON.fromJson(performGet(url), infoResponseType);
    if (response.getData() == null) {
      // attempt for an etf asset class
      url =
          String.format(
              "https://api.nasdaq.com/api/quote/%s/info?assetclass=etf", symbol.toUpperCase());
      response = GSON.fromJson(performGet(url), infoResponseType);
    }
    cachedStockInfo.put(symbol, response.getData());
    return response.getData();
  }

  /**
   * @param symbol The symbol to look up.
   * @return A Info data object.
   * @throws IOException Throws an exception from REST request.
   */
  public static Summary getSummary(String symbol) throws IOException, InterruptedException {
    if (cachedSummary.containsKey(symbol)) {
      return cachedSummary.get(symbol);
    }
    String url =
        String.format(
            "https://api.nasdaq.com/api/quote/%s/summary?assetclass=stocks", symbol.toUpperCase());
    String dataStr = performGet(url);
    ResponseData<Summary> response = GSON.fromJson(dataStr, summaryResponseType);
    if (response.getData() == null) {
      // attempt for an etf asset class
      url =
          String.format(
              "https://api.nasdaq.com/api/quote/%s/summary?assetclass=etf", symbol.toUpperCase());
      response = GSON.fromJson(performGet(url), summaryResponseType);
    }
    cachedSummary.put(symbol, response.getData());
    return response.getData();
  }

  /**
   * Gets a list of company dividend profiles give a set of symbols.
   *
   * @param symbols The symbols to return.
   * @return A list of dividend profiles.
   * @throws IOException An exception at service error.
   */
  public static Map<String, DividendProfile> getDividendProfiles(Set<String> symbols)
      throws IOException, InterruptedException {
    Map<String, DividendProfile> map = new HashMap<>();
    for (String s : symbols) {
      DividendProfile profile = getDividendProfile(s);
      map.put(s, profile);
    }
    return map;
  }

  /**
   * Gets a list of company profiles give a set of symbols.
   *
   * @param symbols The symbols to return.
   * @return A list of company profiles.
   * @throws IOException An exception at service error.
   */
  public static List<CompanyProfile> getCompanyProfiles(Set<String> symbols) throws IOException {
    List<CompanyProfile> companyProfiles = new ArrayList<>();
    for (String s : symbols) {
      CompanyProfile profile = getCompanyProfile(s);
      if (profile != null) {
        companyProfiles.add(profile);
      }
    }
    return companyProfiles;
  }

  /**
   * Gets a list of stock info give a set of symbols.
   *
   * @param symbols The symbols to return.
   * @return A list of stock info.
   * @throws IOException An exception at service error.
   */
  public static Map<String, StockInfo> getAllInfo(Set<String> symbols)
      throws IOException, InterruptedException {
    Map<String, StockInfo> map = new HashMap<>();
    for (String s : symbols) {
      StockInfo stockInfo = getInfo(s);
      map.put(s, stockInfo);
    }
    return map;
  }

  /**
   * Performs a get request to Nasdaq site.
   *
   * @param url The url to perform rest request.
   * @return A string representation of return.
   */
  private static String performGet(String url) {
    HttpUriRequest request =
        RequestBuilder.get()
            .setUri(url)
            .setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*")
            .setHeader(HttpHeaders.HOST, "api.nasdaq.com")
            .setHeader(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.3 Safari/605.1.15")
            .setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-us")
            .setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
            .build();

    for (int i = 0; i < 3; i++) {
      try (CloseableHttpClient httpclient =
          HttpClients.custom().setDefaultRequestConfig(config).build()) {
        try (CloseableHttpResponse response = httpclient.execute(request)) {
          return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
        }
      } catch (IOException e) {
        log.error(
            String.format(
                "Exception occurred for the following:\n%s\n%s\nAttempting %d of 3.",
                e.getLocalizedMessage(), url, i + 1));
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {
          log.error(ex.getLocalizedMessage());
          i = 3;
        }
      }
    }
    return null;
  }

  /** Clears cached map. */
  public static void clearCache() {
    cachedCompanyProfile.clear();
    cachedDividendProfile.clear();
    cachedStockInfo.clear();
    cachedSummary.clear();
  }
}
