package com.portfoliowatch.service;

import com.portfoliowatch.model.dbo.Company;
import com.portfoliowatch.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Map<String, Company> getCompanyMap(Set<String> symbols) {
        Map<String, Company> map = new HashMap<>();
        List<Company> companies = companyRepository.findAllById(symbols);
        for (Company company: companies) {
            map.put(company.getSymbol(), company);
        }
        return map;
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }


}
