import dao.OrganizationDAO;
import entity.Organization;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

;import static org.junit.jupiter.api.Assertions.*;

public class OrganizationDAOTest {
    private static @NotNull OrganizationDAO organizationDAO;

    @BeforeAll
    static void init(){
        organizationDAO = new OrganizationDAO();
    }
    @Test
    void getById() {
        Organization organization =  new Organization(1287, "Provider 1", 258193);
        assertEquals(organization, organizationDAO.getById(organization.getInn()));
    }
    @Test
    void save() {
        Organization organization =  new Organization(1901, "Provider 120", 205920);
        organizationDAO.save(organization);
        assertEquals(organization, organizationDAO.getById(organization.getInn()));
        organizationDAO.delete(organization);
    }
    @Test
    void getAll() {
        List<Organization> organizationList = new ArrayList<>();
        organizationList.add(new Organization(1287,"Provider 1",258193));
        organizationList.add(new Organization(1589,"Provider 2",158538));
        organizationList.add(new Organization(1940,"Provider 3",563139));
        organizationList.add(new Organization(1481,"Provider 4",614783));
        organizationList.add(new Organization(1417,"Provider 5",157850));

        assertEquals(organizationList, organizationDAO.getAll());
    }
    @Test
    void update() {
        Organization organization =  new Organization(1149,"Provider 141",285258);
        organizationDAO.save(organization);
        organization.setName("Provider 29");
        organizationDAO.update(organization);
        assertEquals(organization, organizationDAO.getById(organization.getInn()));
        organizationDAO.delete(organization);
    }
    @Test
    void delete() {
        Organization organization =  new Organization(5352,"Provider 13", 140105);
        organizationDAO.save(organization);
        organizationDAO.delete(organization);
        assertNotEquals(organization, organizationDAO.getById(organization.getInn()));
        assertNull(organizationDAO.getById(organization.getInn()));
    }
}
