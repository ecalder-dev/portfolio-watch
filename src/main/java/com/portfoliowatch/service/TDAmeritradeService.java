package com.portfoliowatch.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.tdameritrade.TDAmeriPosition;
import com.portfoliowatch.model.tdameritrade.TDAmeriQuote;
import com.portfoliowatch.model.tdameritrade.TDAmeriToken;
import com.portfoliowatch.model.tdameritrade.TDAmeriTransaction;
import com.portfoliowatch.repository.TransactionRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TDAmeritradeService {

    private static final Logger logger = LoggerFactory.getLogger(TDAmeritradeService.class);

    private final Gson gson = new Gson();

    private final Type tdAmeriPositionListType = new TypeToken<ArrayList<TDAmeriPosition>>(){}.getType();

    private final Type tdAmeriQuoteMapType = new TypeToken<HashMap<String, TDAmeriQuote>>(){}.getType();

    private final Type tdAmeriTransactionListType = new TypeToken<ArrayList<TDAmeriTransaction>>(){}.getType();

    private final Type errorMapType = new TypeToken<HashMap<String, String>>(){}.getType();

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${td-ameritrade.account-id}")
    private String accountId;

    @Value("${td-ameritrade.redirect}")
    private String redirectUrl;

    @Value("${td-ameritrade.client-id}")
    private  String clientId;

    private TDAmeriToken token;

    public String getLoginURL() {
        try {
            URIBuilder uriBuilder = new URIBuilder("https://auth.tdameritrade.com/auth");
            uriBuilder.addParameter("response_type", "code");
            uriBuilder.addParameter("redirect_uri", redirectUrl);
            uriBuilder.addParameter("client_id", clientId + "%40AMER.OAUTHAP");
            return uriBuilder.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TDAmeriPosition> getTDAccountPositions() throws IOException, URISyntaxException {
        List<TDAmeriPosition> positions = new LinkedList<>();
        String responseJson = "";
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            String TD_AMER_TOKEN_URL = "https://api.tdameritrade.com/v1/accounts";
            URIBuilder builder = new URIBuilder(TD_AMER_TOKEN_URL);
            builder.addParameter("fields", "positions");
            HttpGet get = new HttpGet(builder.build());
            get.addHeader("Authorization", this.getBearerToken());
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                responseJson = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                JsonArray convertedObject = new Gson().fromJson(responseJson, JsonArray.class);
                for (JsonElement accountElement : convertedObject) {
                    JsonObject accountObject = accountElement.getAsJsonObject();
                    JsonObject securitiesAccountObject = accountObject.getAsJsonObject("securitiesAccount");
                    JsonArray positionsArray = securitiesAccountObject.getAsJsonArray("positions");
                    List<TDAmeriPosition> tdAmeriPositions = gson.fromJson(positionsArray.toString(), tdAmeriPositionListType);
                    for (TDAmeriPosition td : tdAmeriPositions) {
                        String symbol = td.getInstrument().getSymbol();
                        Optional<TDAmeriPosition> exists = positions.stream().filter(p -> p.getInstrument().getSymbol().equals(symbol)).findAny();
                        if (exists.isPresent()) {
                            exists.get().applyNewShares(td);
                        } else {
                            positions.add(td);
                        }
                    }
                }
            }
        } catch (JsonSyntaxException jse) {
            if (!StringUtils.isEmpty(responseJson)) {
                Map<String, String> map = gson.fromJson(responseJson, errorMapType);
                if (map.get("error") != null) {
                    throw new IOException(map.get("error"));
                }
            }
        }
        return positions;
    }

    public List<TDAmeriTransaction> getTDTransactions()  throws IOException, URISyntaxException {
        List<TDAmeriTransaction> transactions;
        String responseJson = "";
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            String TD_AMERI_URL = String.format("https://api.tdameritrade.com/v1/accounts/%s/transactions", accountId);
            URIBuilder builder = new URIBuilder(TD_AMERI_URL);
            HttpGet get = new HttpGet(builder.build());
            get.addHeader("Authorization", this.getBearerToken());
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                responseJson = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                transactions = gson.fromJson(responseJson, tdAmeriTransactionListType);
            }
        } catch (JsonSyntaxException jse) {
            throw new IOException(jse);
        }
        return transactions;
    }

    public Map<String, TDAmeriQuote> getTDAccountQuotes(List<String> symbols) {
        Map<String, TDAmeriQuote>quotes = new HashMap<>();
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            String TD_AMER_TOKEN_URL = "https://api.tdameritrade.com/v1/marketdata/quotes";
            URIBuilder builder = new URIBuilder(TD_AMER_TOKEN_URL);
            String symbolsStr = StringUtils.join(symbols, ",");
            builder.addParameter("symbol", symbolsStr);
            HttpGet get = new HttpGet(builder.build());
            get.addHeader("Authorization", this.getBearerToken());
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                String responseJson = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                quotes = gson.fromJson(responseJson, tdAmeriQuoteMapType);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return quotes;
    }

    public TDAmeriToken authorize(String decodedAuthCode) {
        token = this.getNewAccessAndRefreshToken(decodedAuthCode);
        return token;
    }

    private String getBearerToken() {
        if (token == null) { return null; }
        long currentTimeSeconds = System.currentTimeMillis();
        long accessDiff = (currentTimeSeconds - token.getAccessTokenTimestamp()) / 1000;
        long refreshDiff = (currentTimeSeconds - token.getRefreshTokenTimestamp()) / 1000;
        if (refreshDiff >= (token.getRefreshTokenExpiresIn() - 3600)) {
            this.token = this.getNewRefreshToken(token.getRefreshToken());
        } else if (accessDiff >= token.getExpiresIn()) {
            TDAmeriToken newAccessToken = this.getNewAccessToken(token.getRefreshToken());
            if (newAccessToken == null) { return null; }
            token.setAccessToken(newAccessToken.getAccessToken());
            token.setAccessTokenTimestamp(currentTimeSeconds);
        }
        return "Bearer " + token.getAccessToken();
    }

    private TDAmeriToken getNewAccessAndRefreshToken(String authorizationCode) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("code", authorizationCode));
        parameters.add(new BasicNameValuePair("client_id", clientId + "@AMER.OAUTHAP"));
        parameters.add(new BasicNameValuePair("access_type", "offline"));
        parameters.add(new BasicNameValuePair("redirect_uri", redirectUrl));
        parameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        parameters.add(new BasicNameValuePair("refresh_token", ""));
        Long currentTime = System.currentTimeMillis();
        TDAmeriToken tdAmeriToken = performPostToken(parameters);
        tdAmeriToken.setAccessTokenTimestamp(currentTime);
        tdAmeriToken.setRefreshTokenTimestamp(currentTime);
        return tdAmeriToken;

    }

    private TDAmeriToken getNewRefreshToken(String refreshToken) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("client_id", clientId + "@AMER.OAUTHAP"));
        parameters.add(new BasicNameValuePair("access_type", "offline"));
        parameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        parameters.add(new BasicNameValuePair("refresh_token", refreshToken));
        Long currentTime = System.currentTimeMillis();
        TDAmeriToken tdAmeriToken = performPostToken(parameters);
        tdAmeriToken.setRefreshTokenTimestamp(currentTime);
        return tdAmeriToken;
    }

    private TDAmeriToken getNewAccessToken(String refreshToken) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("client_id", clientId + "@AMER.OAUTHAP"));
        parameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        parameters.add(new BasicNameValuePair("refresh_token", refreshToken));
        return performPostToken(parameters);
    }

    private TDAmeriToken performPostToken(List<NameValuePair> parameters) {
        TDAmeriToken token = null;
        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            String TD_AMER_TOKEN_URL = "https://api.tdameritrade.com/v1/oauth2/token";
            URIBuilder builder = new URIBuilder(TD_AMER_TOKEN_URL);
            HttpPost post = new HttpPost(builder.build());
            post.setEntity(new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8.name()));
            try (CloseableHttpResponse response = httpclient.execute(post)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                token = gson.fromJson(responseString, TDAmeriToken.class);
                if (token.getError() != null) {
                    logger.info(token.getError());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return token;
    }
}
