package com.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class CompanyService implements ICompanyService {

    @Override
    public Company getTopLevelParent(Company child) {
        if (child == null) {
            return null;
        }
        Company current = child;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    @Override
    public long getEmployeeCountForCompanyAndChildren(Company company, List<Company> companies) {
        if (company == null) {
            return 0;
        }

       
        Map<Company, List<Company>> childrenMap = new HashMap<>();
        for (Company c : companies) {
            Company parent = c.getParent();
            if (parent != null) {
                childrenMap.computeIfAbsent(parent, k -> new ArrayList<>()).add(c);
            }
        }

        return calculateTotalEmployees(company, childrenMap);
    }

    private long calculateTotalEmployees(Company company, Map<Company, List<Company>> childrenMap) {
        long count = company.getEmployeeCount();
        List<Company> children = childrenMap.get(company);
        if (children != null) {
            for (Company child : children) {
                count += calculateTotalEmployees(child, childrenMap);
            }
        }
        return count;
    }
}
