package dao;

import commons.JDBCCredentials;
import entity.Invoice;
import generated.Tables;
import generated.tables.records.InvoicesRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static generated.Tables.INVOICES;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public final class InvoiceDAO implements DAO<Invoice> {

    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static Connection connection;
    private static DSLContext context;

    public InvoiceDAO() {
        try {
            this.connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
            this.context = DSL.using(connection, SQLDialect.POSTGRES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Invoice> getAll() {
        List<Invoice> invoices = new ArrayList<>();

        final Result<InvoicesRecord> records = context.fetch(INVOICES);
        for (var record : records) {
            invoices.add(new Invoice(
                    record.getId(),
                    Date.valueOf((record.getDate().toString())),
                    record.getOrgId()
            ));
        }
        return invoices;
    }

    @Override
    public Invoice getById(@NotNull int id) {
        final InvoicesRecord record = context.fetchOne(INVOICES, INVOICES.ID.eq(id));
        if (record != null) {
            return new Invoice(
                    record.getId(),
                    Date.valueOf((record.getDate().toString())),
                    record.getOrgId()
            );
        }
        return null;
    }

    @Override
    public void save(@NotNull Invoice entity) {
        context
                .insertInto(INVOICES, INVOICES.ID, INVOICES.DATE, INVOICES.ORG_ID)
                .values(entity.getId(), entity.getDate().toLocalDate(), entity.getOrgId())
                .execute();

    }

    @Override
    public void update(@NotNull Invoice entity) {
        context
                .update(INVOICES)
                .set(INVOICES.DATE, entity.getDate().toLocalDate())
                .set(INVOICES.ORG_ID, entity.getOrgId())
                .where(INVOICES.ID.eq(entity.getId()))
                .execute();

    }

    @Override
    public void delete(@NotNull Invoice entity) {
        context
                .delete(INVOICES)
                .where(INVOICES.ID.eq(entity.getId()))
                .execute();
    }
}
