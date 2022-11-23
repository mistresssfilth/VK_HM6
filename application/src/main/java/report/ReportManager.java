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
    private static Connection connection;

    public ReportManager() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Organization> getProvidersByCountProducts() {
        List<Organization> organizations = new ArrayList<>();

        final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
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

    public List<Organization> getProvidersWithCountProductsByValue(int value) {
        List<Organization> organizations = new ArrayList<>();

        final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
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

    public Integer getAveragePrice(Date begin, Date end, int id) {

        final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
        final var records = context
                .select(DSL.avg(POSITIONS.PRICE))
                .from(POSITIONS)
                .join(INVOICES).on(INVOICES.ID.eq(POSITIONS.INVOICE_ID))
                .where(INVOICES.DATE.between(begin.toLocalDate(), end.toLocalDate()))
                .and(POSITIONS.PRODUCT_ID.eq(id))
                .fetchOne();
        return records.value1().intValue();
    }

    public Map<Organization, List<Product>> getProductsForPeriod(Date begin, Date end) {
        Map<Organization, List<Product>> products = new HashMap<>();

        final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
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

//    public Map<Organization, List<Product>> getCountAndPrice(Date begin, Date end){
//        Map<Organization, List<Product>> products = new HashMap<>();
//        try (var preparedStatement = connection.prepareStatement(
//                "SELECT products.id as prod_id, products.name as product_name, products.code, SUM(count), price " +
//                        "FROM products " +
//                        "LEFT JOIN positions ON positions.product_id = products.id " +
//                        "LEFT JOIN invoices ON positions.invoice_id = invoices.id " +
//                        "WHERE invoices.date BETWEEN '2020-03-05' AND '2022-12-12' " +
//                        "GROUP BY products.id, price "))
//        {
//            preparedStatement.setDate(1, begin);
//            preparedStatement.setDate(2, end);
//            try(var resultSet = preparedStatement.getResultSet()){
//                while(resultSet.next()){
//                    Product product = new Product(
//                                resultSet.getInt("prod_id"),
//                                resultSet.getString("product_name"),
//                                resultSet.getInt("code"));
//                    }
//                }
//            }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return products;
//    }
}
