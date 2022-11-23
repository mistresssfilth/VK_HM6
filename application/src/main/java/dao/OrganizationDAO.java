package dao;

import commons.JDBCCredentials;
import entity.Organization;
import generated.Tables;
import generated.tables.records.OrganizationsRecord;
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

public final class OrganizationDAO implements DAO<Organization> {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static Connection connection;

    public OrganizationDAO() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public @NotNull List<Organization> getAll() {
        List<Organization> organizations = new ArrayList<>();
        try {
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final Result<OrganizationsRecord> records = context.fetch(Tables.ORGANIZATIONS);
            for (var record : records) {
                organizations.add(new Organization(
                        record.getInn(),
                        record.getName(),
                        record.getCheckingAccount()));
            }
            return organizations;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public Organization getById(@NotNull int inn) {
        try {
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final OrganizationsRecord record = context.fetchOne(Tables.ORGANIZATIONS, Tables.ORGANIZATIONS.INN.eq(inn));
            if (record != null){
                return new Organization(
                        record.getInn(),
                        record.getName(),
                        record.getCheckingAccount());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NotNull Organization entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .insertInto(Tables.ORGANIZATIONS, Tables.ORGANIZATIONS.INN, Tables.ORGANIZATIONS.NAME, Tables.ORGANIZATIONS.CHECKING_ACCOUNT)
                    .values(entity.getInn(), entity.getName(), entity.getCheckingAccount())
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(@NotNull Organization entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .update(Tables.ORGANIZATIONS)
                    .set(Tables.ORGANIZATIONS.NAME, entity.getName())
                    .set(Tables.ORGANIZATIONS.CHECKING_ACCOUNT, entity.getCheckingAccount())
                    .where(Tables.ORGANIZATIONS.INN.eq(entity.getInn()))
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void delete(@NotNull Organization entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .delete(Tables.ORGANIZATIONS)
                    .where(Tables.ORGANIZATIONS.INN.eq(entity.getInn()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
