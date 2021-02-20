package com.portfoliowatch.controller;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@RequestMapping("/td")
@RestController
public class TDAmeritradeController {

    private final String TDAMER_AUTH_URL = "https://auth.tdameritrade.com/auth";

    @Value("td-ameritrade.redirect")
    private String redirectUrl;

    @Value("td-ameritrade.client-id")
    private  String clientId;

    @GetMapping("login")
    public String login(HttpServletResponse httpServletResponse) {
        try {
            URIBuilder uriBuilder = new URIBuilder(TDAMER_AUTH_URL);
            uriBuilder.addParameter("response_type", "code");
            uriBuilder.addParameter("redirect_uri", "");
            uriBuilder.addParameter("client_id", "" + "%40AMER.OAUTHAP");
            return uriBuilder.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("oauth")
    public void callback(@RequestParam String code) {
    }
}
