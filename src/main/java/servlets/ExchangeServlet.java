package servlets;

import convertors.CurrencyConvertor;
import dao.CurrencyService;
import dao.ExchangeRatesService;
import model.Currency;
import model.ExchangeDto;
import model.ExchangeRates;
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


@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    CurrencyService currencyService = new CurrencyService();
    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String baseCode = req.getParameter("from");
        String targetCode = req.getParameter("to");
        String amount = req.getParameter("amount");


        if (baseCode == null || targetCode == null || amount == null ||
            baseCode.isEmpty() || targetCode.isEmpty() || amount.isEmpty()) {

            JsonUtil.sendErrorResponse(resp, "Required form field is missing", HttpStatusCode.NOT_FOUND.getValue());
            return;
        }

        try {

            ExchangeRates exchangeRate = exchangeRatesService.getExchangeRateByBaseCodeAndTargetCode(baseCode, targetCode);
            Currency base = currencyService.getByCode(baseCode);
            Currency target = currencyService.getByCode(targetCode);

            double convertedAmount = exchangeRate.getRate() * Double.parseDouble(amount);

            ExchangeDto exchangeDto = new ExchangeDto(CurrencyConvertor.toDto(base), CurrencyConvertor.toDto(target),
                                                        exchangeRate.getRate(), Double.parseDouble(amount), convertedAmount);
            JsonUtil.sendJsonResponse(resp, exchangeDto, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal Server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        } catch (NoSuchElementException e) {
            JsonUtil.sendErrorResponse(resp, "!!! not found !!!", HttpStatusCode.NOT_FOUND.getValue());
        }
    }
}
