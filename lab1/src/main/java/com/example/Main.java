package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    private static int testsPassed = 0;

    public static void main(String[] args) {
        ICompanyService service = new CompanyService();

        try {

            // Test 1: null на вході
            assert service.getTopLevelParent(null) == null : "Test 1 Failed";
            reportTestSuccess(1);

            // Test 2: Компанія без батьків (корінь)
            Company root = new Company(null, 100);
            assert service.getTopLevelParent(root) == root : "Test 2 Failed";
            reportTestSuccess(2);

            // Test 3: Пряма дочірня компанія (2 рівні)
            Company child = new Company(root, 50);
            assert service.getTopLevelParent(child) == root : "Test 3 Failed";
            reportTestSuccess(3);

            // Test 4: Глибока ієрархія (3 рівні)
            Company grandchild = new Company(child, 20);
            assert service.getTopLevelParent(grandchild) == root : "Test 4 Failed";
            reportTestSuccess(4);

            // Test 5: Дуже глибока ієрархія (5 рівнів)
            Company level4 = new Company(grandchild, 10);
            Company level5 = new Company(level4, 5);
            assert service.getTopLevelParent(level5) == root : "Test 5 Failed";
            reportTestSuccess(5);

            // Test 6: null компанія
            assert service.getEmployeeCountForCompanyAndChildren(null, new ArrayList<>()) == 0 : "Test 6 Failed";
            reportTestSuccess(6);

            // Test 7: Порожній список компаній (повинна повернути кількість працівників самої компанії)
            assert service.getEmployeeCountForCompanyAndChildren(root, Collections.emptyList()) == 100 : "Test 7 Failed";
            reportTestSuccess(7);

            // Test 8: Компанія з 0 працівників і без дітей
            Company zeroEmp = new Company(null, 0);
            assert service.getEmployeeCountForCompanyAndChildren(zeroEmp, Arrays.asList(zeroEmp)) == 0 : "Test 8 Failed";
            reportTestSuccess(8);

            // Test 9: Компанія без дочірніх підрозділів у списку
            assert service.getEmployeeCountForCompanyAndChildren(root, Arrays.asList(root)) == 100 : "Test 9 Failed";
            reportTestSuccess(9);

            // Test 10: Прямі дочірні підрозділи (1 рівень дітей)
            Company c1 = new Company(root, 50);
            Company c2 = new Company(root, 30);
            List<Company> list10 = Arrays.asList(root, c1, c2);
            assert service.getEmployeeCountForCompanyAndChildren(root, list10) == 180 : "Test 10 Failed";
            reportTestSuccess(10);

            // Test 11: Складна ієрархія (кілька рівнів)
            // root(100) -> c1(50), c2(30)
            // c1(50) -> gc1(20), gc2(10)
            Company gc1 = new Company(c1, 20);
            Company gc2 = new Company(c1, 10);
            List<Company> list11 = Arrays.asList(root, c1, c2, gc1, gc2);
            assert service.getEmployeeCountForCompanyAndChildren(root, list11) == 210 : "Test 11 Failed";
            reportTestSuccess(11);

            // Test 12: Компанії в списку, що не належать до ієрархії
            Company otherRoot = new Company(null, 500);
            Company otherChild = new Company(otherRoot, 200);
            List<Company> list12 = Arrays.asList(root, c1, otherRoot, otherChild);
            // Повинно врахувати тільки root(100) та c1(50) = 150
            assert service.getEmployeeCountForCompanyAndChildren(root, list12) == 150 : "Test 12 Failed";
            reportTestSuccess(12);

            System.out.println("\nВсього пройдено тестів: " + testsPassed + " з 12");
            System.out.println("Загальна оцінка: " + testsPassed + " бал(ів)");

        } catch (AssertionError e) {
            System.err.println("\nПОМИЛКА: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void reportTestSuccess(int testNum) {
        testsPassed++;
        System.out.println("Тест " + testNum + " пройдено успішно.");
    }
}
