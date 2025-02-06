package servlet;

import response.ErrorResponse;
import response.Response;
import service.CurrencyService;
import dto.CurrencyDto;
import model.Currency;
import convertor.CurrencyConvertor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static util.ServletUtils.isNotValidParams;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();

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
            String code = req.getParameter("code");
            String fullName = req.getParameter("full_name");
            String sign = req.getParameter("sign");

            if (isNotValidParams(code, fullName, sign, resp)) {
                return;
            }

            code = code.toUpperCase();

            if (isParamsNotExist(code, fullName, sign, resp)) {
                return;
            }

            Currency newCurrency = new Currency(0, code, fullName, sign);
            int id = currencyService.addCurrency(code, fullName, sign);
            newCurrency.setId(id);
            CurrencyDto dto = CurrencyConvertor.toDto(newCurrency);

            Response.sendCreated(resp, dto);

        }  catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal server error: " + e.getMessage());
        }

    }

    private boolean isParamsNotExist(String code, String fullName, String sign, HttpServletResponse resp) throws SQLException, IOException {
        if (currencyService.existCode(code) || currencyService.existFullName(fullName) || currencyService.existSign(sign)) {
            ErrorResponse.sendConflict(resp, "Currency already exist");
            return true;
        }
        return false;
    }

}
