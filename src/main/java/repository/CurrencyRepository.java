package repository;

import model.Currency;
import db.DbConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CurrencyRepository implements Repository<Currency> {

    @Override
    public List<Currency> getAll() throws SQLException {
        final String SELECT_ALL_SQL = "SELECT * FROM currencies";
        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = DbConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_SQL)) {

            while (resultSet.next()) {
                currencies.add(new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign")
                        ));
            }

        }
        return currencies;
    }

    public Currency add(Currency currency) throws SQLException, SQLDataException {
        final String ADD_CURRENCY = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_CURRENCY)) {

            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    currency.setId(resultSet.getInt(1));
                    return currency;
                } else {
                    throw new SQLException();
                }

            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new SQLDataException("Currency with this params already exists.", e);
            } else {
                throw e;
            }
        }

    }

    public Optional<Currency> getById(int id) throws SQLException {
        final String GET_BY_ID = "SELECT * FROM currencies WHERE id = ?";

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_ID)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign")
                ));
            }
        }
        return Optional.empty();
    }

    public Currency getByCode(String code) throws SQLException, NoSuchElementException {
        final String GET_BY_CODE = "SELECT * FROM currencies WHERE code = ?";

        try (Connection connection = DbConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_CODE)) {

            statement.setString(1,code);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return (new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign")
                ));
            } else {
                throw new NoSuchElementException();
            }
        }
    }

}
