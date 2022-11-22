import commons.FlywayInit;
import commons.JDBCCredentials;
import org.jetbrains.annotations.NotNull;

public class Main {
    private static final @NotNull JDBCCredentials CREDS = JDBCCredentials.DEFAULT;

    public static void main(String[] args) {
        FlywayInit.initDb();
    }
}
