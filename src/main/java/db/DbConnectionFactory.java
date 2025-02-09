package util;

import org.sqlite.SQLiteDataSource;
import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public class DbFactory {

    private static DbFactory instance = null;
    private DbFactory() {
    }

    public static DbFactory getInstance() {
        if(instance==null){
            instance = new DbFactory();
        }
        return instance;
    }

    private DataSource getDataSource() {
        URL resource = DbFactory.class.getClassLoader().getResource("currency-exchange.db");
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
        DbFactory dbFactory = DbFactory.getInstance();
        DataSource dataSource = dbFactory.getDataSource();
        return dataSource.getConnection();
    }
}
