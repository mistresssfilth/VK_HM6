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

import static generated.Tables.POSITIONS;

public final class PositionDAO implements DAO<Position> {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;
    private static Connection connection;
    private static DSLContext context;

    public PositionDAO() {
        try {
            connection = DriverManager.getConnection(CREDS.getUrl(), CREDS.getLogin(), CREDS.getPassword());
            this.context = DSL.using(connection, SQLDialect.POSTGRES);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Position> getAll() {
        final List<Position> positions = new ArrayList<>();
        final Result<PositionsRecord> records = context.fetch(POSITIONS);
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
    }

    @Override
    public Position getById(@NotNull int id) {
        final PositionsRecord record = context.fetchOne(POSITIONS, POSITIONS.ID.eq(id));
        if (record != null) {
            return new Position(
                    record.getId(),
                    record.getPrice(),
                    record.getProductId(),
                    record.getInvoiceId(),
                    record.getCount()
            );
        }
        return null;
    }

    @Override
    public void save(@NotNull Position entity) {
        context
                .insertInto(
                        POSITIONS,
                        POSITIONS.ID,
                        POSITIONS.PRICE,
                        POSITIONS.PRODUCT_ID,
                        POSITIONS.INVOICE_ID,
                        POSITIONS.COUNT
                )
                .values(entity.getId(), entity.getPrice(), entity.getProductId(), entity.getInvoiceId(), entity.getCount())
                .execute();
    }

    @Override
    public void update(@NotNull Position entity) {
        context
                .update(POSITIONS)
                .set(POSITIONS.PRICE, entity.getPrice())
                .set(POSITIONS.PRODUCT_ID, entity.getProductId())
                .set(POSITIONS.INVOICE_ID, entity.getInvoiceId())
                .set(POSITIONS.COUNT, entity.getCount())
                .where(POSITIONS.ID.eq(entity.getId()))
                .execute();
    }

    @Override
    public void delete(@NotNull Position entity) {
        context
                .delete(POSITIONS)
                .where(POSITIONS.ID.eq(entity.getId()))
                .execute();
    }
}
