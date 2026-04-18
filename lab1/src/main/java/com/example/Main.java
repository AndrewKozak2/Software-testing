package com.example;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Company root = new Company(null, 100);
        Company child1 = new Company(root, 50);
        Company child2 = new Company(root, 30);
        Company child1_1 = new Company(child1, 10);
        Company child1_2 = new Company(child1, 20);
        
        List<Company> companies = Arrays.asList(root, child1, child2, child1_1, child1_2);
        
        ICompanyService service = new CompanyService();
        
        assert service.getTopLevelParent(child1_1) == root : "Error in getTopLevelParent(child1_1)";
        assert service.getTopLevelParent(root) == root : "Error in getTopLevelParent(root)";
        
        long rootCount = service.getEmployeeCountForCompanyAndChildren(root, companies);
        assert rootCount == 210 : "Error in getEmployeeCountForCompanyAndChildren(root): expected 210, got " + rootCount;
        
        long child1Count = service.getEmployeeCountForCompanyAndChildren(child1, companies);
        assert child1Count == 80 : "Error in getEmployeeCountForCompanyAndChildren(child1): expected 80, got " + child1Count;
        
        System.out.println("Tests passed successfully!");
    }
}
