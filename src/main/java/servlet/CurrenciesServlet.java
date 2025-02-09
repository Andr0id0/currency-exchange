package servlet;

import response.ErrorResponse;
import response.Response;
import repository.CurrencyRepository;
import dto.CurrencyDto;
import model.Currency;
import convertor.CurrencyConvertor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


import static util.ServletValidationUtils.validateParam;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyRepository currencyRepository = new CurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = currencyRepository.getAll();

            List<CurrencyDto> dtos = currencies.stream()
                    .map(CurrencyConvertor::toDto).collect(Collectors.toList());

            Response.sendOk(resp, dtos);

        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal server error: " + e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String code = validateParam(req, resp, "code");
            String fullName = validateParam(req, resp, "name");
            String sign = validateParam(req, resp, "sign");

            if (code.length() != 3) {
                ErrorResponse.sendBadRequest(resp, "Code must be XXX");
                return;
            }
            code = code.toUpperCase();
            Currency newCurrency = new Currency(0, code, fullName, sign);
            newCurrency = currencyRepository.add(newCurrency);
            CurrencyDto dto = CurrencyConvertor.toDto(newCurrency);

            Response.sendCreated(resp, dto);

        } catch (SQLDataException e) {
            ErrorResponse.sendConflict(resp, e.getMessage());
        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal server error: " + e.getMessage());
        }  catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }

    }

}
