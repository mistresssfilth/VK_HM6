import commons.FlywayInit;
import entity.Organization;
import entity.Product;
import report.ReportManager;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        FlywayInit.initDb();

        Date begin = Date.valueOf("2019-03-06");
        Date end = Date.valueOf("2022-05-03");

        ReportManager reportManager =  new ReportManager();
        System.out.println("Report #1");
        for (Organization organization : reportManager.getProvidersByCountProducts()){
            System.out.println(organization.getName() + "\t\t" + organization.getInn() + "\t\t" + organization.getCheckingAccount());
        }
        System.out.println("\nReport #2");
        List<Organization> organizationsList = reportManager.getProvidersWithCountProductsByValue(3);
        for(Organization organization : organizationsList){
            System.out.println(organization.getName() + "\t\t" + organization.getInn() + "\t\t" + organization.getCheckingAccount());
        }

        System.out.println("\nReport #3");
        Map<Date, Map<Product, Map<Integer, Integer>>> countAndPrice = reportManager.getCountAndPrice(begin, end);
        for (Map.Entry<Date, Map<Product, Map<Integer, Integer>>> entry : countAndPrice.entrySet()){
            System.out.println(entry.getKey() + "\t\t");
            for (Map.Entry<Product, Map<Integer, Integer>> item : entry.getValue().entrySet()){
                System.out.println(item.getKey() + "\t\t");
                System.out.println("count" + "\t" + "sum");
                for(Map.Entry<Integer, Integer> count : item.getValue().entrySet()){
                    System.out.println(count.getKey() + "\t\t" + count.getValue());
                }
            }
        }

        System.out.println("\nReport #4");
        Map<Product, Double> averagePrice = reportManager.getAveragePrice(begin, end);
        for(Map.Entry<Product, Double> entry : averagePrice.entrySet()){
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }

        System.out.println("\nReport #5");
        Map<Organization, List<Product>> map = reportManager.getProductsForPeriod(begin, end);
        for (Map.Entry<Organization, List<Product>> entry : map.entrySet()){
            System.out.println(entry.getKey().getName() + "\t\t" + entry.getKey().getInn() + "\t\t" + entry.getKey().getCheckingAccount());
            if (entry.getValue().size() < 1)
                System.out.println("The organization didn't produce products in the specified period");
            else {
                for (Product product : entry.getValue())
                    System.out.println(product.getName() + "\t\t" + product.getCode());
            }
        }
    }
}
