package db;

import org.sqlite.SQLiteDataSource;
import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public class DbConnectionFactory {

    private static DbConnectionFactory instance = null;
    private DbConnectionFactory() {
    }

    public static DbConnectionFactory getInstance() {
        if(instance==null){
            instance = new DbConnectionFactory();
        }
        return instance;
    }

    private DataSource getDataSource() {
        URL resource = DbConnectionFactory.class.getClassLoader().getResource("currency-exchange.db");
        String path = null;
        if (resource != null) {
            try {
                path = new File(resource.toURI()).getAbsolutePath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl(String.format("jdbc:sqlite:%s", path));
        return sqLiteDataSource;
    }


    public static Connection getConnection() throws SQLException {
        DbConnectionFactory dbConnectionFactory = DbConnectionFactory.getInstance();
        DataSource dataSource = dbConnectionFactory.getDataSource();
        return dataSource.getConnection();
    }
}
