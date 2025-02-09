package service;

import model.Currency;
import model.ExchangeRates;
import util.DbFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class ExchangeRatesService {

    CurrencyService currencyService = new CurrencyService();

    public List<ExchangeRates> getAllExchangeRates() throws SQLException {
        final String GET_ALL_EXCHANGE_RATES = "SELECT * FROM exchange_rates";

        List<ExchangeRates> exchangeRates = new ArrayList<>();

        try (Connection connection = DbFactory.getConnection();
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

    public ExchangeRates getExchangeRateByBaseCodeAndTargetCode(String baseCode, String targetCode) throws SQLException, NoSuchElementException {
        final String GET_BY_BASE_AND_TARGET_CURRENCY = """
               SELECT base.id AS base_id,
               target.id AS target_id,
               er.id AS exchange_rate_id, er.rate
               FROM exchange_rates er
               INNER JOIN currencies base ON er.base_currency_id = base.id
               INNER JOIN currencies target ON er.target_currency_id = target.id
               WHERE base.code = ? AND target.code = ?
                """;

        try (Connection connection = DbFactory.getConnection();
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

    public ExchangeRates addExchangeRateByCurrenciesCods(String baseCode, String targetCode, BigDecimal rate) throws SQLException {
        final String ADD_EXCHANGE_RATE = """
                                            INSERT OR IGNORE INTO exchange_rates (base_currency_id, target_currency_id, rate)
                                            VALUES (
                                            (SELECT id FROM currencies WHERE code = ?),
                                            (SELECT id FROM currencies WHERE code = ?), ?)
                                            """;

        try (Connection connection = DbFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_EXCHANGE_RATE)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            statement.setBigDecimal(3, rate);
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

        try (Connection connection = DbFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_BASE_CODE_AND_TARGET_CODE)) {

            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }

    }

    public ExchangeRates updateExchangeRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException, NoSuchElementException {
        final String UPDATE_EXCHANGE_RATE = """ 
                UPDATE exchange_rates SET rate = ?
                WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
                AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
                """;

        try (Connection connection = DbFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_EXCHANGE_RATE)) {

            statement.setBigDecimal(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new NoSuchElementException();
            }

            return getExchangeRateByBaseCodeAndTargetCode(baseCode, targetCode);
        }
    }


    public ExchangeRates getExchangeRate(String baseCode, String targetCode) throws SQLException, NoSuchElementException {

        if (isExchangeRateExist(baseCode, targetCode)) {
            return getDefaultExchangeRates(baseCode, targetCode);
        }
        if (isReversedExchangeRateExist(baseCode, targetCode)) {
            return getReversedExchangeRates(targetCode, baseCode);
        }

        String usd = "USD";
        if (isExchangeRateExist(usd, baseCode) && isExchangeRateExist(usd, targetCode)) {

            ExchangeRates usdBase = getDefaultExchangeRates(usd, baseCode);
            ExchangeRates usdTarget = getDefaultExchangeRates(usd, targetCode);

            BigDecimal newExchangeRate = usdTarget.getRate().divide(usdBase.getRate(), 6, RoundingMode.HALF_UP);

            return new ExchangeRates(0,
                    usdBase.getTargetCurrencyId(),
                    usdTarget.getTargetCurrencyId(),
                    newExchangeRate);

        }

        else throw new NoSuchElementException();
    }

    private boolean isExchangeRateExist(String baseCode, String targetCode) throws SQLException {
        return existExchangeRateByBaseCurrencyAndTargetCurrency(baseCode, targetCode);
    }

    private boolean isReversedExchangeRateExist(String baseCode, String targetCode) throws SQLException {
        return existExchangeRateByBaseCurrencyAndTargetCurrency(targetCode, baseCode);
    }


    private ExchangeRates getDefaultExchangeRates(String baseCode, String targetCode) throws SQLException {
        return getExchangeRatesByCods(baseCode, targetCode, false);
    }

    private ExchangeRates getReversedExchangeRates(String baseCode, String targetCode) throws SQLException {
        return getExchangeRatesByCods(baseCode, targetCode, true);
    }

    private ExchangeRates getExchangeRatesByCods(String baseCode, String targetCode, boolean isReverse) throws SQLException {
        ExchangeRates exchangeRate = getExchangeRateByBaseCodeAndTargetCode(baseCode, targetCode);
        Currency base = currencyService.getByCode(baseCode);
        Currency target = currencyService.getByCode(targetCode);
        BigDecimal newExchangeRate = (isReverse) ? (BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP)) : (exchangeRate.getRate());
        int baseId = base.getId();
        int targetId = target.getId();
        if (isReverse) {
            int temp = targetId;
            targetId = baseId;
            baseId = temp;
        }
        return new ExchangeRates(exchangeRate.getId(),
                baseId,
                targetId,
                newExchangeRate);
    }

}
