import dao.ProductDAO;
import entity.Product;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDAOTest {
    private static @NotNull ProductDAO productDAO;

    @BeforeAll
    static void init(){
        productDAO = new ProductDAO();
    }
    @Test
    void getById() {
        Product product = new Product(101, "Item 1");
        assertEquals(product, productDAO.getById(product.getCode()));
    }
    @Test
    void save() {
        Product product = new Product(111, "Item 13");
        productDAO.save(product);
        assertEquals(product, productDAO.getById(product.getCode()));
        productDAO.delete(product);
    }
    @Test
    void getAll() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(101, "Item 1"));
        productList.add(new Product(112, "Item 2"));
        productList.add(new Product(141, "Item 3"));
        productList.add(new Product(353, "Item 4"));
        productList.add(new Product(242, "Item 5"));

        assertEquals(productList, productDAO.getAll());
    }
    @Test
    void update() {
        Product product = new Product(111, "Item 13");
        productDAO.save(product);
        product.setName("Item 21");
        productDAO.update(product);
        assertEquals(product, productDAO.getById(product.getCode()));
        productDAO.delete(product);

    }
    @Test
    void delete() {
        Product product = new Product(111, "Item 13");
        productDAO.save(product);
        productDAO.delete(product);
        assertNull(productDAO.getById(product.getCode()));
    }
}
