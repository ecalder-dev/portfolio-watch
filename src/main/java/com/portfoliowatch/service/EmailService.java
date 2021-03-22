package com.portfoliowatch.service;

import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.model.tdameritrade.TDAmeriPosition;
import com.portfoliowatch.util.TDAmeriPositionDtoComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TDAmeritradeService tdAmeritradeService;

    @Autowired
    private FMPService fmpService;

    private double totalAssetValue = 0;

    private double totalAssetChange = 0;

    public void sendReport(String to) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();

        String templateLocation = "src/main/resources/templates/ReportTemplate.html";
        String result = new String(Files.readAllBytes(Paths.get(templateLocation)));
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        List<TDAmeriPosition> positions = tdAmeritradeService.getTDAccountPositions();
        List<String> tickers = new ArrayList<>();
        for (TDAmeriPosition p : positions) {
            String symbol = p.getInstrument().getSymbol();
            tickers.add(symbol);
        }
        List<FMPProfile> profiles = fmpService.getCompanyProfile(tickers);

        result = result.replace("[first_name]", "there");
        result = result.replace("[stock_table]", createStockTable(positions, profiles));
        result = result.replace("[total_asset_value]", String.valueOf(totalAssetValue));
        String totalAssetChangeColor = totalAssetChange >= 0 ? "green" : "red";
        result = result.replace("[total_asset_change]",
                String.format("<span style=\"color:%s\">$%.2f</span>",
                        totalAssetChangeColor, totalAssetChange));
        helper.setText(result, true);
        helper.setSubject("Daily Watch Report");
        emailSender.send(message);
    }

    private String createStockTable(List<TDAmeriPosition> positions, List<FMPProfile> profiles) {
        if (positions == null || positions.isEmpty()) {
            return "";
        }
        Report report = new Report(positions, profiles);
        Set<String> headers = report.headers;
        StringBuilder htmlBuilder = new StringBuilder().append("<table>");
        htmlBuilder.append("<tr>");
        for (String h: headers) {

            htmlBuilder.append(String.format("<th>%s</th>", h));
        }
        htmlBuilder.append("</tr>");
        int count = 0;
        for (Map<String, String> r: report.rows) {
            if (count++ % 2 == 0) {
                htmlBuilder.append("<tr style=\"background: #E6E8FA\">");
            } else {
                htmlBuilder.append("<tr>");
            }
            for (String h: headers) {
                htmlBuilder.append(String.format("<td>%s</td>", r.get(h)));
            }
            htmlBuilder.append("</tr>");
        }
        htmlBuilder.append("</table>");
        return htmlBuilder.toString();
    }

    class Report {
        private final Set<String> headers;
        private final List<Map<String, String>> rows;
        private final List<TDAmeriPosition> positions;
        private final List<FMPProfile> profiles;

        public Report(List<TDAmeriPosition> positions, List<FMPProfile> profiles) {
            this.rows = new ArrayList<>();
            this.positions = positions;
            this.positions.sort(new TDAmeriPositionDtoComparator());
            this.headers = new LinkedHashSet<>();
            this.profiles = profiles;
            for (TDAmeriPosition position: positions) {
                Optional<FMPProfile> profile = profiles.stream()
                        .filter(p -> p.getSymbol().equals(position.getInstrument().getSymbol())).findFirst();
                profile.ifPresent(fmpProfile -> totalAssetValue += position.getSettledLongQuantity() * fmpProfile.getPrice());
                totalAssetChange += position.getSettledLongQuantity() * position.getCurrentDayProfitLossPercentage();
            }
            this.buildReport();
        }

        private void buildReport() {
            headers.add("Symbol");
            headers.add("Change %");
            headers.add("Current $");
            headers.add("Portfolio %");
            headers.add("Sector");


            for (TDAmeriPosition position: positions) {
                String symbol = position.getInstrument().getSymbol();
                Optional<FMPProfile> optionalFMPProfile = profiles.stream()
                        .filter(p -> p.getSymbol().equals(symbol)).findFirst();
                FMPProfile fmpProfile = optionalFMPProfile.orElse(null);
                Double price = 0.0;
                String companyName = "N/A";
                String sector = "N/A";
                if (fmpProfile != null) {
                    price = fmpProfile.getPrice();
                    companyName = fmpProfile.getCompanyName();
                    sector = fmpProfile.getIsEtf() ? "ETF" : fmpProfile.getSector();
                }

                Map<String, String> columns = new HashMap<>();
                columns.put("Symbol", String.format("<label title=\"%s\">%s</label>", companyName, symbol));
                columns.put("Change %", String.format("%.2f%%", position.getCurrentDayProfitLossPercentage()));
                columns.put("Current $", String.format("$%.2f", price));
                columns.put("Portfolio %", String.format("%.2f%%", ((position.getSettledLongQuantity() * price) /
                        totalAssetValue) * 100));
                columns.put("Sector", sector);
                rows.add(columns);
            }
        }
    }
}