package thembelani.java.tutorials;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting...");

        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(Path.of("music.properties"),
                    StandardOpenOption.READ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String albumName = "Tapestry";
        String query = "SELECT * FROM music.albumview WHERE album_name= '%s'"
                .formatted(albumName);


        var datasource = new MysqlDataSource();
        datasource.setServerName(props.getProperty("serverName"));
        datasource.setPort(Integer.parseInt(props.getProperty("port")));
        datasource.setDatabaseName(props.getProperty("databaseName"));

        try (var connection = datasource.getConnection(
                props.getProperty("user"),
                System.getenv("MYSQL_PASS"));

             Statement statement = connection.createStatement();

        ) {
            ResultSet resultSet = statement.executeQuery(query);

            var meta = resultSet.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.printf("%d %s %s%n",
                        i,
                        meta.getColumnName(i),
                        meta.getColumnTypeName(i)
                );
            }

            System.out.println("=".repeat(50));

            //Setting up column names
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.printf("%-15s", meta.getColumnName(i).toUpperCase());
            }
            System.out.println();

            while (resultSet.next()) {

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    System.out.printf("%-15s", resultSet.getString(i));
                }
                System.out.println();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}