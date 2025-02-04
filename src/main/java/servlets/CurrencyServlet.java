package servlets;

import dao.CurrencyDao;
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
import java.util.Optional;


@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendErrorResponse(resp, "Currency pathInfo is missing", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        String code = pathInfo.substring(1).toUpperCase();

        try {
            Optional<Currency> currency = currencyDao.getByCode(code);

            if (currency.isEmpty()) {
                JsonUtil.sendErrorResponse(resp, "Currency not found", HttpStatusCode.NOT_FOUND.getValue());
                return;
            }

            CurrencyDto dto = CurrencyConvertor.toDto(currency.get());

            JsonUtil.sendJsonResponse(resp, dto, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Database error", HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        }

    }
}
