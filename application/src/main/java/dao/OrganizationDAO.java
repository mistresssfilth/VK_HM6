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

import static generated.Tables.ORGANIZATIONS;

public final class OrganizationDAO implements DAO<Organization> {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static Connection connection;
    private static DSLContext context;

    public OrganizationDAO() {
        try {
            this.connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
            this.context = DSL.using(connection, SQLDialect.POSTGRES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull List<Organization> getAll() {
        List<Organization> organizations = new ArrayList<>();

        final Result<OrganizationsRecord> records = context.fetch(ORGANIZATIONS);
        for (var record : records) {
            organizations.add(new Organization(
                    record.getInn(),
                    record.getName(),
                    record.getCheckingAccount()));
        }
        return organizations;
    }

    @Override
    public Organization getById(@NotNull int inn) {
        final OrganizationsRecord record = context.fetchOne(ORGANIZATIONS, ORGANIZATIONS.INN.eq(inn));
        if (record != null) {
            return new Organization(
                    record.getInn(),
                    record.getName(),
                    record.getCheckingAccount());
        }
        return null;
    }

    @Override
    public void save(@NotNull Organization entity) {
        context
                .insertInto(ORGANIZATIONS, ORGANIZATIONS.INN, ORGANIZATIONS.NAME, ORGANIZATIONS.CHECKING_ACCOUNT)
                .values(entity.getInn(), entity.getName(), entity.getCheckingAccount())
                .execute();
    }

    @Override
    public void update(@NotNull Organization entity) {
        context
                .update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, entity.getName())
                .set(ORGANIZATIONS.CHECKING_ACCOUNT, entity.getCheckingAccount())
                .where(ORGANIZATIONS.INN.eq(entity.getInn()))
                .execute();
    }

    @Override
    public void delete(@NotNull Organization entity) {
        context
                .delete(ORGANIZATIONS)
                .where(ORGANIZATIONS.INN.eq(entity.getInn()))
                .execute();
    }
}
