package com.portfoliowatch.api;

import com.google.gson.Gson;
import com.portfoliowatch.model.wsj.WSJId;
import com.portfoliowatch.model.wsj.WSJInstrument;
import com.portfoliowatch.model.wsj.WSJResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class WallStreetJournalAPI {

    private static final Gson GSON = new Gson();

    public static List<WSJInstrument> getIndices() throws URISyntaxException, IOException {
        String SRV_TYPE = "mdc_quotes";
        String INDEX_URL = "https://www.wsj.com/market-data/stocks/us/indexes";
        List<WSJInstrument> responseData = null;
        WSJId id = new WSJId();
        id.setApplication("WSJ");
        id.addInstrument("INDEX/US//DJIA", "Industrial Average");
        id.addInstrument("INDEX/US//SPX", "500 Index");
        id.addInstrument("INDEX/US//COMP", "Composite");
        id.addInstrument("INDEX/US//RUT", "Russell 2000");
        id.setExpanded(true);
        id.setRefreshInterval(60000);
        id.setServerSideType(SRV_TYPE);

        URIBuilder builder = new URIBuilder(INDEX_URL);
        builder.addParameter("id", GSON.toJson(id));
        builder.addParameter("type", SRV_TYPE);
        HttpGet get = new HttpGet(builder.build());

        try (CloseableHttpClient httpclient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpclient.execute(get)) {
                String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
                WSJResponse instrumentResponse = GSON.fromJson(responseStr, WSJResponse.class);
                if (instrumentResponse.getData() != null) {
                    responseData = instrumentResponse.getData().getInstruments();
                }
            }
        }
        return responseData;
    }

}
