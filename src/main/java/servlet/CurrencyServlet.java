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
import java.util.NoSuchElementException;
import static util.ServletUtils.validatePathCode;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = validatePathCode(resp, req.getPathInfo());

            String code = pathInfo.toUpperCase();

            Currency currency = currencyService.getByCode(code);

            CurrencyDto dto = CurrencyConvertor.toDto(currency);
            Response.sendOk(resp, dto);

        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Currency not found");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }


    }
}
