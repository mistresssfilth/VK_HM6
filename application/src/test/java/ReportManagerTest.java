import entity.Organization;
import entity.Product;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import report.ReportManager;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportManagerTest {
    private static @NotNull ReportManager reportManager;
    private static final @NotNull Date BEGIN = Date.valueOf("2019-03-06");
    private static final @NotNull Date END = Date.valueOf("2022-05-03");

    @BeforeAll
    static void init(){
        reportManager = new ReportManager();
    }
    @Test
    void getProvidersByCountProducts(){
        List<Organization> organizationList = new ArrayList<>();
        organizationList.add(new Organization(1287,"Provider 1",  258193));
        organizationList.add(new Organization(1589,"Provider 2",  158538));
        organizationList.add(new Organization(1481,"Provider 4",  614783));

        assertEquals(organizationList, reportManager.getProvidersByCountProducts());

    }
    @Test
    void getProvidersWithCountProductsByValue(){
        List<Organization> organizationList = new ArrayList<>();
        organizationList.add(new Organization(1589,"Provider 2",  158538));
        organizationList.add(new Organization(1287,"Provider 1",  258193));

        assertEquals(organizationList, reportManager.getProvidersWithCountProductsByValue(2));
    }
    @Test
    void getAveragePrice(){
        String expected = "{Product(code=101, name=Item 1)=1100.0, " +
                "Product(code=242, name=Item 5)=390.0, " +
                "Product(code=112, name=Item 2)=3369.0, " +
                "Product(code=353, name=Item 4)=1300.0}";
        assertEquals(expected.trim(), reportManager.getAveragePrice(BEGIN, END).toString().trim());
    }
    @Test
    void getProductsForPeriod(){
        Map<Organization, List<Product>> map = new HashMap<>();
        List<Product> productsByProvider1 = new ArrayList<>();
        List<Product> productsByProvider2 = Arrays.asList(new Product(112, "Item 2"));
        productsByProvider1.add(new Product(242,"Item 5"));
        productsByProvider1.add(new Product(353,"Item 4"));
        productsByProvider1.add(new Product(101,"Item 1"));
        map.put(new Organization(1287,"Provider 1",258193), productsByProvider1);
        map.put(new Organization(1589,"Provider 2",158538), productsByProvider2);

        assertEquals(map, reportManager.getProductsForPeriod(BEGIN, END));
    }

    @Test
    void getCountAndPrice(){
        String expected = "{2022-05-03=[{Product(code=101, name=Item 1)={1=1100}}, " +
                "{Product(code=353, name=Item 4)={2=1300}}], " +
                "2021-08-10=[{Product(code=242, name=Item 5)={10=390}}], " +
                "2019-03-06=[{Product(code=112, name=Item 2)={4=3369}}]}";
        assertEquals(expected.trim(), reportManager.getCountAndPrice(BEGIN, END).toString().trim());

    }
}
