package com.example;

import java.util.List;

public interface ICompanyService {
    /**
     * @param child 
     *              
     * @return 
     */
    Company getTopLevelParent(Company child);

    /**
     * @param company                   
     * @param companies 
     * @return 
     */
    long getEmployeeCountForCompanyAndChildren(Company company, List<Company> companies);
}
