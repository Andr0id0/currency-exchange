package dao;

import model.Currency;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CurrencyService {


    public List<Currency> getAllCurrencies() throws SQLException {
        final String SELECT_ALL_SQL = "SELECT * FROM currencies";
        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = DBUtil.getConnection();
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

    public int addCurrency(String code, String fullName, String sign) throws SQLException {
        final String ADD_CURRENCY = "INSERT OR IGNORE INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_CURRENCY)) {

            statement.setString(1, code);
            statement.setString(2, fullName);
            statement.setString(3, sign);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new SQLException();
                }

            }

        }
    }

    public Optional<Currency> getById(int id) throws SQLException {
        final String GET_BY_ID = "SELECT * FROM currencies WHERE id = ?";

        try (Connection connection = DBUtil.getConnection();
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

        try (Connection connection = DBUtil.getConnection();
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

    public boolean existCode(String code) throws SQLException {
        final String GET_BY_CODE = "SELECT * FROM currencies WHERE code = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_CODE)) {

            statement.setString(1,code);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }

    }

    public boolean existFullName(String fullName) throws SQLException {
        final String GET_BY_FULL_NAME = "SELECT * FROM currencies WHERE full_name = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_FULL_NAME)) {

            statement.setString(1, fullName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }

    }

    public boolean existSign(String sign) throws SQLException {
        final String GET_BY_FULL_SIGN = "SELECT * FROM currencies WHERE sign = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_FULL_SIGN)) {

            statement.setString(1, sign);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }

    }


}
