package com.portfoliowatch.service;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final String templateLocation = "src/main/resources/templates/ReportTemplate.html";

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private PositiionService positiionService;

    @Autowired QuoteService quoteService;

    public void sendReport(String to, Long portfolioId) throws IOException, MessagingException, ExecutionException, InterruptedException {
        logger.info(String.valueOf(portfolioId));
        MimeMessage message = emailSender.createMimeMessage();

        String result = new String(Files.readAllBytes(Paths.get(templateLocation)));
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        List<Position> positions = positiionService.getTransactionsByPortfolio(portfolioId).get();
        List<String> tickers = positions.stream().map(Position::getTicker).collect(Collectors.toList());
        List<Quote> quotes = quoteService.getQuotes(tickers).get();

        result = result.replace("[first_name]", "TestUser");
        result = result.replace("[stock_table]", createStockTable(positions, quotes));

        helper.setText(result, true);
        helper.setSubject("Daily Watch Report");
        emailSender.send(message);

    }

    private String createStockTable(List<Position> positions, List<Quote> quotes) {
        if (positions == null || positions.isEmpty()) {
            return "";
        }
        Report report = new Report(positions, quotes);
        Set<String> headers = report.headers;
        StringBuilder tagBuilder = new StringBuilder().append("<table>");
        tagBuilder.append("<tr>");
        for (String h: headers) {
            tagBuilder.append(String.format("<th>%s</th>", h));
        }
        tagBuilder.append("</tr>");
        for (Map<String, String> r: report.rows) {
            tagBuilder.append("<tr>");
            for (String h: headers) {
                tagBuilder.append(String.format("<td>%s</td>", r.get(h)));
            }
            tagBuilder.append("</tr>");
        }
        tagBuilder.append("</table>");
        return tagBuilder.toString();
    }

    private class Report {
        private final Set<String> headers;
        private final List<Map<String, String>> rows;
        private final Map<String, Quote> quoteMap;
        private final Map<String, Position> positionMap;

        public Report(List<Position> positions, List<Quote> quotes) {
            rows = new ArrayList<>();
            quoteMap = new HashMap<>();
            positionMap = new HashMap<>();
            headers = new HashSet<>();
            quotes.forEach(quote -> {
                quoteMap.put(quote.getTicker(), quote);
            });
            positions.forEach(position -> {
                positionMap.put(position.getTicker(), position);
            });

            this.buildReport();
        }

        private void buildReport() {
            headers.add("Ticker");
            headers.add("Change Percent");
            headers.add("Total Price Change");

            for (String ticker: positionMap.keySet()) {
                Position position = positionMap.get(ticker);
                Quote quote = quoteMap.get(ticker);

                String totalChange = quote != null ? String.valueOf(quote.getChange() * position.getShareCount()) : "NA";
                String changePercent = quote != null ? String.valueOf(quote.getChangePercent()) : "NA";

                Map<String, String> columns = new HashMap<>();
                columns.put("Ticker", String.format("%s (%s)", ticker, ticker));
                columns.put("Change Percent", changePercent);
                columns.put("Total Price Change", String.format("$%s", totalChange));
                rows.add(columns);
            }
        }
    }
}
