package com.portfoliowatch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.portfoliowatch.model.financialmodelingprep.FMPNews;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

@Service
public class FMPService {

    @Value("${financial-modeling-prep.api-key}")
    private String apiKey;

    private final Gson gson;

    private final Type fmpProfileListType = new TypeToken<ArrayList<FMPProfile>>(){}.getType();

    private final Type fmpNewsListType = new TypeToken<ArrayList<FMPNews>>(){}.getType();

    private Date lastNewsPull;

    private List<FMPNews> cachedNews;

    private final long cacheRefreshRate = 21600000; //6 hours

    public FMPService() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public List<FMPProfile> getCompanyProfile(Set<String> symbols) throws URISyntaxException, IOException {
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

    public List<FMPNews> getNews(Set<String> symbols, int daysBefore) throws URISyntaxException, IOException {
        String FMP_URL = "https://financialmodelingprep.com/api/v3/stock_news";
        Date nowDate = new Date();
        Date startDate = this.subtractDays(nowDate, daysBefore);

        if (lastNewsPull != null && cachedNews != null
                && nowDate.getTime() - lastNewsPull.getTime() < cacheRefreshRate) {
            return cachedNews;
        }

        if (cachedNews == null) {
            cachedNews = new ArrayList<>();
        } else {
            cachedNews.clear();
        }

        for (String symbol: symbols) {
            URIBuilder builder = new URIBuilder(FMP_URL);
            builder.addParameter("tickers", symbol);
            builder.addParameter("apikey", apiKey);
            HttpGet get = new HttpGet(builder.build());
            try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
                try (CloseableHttpResponse response = httpclient.execute(get)) {
                    String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                    List<FMPNews> fmpNews = gson.fromJson(responseStr, fmpNewsListType);
                    for (FMPNews news: fmpNews) {
                        if (startDate.before(news.getPublishedDate())) {
                            cachedNews.add(news);
                        }
                    }
                }
            }
        }

        lastNewsPull = nowDate;

        return cachedNews;
    }

    private Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }
}
