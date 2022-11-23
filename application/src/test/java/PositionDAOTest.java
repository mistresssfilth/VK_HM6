import dao.PositionDAO;
import entity.Position;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PositionDAOTest {
    private static @NotNull PositionDAO positionDAO;

    @BeforeAll
    static void init(){
        positionDAO = new PositionDAO();
    }
    @Test
    void getById() {
        Position position = new Position(1, 1100, 101, 1, 1);
        assertEquals(position, positionDAO.getById(position.getId()));
    }
    @Test
    void save() {
        Position position = new Position(6, 1100, 101, 1, 1);
        positionDAO.save(position);
        assertEquals(position, positionDAO.getById(position.getId()));
        positionDAO.delete(position);
    }
    @Test
    void getAll() {
        List<Position> positionList = new ArrayList<>();
        positionList.add(new Position(1, 1100, 101, 1, 1));
        positionList.add(new Position(2, 1300, 353, 1, 2));
        positionList.add(new Position(3, 12200, 141, 3, 1));
        positionList.add(new Position(4, 3369, 112, 4, 4));
        positionList.add(new Position(5, 390, 242, 2, 10));

        assertEquals(positionList, positionDAO.getAll());
    }
    @Test
    void update() {
        Position position = new Position(6, 1100, 101, 1, 1);
        positionDAO.save(position);
        position.setPrice(1500);
        position.setCount(2);
        positionDAO.update(position);
        assertEquals(position, positionDAO.getById(position.getId()));
        positionDAO.delete(position);

    }
    @Test
    void delete() {
        Position position = new Position(6, 1100, 101, 1, 1);
        positionDAO.save(position);
        positionDAO.delete(position);
        assertNull(positionDAO.getById(position.getId()));
    }

}
