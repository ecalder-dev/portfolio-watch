package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Company;
import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private NasdaqService nasdaqApi;

    /**
     * Gets all companies from database.
     * @return List of companies.
     */
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    /**
     * Gets a company given a symbol. If it's not found, it will perform a request from nasdaq service to
     * get the necessary information and save to database.
     * @param symbol The symbol to get company with.
     * @return Company object.
     */
    public Company getCompany(String symbol) {
        Company foundCompany = companyRepository.findById(symbol).orElse(null);
        if (foundCompany == null) {
            try {
                CompanyProfile companyProfile = nasdaqApi.getCompanyProfile(symbol);
                if (companyProfile != null) {
                    Company company = new Company();
                    company.setSymbol(companyProfile.getSymbol().getValue());
                    company.setName(companyProfile.getCompanyName().getValue());
                    company.setDescription(companyProfile.getCompanyDescription().getValue());
                    company.setAddress(companyProfile.getAddress().getValue());
                    company.setUrl(companyProfile.getCompanyUrl().getValue());
                    company.setIndustry(companyProfile.getIndustry().getValue());
                    company.setSector(companyProfile.getSector().getValue());
                    foundCompany = companyRepository.save(company);
                } else {
                    log.error("Company profile not found from Nasdaq: {}", nasdaqApi);
                }
            } catch (IOException e) {
                log.error("Unable to reach Nasdaq service: {}", e.getLocalizedMessage());
            }
        }
        return foundCompany;
    }

    /**
     * Gets a list of companies given a set of symbols. If it's not found, it will perform a request from
     * nasdaq service to get the necessary information and save to database.
     * @param symbols The set of symbols to get company with.
     * @return List of Company objects.
     */
    public List<Company> getCompanies(Set<String> symbols) {
        Set<String> symbolsTemp = new HashSet<>(symbols);
        List<Company> currentCompanies = companyRepository.findAllById(symbols);
        Set<String> symbolsFound = currentCompanies.stream().map(Company::getSymbol).collect(Collectors.toSet());
        symbolsTemp.removeAll(symbolsFound);
        if (!symbolsTemp.isEmpty()) {
            try {
                List<CompanyProfile> companyProfiles = nasdaqApi.getCompanyProfiles(symbolsTemp);
                for (CompanyProfile companyProfile: companyProfiles) {
                    log.info("Adding new profile: {}", companyProfile.toString());
                    Company company = new Company();
                    company.setSymbol(companyProfile.getSymbol().getValue());
                    company.setName(companyProfile.getCompanyName().getValue());
                    company.setDescription(companyProfile.getCompanyDescription().getValue());
                    company.setAddress(companyProfile.getAddress().getValue());
                    company.setUrl(companyProfile.getCompanyUrl().getValue());
                    company.setIndustry(companyProfile.getIndustry().getValue());
                    company.setSector(companyProfile.getSector().getValue());
                    currentCompanies.add(companyRepository.save(company));
                }
            } catch (IOException e) {
                log.error("Unable to reach Nasdaq service: {}", e.getLocalizedMessage());
            }
        }
        return currentCompanies;
    }
}
