package dao;

import model.ExchangeRates;
import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class ExchangeRatesService {

    CurrencyService currencyService = new CurrencyService();

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

    public ExchangeRates getExchangeRateByBaseCodeAndTargetCode(String baseCode, String targetCode) throws SQLException, NoSuchElementException {
        final String GET_BY_BASE_AND_TARGET_CURRENCY = """
               SELECT base.id AS base_id, base.code AS base_code, base.full_name AS base_full_name, base.sign AS base_sign,
               target.id AS target_id, target.code AS target_code, target.full_name AS target_full_name, target.sign AS target_sign,
               er.id AS exchange_rate_id, er.rate
               FROM exchange_rates er
               INNER JOIN currencies base ON er.base_currency_id = base.id
               INNER JOIN currencies target ON er.target_currency_id = target.id
               WHERE base.code = ? AND target.code = ?
                """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_BASE_AND_TARGET_CURRENCY);) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    int baseCurrencyId = resultSet.getInt("base_id");
                    int targetCurrencyId = resultSet.getInt("target_id");
                    int id = resultSet.getInt("exchange_rate_id");
                    double rate = resultSet.getDouble("rate");

                    return new ExchangeRates(id, baseCurrencyId, targetCurrencyId, rate);
                } else {
                    throw new NoSuchElementException();
                }
            }

        }
    }

    public ExchangeRates addExchangeRateByCurrenciesCods(String baseCode, String targetCode, double rate) throws SQLException {
        final String ADD_EXCHANGE_RATE = """
                                            INSERT OR IGNORE INTO exchange_rates (base_currency_id, target_currency_id, rate)
                                            VALUES (
                                            (SELECT id FROM currencies WHERE code = ?),
                                            (SELECT id FROM currencies WHERE code = ?), ?)
                                            """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_EXCHANGE_RATE)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            statement.setDouble(3, rate);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int baseId = currencyService.getByCode(baseCode).getId();
                    int targetId = currencyService.getByCode(targetCode).getId();
                    return new ExchangeRates(id, baseId, targetId, rate);
                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public boolean existExchangeRateByBaseCurrencyAndTargetCurrency(String baseCode, String targetCode) throws SQLException {
        final String GET_BY_BASE_CODE_AND_TARGET_CODE = """
                SELECT * FROM exchange_rates WHERE base_currency_id = 
                (SELECT id FROM currencies WHERE code = ?) AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
                """;

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_BASE_CODE_AND_TARGET_CODE)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }

    }

    public ExchangeRates updateExchangeRate(String baseCode, String targetCode, double rate) throws SQLException, NoSuchElementException {
        final String UPDATE_EXCHANGE_RATE = """ 
                UPDATE exchange_rates SET rate = ?
                WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
                AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
                """;

        try (Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {

            statement.setDouble(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new NoSuchElementException();
            }

            return getExchangeRateByBaseCodeAndTargetCode(baseCode, targetCode);
        }
    }

}
