package dao;

import commons.JDBCCredentials;
import entity.Invoice;
import entity.Organization;
import generated.Tables;
import generated.tables.records.InvoicesRecord;
import generated.tables.records.OrganizationsRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.QOM;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NotNullNullableValidation", "SqlNoDataSourceInspection", "SqlResolve"})
public final class InvoiceDAO implements DAO<Invoice> {

    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static  Connection connection;

    public InvoiceDAO() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Invoice> getAll() {
        List<Invoice> invoices = new ArrayList<>();
        try {
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final Result<InvoicesRecord> records = context.fetch(Tables.INVOICES);
            for (var record : records) {
                invoices.add(new Invoice(
                        record.getId(),
                        Date.valueOf((record.getDate().toString())),
                        record.getOrgId()
                ));
            }
            return invoices;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public Invoice getById(@NotNull int id) {
        try {
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final InvoicesRecord record = context.fetchOne(Tables.INVOICES, Tables.INVOICES.ID.eq(id));
            if (record != null){
                return new Invoice(
                        record.getId(),
                        Date.valueOf((record.getDate().toString())),
                        record.getOrgId()
                );

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NotNull Invoice entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .insertInto(Tables.INVOICES, Tables.INVOICES.ID, Tables.INVOICES.DATE, Tables.INVOICES.ORG_ID)
                    .values(entity.getId(), entity.getDate().toLocalDate(), entity.getOrgId())
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update(@NotNull Invoice entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .update(Tables.INVOICES)
                    .set(Tables.INVOICES.DATE, entity.getDate().toLocalDate())
                    .set(Tables.INVOICES.ORG_ID, entity.getOrgId())
                    .where(Tables.INVOICES.ID.eq(entity.getId()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Invoice entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .delete(Tables.INVOICES)
                    .where(Tables.INVOICES.ID.eq(entity.getId()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
