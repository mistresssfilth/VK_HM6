package dao;

import commons.JDBCCredentials;
import entity.Position;
import generated.Tables;
import generated.tables.records.PositionsRecord;
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

public final class PositionDAO implements DAO<Position> {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static  Connection connection;

    public PositionDAO() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Position> getAll() {
        final List<Position> positions = new ArrayList<>();
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final Result<PositionsRecord> records = context.fetch(Tables.POSITIONS);
            for (var record : records) {
                positions.add(new Position(
                        record.getId(),
                        record.getPrice(),
                        record.getProductId(),
                        record.getInvoiceId(),
                        record.getCount()
                ));
            }
            return positions;
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public Position getById(@NotNull int id) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            final PositionsRecord record = context.fetchOne(Tables.POSITIONS, Tables.POSITIONS.ID.eq(id));
            if (record != null){
                return new Position(
                        record.getId(),
                        record.getPrice(),
                        record.getProductId(),
                        record.getInvoiceId(),
                        record.getCount()
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(@NotNull Position entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .insertInto(
                            Tables.POSITIONS,
                            Tables.POSITIONS.ID,
                            Tables.POSITIONS.PRICE,
                            Tables.POSITIONS.PRODUCT_ID,
                            Tables.POSITIONS.INVOICE_ID,
                            Tables.POSITIONS.COUNT
                    )
                    .values(entity.getId(), entity.getPrice(), entity.getProductId(), entity.getInvoiceId(), entity.getCount())
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update(@NotNull Position entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .update(Tables.POSITIONS)
                    .set(Tables.POSITIONS.PRICE, entity.getPrice())
                    .set(Tables.POSITIONS.PRODUCT_ID, entity.getProductId())
                    .set(Tables.POSITIONS.INVOICE_ID, entity.getInvoiceId())
                    .set(Tables.POSITIONS.COUNT, entity.getCount())
                    .where(Tables.POSITIONS.ID.eq(entity.getId()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@NotNull Position entity) {
        try{
            final DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            context
                    .delete(Tables.POSITIONS)
                    .where(Tables.POSITIONS.ID.eq(entity.getId()))
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
