package repository;

import model.ExchangeRates;
import db.DbConnectionFactory;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class ExchangeRatesRepository implements Repository<ExchangeRates>{

    CurrencyRepository currencyRepository = new CurrencyRepository();

    @Override
    public List<ExchangeRates> getAll() throws SQLException {
        final String GET_ALL_EXCHANGE_RATES = "SELECT * FROM exchange_rates";

        List<ExchangeRates> exchangeRates = new ArrayList<>();

        try (Connection connection = DbConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_EXCHANGE_RATES)) {

            while (resultSet.next()) {
                exchangeRates.add(new ExchangeRates(
                        resultSet.getInt("id"),
                        resultSet.getInt("base_currency_id"),
                        resultSet.getInt("target_currency_id"),
                        resultSet.getBigDecimal("rate")
                ));
            }

        }
        return exchangeRates;
    }

    public ExchangeRates getByCods(String baseCode, String targetCode) throws SQLException, NoSuchElementException {
        final String GET_BY_BASE_AND_TARGET_CURRENCY = """
               SELECT base.id AS base_id,
               target.id AS target_id,
               er.id AS exchange_rate_id, er.rate
               FROM exchange_rates er
               INNER JOIN currencies base ON er.base_currency_id = base.id
               INNER JOIN currencies target ON er.target_currency_id = target.id
               WHERE base.code = ? AND target.code = ?
                """;

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_BASE_AND_TARGET_CURRENCY);) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    int baseCurrencyId = resultSet.getInt("base_id");
                    int targetCurrencyId = resultSet.getInt("target_id");
                    int id = resultSet.getInt("exchange_rate_id");
                    BigDecimal rate = resultSet.getBigDecimal("rate");

                    return new ExchangeRates(id, baseCurrencyId, targetCurrencyId, rate);
                } else {
                    throw new NoSuchElementException();
                }
            }

        }
    }

    public ExchangeRates add(String baseCode, String targetCode, BigDecimal rate) throws SQLException, SQLDataException {
        final String ADD_EXCHANGE_RATE = """
                                            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
                                            VALUES (
                                            (SELECT id FROM currencies WHERE code = ?),
                                            (SELECT id FROM currencies WHERE code = ?), ?)
                                            """;

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_EXCHANGE_RATE)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            statement.setBigDecimal(3, rate);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int baseId = currencyRepository.getByCode(baseCode).getId();
                    int targetId = currencyRepository.getByCode(targetCode).getId();
                    return new ExchangeRates(id, baseId, targetId, rate);
                } else {
                    throw new SQLException();
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new SQLDataException("Exchange rate for this currency pair already exists.", e);
            } else {
                throw e;
            }
        }
    }

    public boolean exist(String baseCode, String targetCode) throws SQLException {
        final String GET_BY_BASE_CODE_AND_TARGET_CODE = """
                SELECT * FROM exchange_rates WHERE base_currency_id = 
                (SELECT id FROM currencies WHERE code = ?) AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
                """;

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_BASE_CODE_AND_TARGET_CODE)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }

    }

    public ExchangeRates update(String baseCode, String targetCode, BigDecimal rate) throws SQLException, NoSuchElementException {
        final String UPDATE_EXCHANGE_RATE = """ 
                UPDATE exchange_rates SET rate = ?
                WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
                AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
                """;

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {

            statement.setBigDecimal(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new NoSuchElementException();
            }

            return getByCods(baseCode, targetCode);
        }
    }




}
