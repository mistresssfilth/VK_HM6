package report;

import commons.JDBCCredentials;
import entity.Organization;
import entity.Product;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static generated.Tables.*;

public class ReportManager {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private DSLContext context;
    private Connection connection;


    public ReportManager() {
        try {
            this.connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
            this.context = DSL.using(connection, SQLDialect.POSTGRES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public @NotNull List<Organization> getProvidersByCountProducts() {
        List<Organization> organizations = new ArrayList<>();
        final Result<Record3<Integer, String, Integer>> records = context
                .select(
                        ORGANIZATIONS.INN,
                        ORGANIZATIONS.NAME,
                        ORGANIZATIONS.CHECKING_ACCOUNT)
                .from(ORGANIZATIONS)
                .join(INVOICES).on(INVOICES.ORG_ID.eq(ORGANIZATIONS.INN))
                .join(POSITIONS).on(POSITIONS.INVOICE_ID.eq(INVOICES.ID))
                .groupBy(ORGANIZATIONS.INN,
                        ORGANIZATIONS.NAME,
                        ORGANIZATIONS.CHECKING_ACCOUNT)
                .orderBy(DSL.sum(POSITIONS.COUNT).desc())
                .limit(10)
                .fetch();
        for (var record : records) {
            organizations.add(new Organization(
                    record.value1(),
                    record.value2(),
                    record.value3()
            ));
        }
        return organizations;

    }

    public @NotNull List<Organization> getProvidersWithCountProductsByValue(int value) {
        List<Organization> organizations = new ArrayList<>();
        final Result<Record3<Integer, String, Integer>> records = context
                .select(
                        ORGANIZATIONS.INN,
                        ORGANIZATIONS.NAME,
                        ORGANIZATIONS.CHECKING_ACCOUNT
                )
                .from(ORGANIZATIONS)
                .join(INVOICES).on(INVOICES.ORG_ID.eq(ORGANIZATIONS.INN))
                .join(POSITIONS).on(POSITIONS.INVOICE_ID.eq(INVOICES.ID))
                .where(POSITIONS.COUNT.greaterThan(value))
                .fetch();
        for (var record : records) {
            organizations.add(new Organization(
                    record.value1(),
                    record.value2(),
                    record.value3()
            ));
        }
        return organizations;

    }

    public Map<Product, Double> getAveragePrice(Date begin, Date end) {
        Map<Product, Double> map = new HashMap<>();
        final var records = context
                .select(PRODUCTS.CODE, PRODUCTS.NAME, DSL.avg(POSITIONS.PRICE))
                .from(POSITIONS)
                .join(INVOICES).on(INVOICES.ID.eq(POSITIONS.INVOICE_ID))
                .join(PRODUCTS).on(POSITIONS.PRODUCT_ID.eq(PRODUCTS.CODE))
                .where(INVOICES.DATE.between(begin.toLocalDate(), end.toLocalDate()))
                .groupBy(PRODUCTS.CODE, PRODUCTS.NAME)
                .fetch();
        for (var record : records){
            Product product = new Product(record.value1(), record.value2());
            if(!map.containsKey(product))
                map.put(product, record.value3().doubleValue());
        }
        return map;
    }

    public @NotNull Map<Organization, List<Product>> getProductsForPeriod(Date begin, Date end) {
        Map<Organization, List<Product>> products = new HashMap<>();
        final var records = context
                .select(ORGANIZATIONS.INN, ORGANIZATIONS.NAME, ORGANIZATIONS.CHECKING_ACCOUNT,
                        PRODUCTS.CODE, PRODUCTS.NAME)
                .from(ORGANIZATIONS)
                .leftJoin(INVOICES).on(INVOICES.ORG_ID.eq(ORGANIZATIONS.INN))
                .leftJoin(POSITIONS).on(POSITIONS.INVOICE_ID.eq(INVOICES.ID))
                .leftJoin(PRODUCTS).on(POSITIONS.PRODUCT_ID.eq(PRODUCTS.CODE))
                .where(INVOICES.DATE.between(begin.toLocalDate(), end.toLocalDate()))
                .fetch();
        for (var record : records) {
            Organization org = new Organization(
                    record.value1(),
                    record.value2(),
                    record.value3()
            );
            if (!products.containsKey(org))
                products.put(org, new ArrayList<>());
            Product product = null;
            if (record.value5() != null) {
                product = new Product(
                        record.value4(),
                        record.value5()
                );
            }
            products.get(org).add(product);
        }
        return products;

    }
    public @NotNull Map<Date, Map<Product, Map<Integer, Integer>>> getCountAndPrice(Date begin, Date end){
        Map<Date, Map<Product, Map<Integer, Integer>>> map = new HashMap<>();
        Map<Product, Integer> count = new HashMap<>();
        Map<Product, Integer> summary = new HashMap<>();

        var records = context
                .select(INVOICES.DATE, PRODUCTS.CODE, PRODUCTS.NAME, DSL.sum(POSITIONS.COUNT), DSL.sum(POSITIONS.PRICE))
                .from(PRODUCTS)
                .leftJoin(POSITIONS).on(POSITIONS.PRODUCT_ID.eq(PRODUCTS.CODE))
                .leftJoin(INVOICES).on(POSITIONS.INVOICE_ID.eq(INVOICES.ID))
                .where(INVOICES.DATE.between(begin.toLocalDate(), end.toLocalDate()))
                .groupBy(INVOICES.DATE, PRODUCTS.NAME, PRODUCTS.CODE)
                .fetch();

        for (var record : records){
            Product product = new Product(
                    record.value2(),
                    record.value3()
            );
            if(!count.containsKey(product))
                count.put(product, 0);
            if(!summary.containsKey(product))
                summary.put(product, 0);

            count.put(product, count.get(product) + record.value4().intValue());
            summary.put(product, summary.get(product) + record.value5().intValue());

            Date date = Date.valueOf(record.value1());

            if(!map.containsKey(date))
                map.put(date, new HashMap<>());

            Map<Integer, Integer> map1 = new HashMap<>();
            map1.put(count.get(product), summary.get(product));

            map.get(date).put(product, map1);

        }
        return map;
    }
}
