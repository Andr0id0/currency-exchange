package servlets;

import dao.CurrencyService;
import dto.CurrencyDto;
import model.Currency;
import convertors.CurrencyConvertor;
import util.HttpStatusCode;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendErrorResponse(resp, "Currency pathInfo is missing", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        String code = pathInfo.substring(1).toUpperCase();

        try {
            Currency currency = currencyService.getByCode(code);

            CurrencyDto dto = CurrencyConvertor.toDto(currency);

            JsonUtil.sendJsonResponse(resp, dto, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        } catch (NoSuchElementException e) {
            JsonUtil.sendErrorResponse(resp, "Currency not found", HttpStatusCode.NOT_FOUND.getValue());
        }


    }
}
