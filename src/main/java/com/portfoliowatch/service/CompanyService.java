package com.portfoliowatch.service;

import com.portfoliowatch.model.entity.Company;
import com.portfoliowatch.model.nasdaq.CompanyProfile;
import com.portfoliowatch.repository.CompanyRepository;
import com.portfoliowatch.service.third.NasdaqAPI;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;

  /**
   * Gets all companies from database.
   *
   * @return List of companies.
   */
  public List<Company> getAllCompanies() {
    return companyRepository.findAll();
  }

  /**
   * Gets a company given a symbol. If it's not found, it will perform a request from nasdaq service
   * to get the necessary information and save to database.
   *
   * @param symbol The symbol to get company with.
   * @return Company object.
   */
  public Company getCompany(String symbol) {
    Company foundCompany = companyRepository.findById(symbol).orElse(null);
    if (foundCompany == null) {
      try {
        CompanyProfile companyProfile = NasdaqAPI.getCompanyProfile(symbol);
        if (companyProfile != null) {
          log.info("Adding new profile: {}", companyProfile);
          log.info(companyProfile.getCompanyDescription().getValue());
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
          log.error("Company profile not found from Nasdaq.");
        }
      } catch (IOException e) {
        log.error("Unable to reach Nasdaq service: {}", e.getLocalizedMessage());
      }
    }
    return foundCompany;
  }

  /**
   * Gets a list of companies given a set of symbols. If it's not found, it will perform a request
   * from nasdaq service to get the necessary information and save to database.
   *
   * @param symbols The set of symbols to get company with.
   * @return List of Company objects.
   */
  public List<Company> getCompanies(Set<String> symbols) {
    Set<String> symbolsTemp = new HashSet<>(symbols);
    List<Company> currentCompanies = companyRepository.findAllById(symbols);
    Set<String> symbolsFound =
        currentCompanies.stream().map(Company::getSymbol).collect(Collectors.toSet());
    symbolsTemp.removeAll(symbolsFound);
    if (!symbolsTemp.isEmpty()) {
      try {
        List<CompanyProfile> companyProfiles = NasdaqAPI.getCompanyProfiles(symbolsTemp);
        for (CompanyProfile companyProfile : companyProfiles) {
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
