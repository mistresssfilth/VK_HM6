package dao;

import commons.JDBCCredentials;
import entity.Organization;
import entity.Product;
import generated.Tables;
import generated.tables.records.OrganizationsRecord;
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

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceIncpection", "SqlResolve"})
public final class ProductDAO implements DAO<Product> {

    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static  Connection connection;

    public ProductDAO() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull List<Product> getAll() {
        final List<Product> products = new ArrayList<>();
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final Result<ProductsRecord> records = context.fetch(Tables.PRODUCTS);
            for (var record : records) {
                    products.add(new Product(
                            record.getCode(),
                            record.getName()
                    ));
        }
            return products;
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new IllegalStateException();

    }

    @Override
    public @NotNull Product getById(@NotNull int id) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final ProductsRecord record = context.fetchOne(Tables.PRODUCTS, Tables.PRODUCTS.CODE.eq(id));
            if (record != null){
                return new Product(
                        record.getCode(),
                        record.getName()
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NotNull Product entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .insertInto(Tables.PRODUCTS, Tables.PRODUCTS.CODE, Tables.PRODUCTS.NAME)
                    .values(entity.getCode(), entity.getName())
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void update(@NotNull Product entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .update(Tables.PRODUCTS)
                    .set(Tables.PRODUCTS.NAME, entity.getName())
                    .where(Tables.PRODUCTS.CODE.eq(entity.getCode()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Product entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .delete(Tables.PRODUCTS)
                    .where(Tables.PRODUCTS.CODE.eq(entity.getCode()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
