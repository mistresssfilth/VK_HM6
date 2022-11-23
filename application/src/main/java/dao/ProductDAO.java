package dao;

import commons.JDBCCredentials;
import entity.Product;
import generated.Tables;
import generated.tables.records.ProductsRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static generated.Tables.PRODUCTS;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceIncpection", "SqlResolve"})
public final class ProductDAO implements DAO<Product> {

    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static Connection connection;
    private static DSLContext context;

    public ProductDAO() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
            this.context = DSL.using(connection, SQLDialect.POSTGRES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull List<Product> getAll() {
        final List<Product> products = new ArrayList<>();
        final Result<ProductsRecord> records = context.fetch(PRODUCTS);
        for (var record : records) {
            products.add(new Product(
                    record.getCode(),
                    record.getName()
            ));
        }
        return products;
    }

    @Override
    public @NotNull Product getById(@NotNull int id) {
        final ProductsRecord record = context.fetchOne(PRODUCTS, PRODUCTS.CODE.eq(id));
        if (record != null) {
            return new Product(
                    record.getCode(),
                    record.getName()
            );
        }
        return null;
    }

    @Override
    public void save(@NotNull Product entity) {
        context
                .insertInto(PRODUCTS, PRODUCTS.CODE, PRODUCTS.NAME)
                .values(entity.getCode(), entity.getName())
                .execute();

    }

    @Override
    public void update(@NotNull Product entity) {
        context
                .update(PRODUCTS)
                .set(PRODUCTS.NAME, entity.getName())
                .where(PRODUCTS.CODE.eq(entity.getCode()))
                .execute();
    }

    @Override
    public void delete(@NotNull Product entity) {
        context
                .delete(PRODUCTS)
                .where(PRODUCTS.CODE.eq(entity.getCode()))
                .execute();
    }
}
