import dao.InvoiceDAO;
import entity.Invoice;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceDAOTest {
    private static @NotNull InvoiceDAO invoiceDAO;

    @BeforeAll
    static void init(){
        invoiceDAO = new InvoiceDAO();
    }
    @Test
    void getById() {
        Invoice invoice =  new Invoice(1, Date.valueOf("2022-05-03"), 1287);
        assertEquals(invoice, invoiceDAO.getById(invoice.getId()));
    }
    @Test
    void save() {
        Invoice invoice =  new Invoice(13, Date.valueOf("2022-05-03"), 1287);
        invoiceDAO.save(invoice);
        assertEquals(invoice, invoiceDAO.getById(invoice.getId()));
        invoiceDAO.delete(invoice);
    }
    @Test
    void getAll() {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(new Invoice(1, Date.valueOf("2022-05-03"), 1287));
        invoiceList.add(new Invoice(2, Date.valueOf("2021-08-10"), 1287));
        invoiceList.add(new Invoice(3, Date.valueOf("2022-11-13"), 1481));
        invoiceList.add(new Invoice(4, Date.valueOf("2019-03-06"), 1589));
        invoiceList.add(new Invoice(5, Date.valueOf("2022-11-08"), 1940));

        assertEquals(invoiceList, invoiceDAO.getAll());
    }
    @Test
    void update() {
        Invoice invoice =  new Invoice(13, Date.valueOf("2022-05-03"), 1287);
        invoiceDAO.save(invoice);
        invoice.setDate(Date.valueOf("2021-05-03"));
        invoiceDAO.update(invoice);
        assertEquals(invoice, invoiceDAO.getById(invoice.getId()));
        invoiceDAO.delete(invoice);
    }
    @Test
    void delete() {
        Invoice invoice =  new Invoice(13, Date.valueOf("2022-05-03"), 1287);
        invoiceDAO.save(invoice);
        invoiceDAO.delete(invoice);
        assertNull(invoiceDAO.getById(invoice.getId()));
    }
}
