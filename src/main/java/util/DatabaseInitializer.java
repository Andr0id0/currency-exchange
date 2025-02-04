package util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.List;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

//    public static void main(String[] args) {
//        createTables();
//        insertSampleData();
//    }


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Инициализация базы данных...");
        createTables();
        insertSampleData();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Очистка ресурсов...");
    }


//    public static void dropTables() {
//        try (Connection conn = DBUtil.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Удаление таблиц
//            stmt.executeUpdate("DROP TABLE IF EXISTS exchange_rates");
//            stmt.executeUpdate("DROP TABLE IF EXISTS currencies");
//            System.out.println("Таблицы успешно удалены.");
//
//        } catch (SQLException e) {
//            System.err.println("Ошибка при удалении таблиц: " + e.getMessage());
//        }
//    }

    private static void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS currencies (
                id INTEGER PRIMARY KEY,
                code TEXT NOT NULL UNIQUE,
                full_name TEXT NOT NULL UNIQUE,
                sign varchar NOT NULL UNIQUE
            );
            """;

        String createOrdersTable = """
            CREATE TABLE IF NOT EXISTS exchange_rates (
                id INTEGER PRIMARY KEY,
                base_currency_id INTEGER NOT NULL,
                target_currency_id INTEGER NOT NULL,
                rate DECIMAL(6) NOT NULL,
                FOREIGN KEY(base_currency_id) REFERENCES currencies(id),
                FOREIGN KEY(target_currency_id) REFERENCES currencies(id),
                UNIQUE(base_currency_id, target_currency_id)
            );
            """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createOrdersTable);

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц: " + e.getMessage());
        }
    }

    private static void insertSampleData() {
        insertCurrencies();
        insertExchangeRates();
    }

    private static void insertCurrencies() {
        String sql = "INSERT OR IGNORE INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        List<String[]> currencies = List.of(
                new String[]{"USD", "United States dollar", "$"},
                new String[]{"EUR", "Euro", "€"},
                new String[]{"RUB", "Russian Ruble", "₽"},
                new String[]{"BYN", "Belarussian Ruble", "Br"}
        );

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String[] currency : currencies) {
                stmt.setString(1, currency[0]);
                stmt.setString(2, currency[1]);
                stmt.setString(3, currency[2]);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при вставке валют: " + e.getMessage());
        }
    }

    private static void insertExchangeRates() {
        String sql = """
        INSERT OR IGNORE INTO exchange_rates (base_currency_id, target_currency_id, rate)
        VALUES (
            (SELECT id FROM currencies WHERE code = ?),
            (SELECT id FROM currencies WHERE code = ?),
            ?
        )""";

        List<Object[]> rates = List.of(
                new Object[]{"USD", "EUR", 0.97355},
                new Object[]{"USD", "RUB", 97.81},
                new Object[]{"USD", "BYN", 3.40}
        );

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Object[] rate : rates) {
                stmt.setString(1, (String) rate[0]);
                stmt.setString(2, (String) rate[1]);
                stmt.setDouble(3, (double) rate[2]);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при вставке курсов: " + e.getMessage());
        }
    }
}