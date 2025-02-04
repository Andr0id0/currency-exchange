package dao;

import model.ExchangeRates;
import util.DBUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ExchangeRatesDao {

    public List<ExchangeRates> getAllExchangeRates() throws SQLException {
        final String GET_ALL_EXCHANGE_RATES = "SELECT * FROM exchange_rates";

        List<ExchangeRates> exchangeRates = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_EXCHANGE_RATES)) {

            while (resultSet.next()) {
                exchangeRates.add(new ExchangeRates(
                        resultSet.getInt("id"),
                        resultSet.getInt("base_currency_id"),
                        resultSet.getInt("target_currency_id"),
                        resultSet.getDouble("rate")
                ));
            }

        }
        return exchangeRates;
    }

}
