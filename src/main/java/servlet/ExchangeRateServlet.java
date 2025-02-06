package servlet;

import convertor.ExchangeRatesConvertor;
import response.ErrorResponse;
import response.Response;
import service.CurrencyService;
import service.ExchangeRatesService;
import dto.ExchangeRatesDto;
import util.RoundDouble;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static util.ServletUtils.*;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (isNotValidPathInfo(resp, pathInfo)) {
            return;
        }
        if (isNotValidTwoPathParams(resp, pathInfo)) {
            return;
        }

        String[] codes = extractCurrencyCodes(pathInfo.substring(1));
        String baseCode = codes[0];
        String targetCode = codes[1];

        try {

            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.getExchangeRate(baseCode, targetCode));
            Response.sendOk(resp, dto);

        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Exchange rate not found");
        }

    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")){
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (isNotValidPathInfo(resp, pathInfo)) {
            return;
        }
        if (isNotValidTwoPathParams(resp, pathInfo)) {
            return;
        }

        String[] codes = extractCurrencyCodes(pathInfo.substring(1));
        String baseCode = codes[0];
        String targetCode = codes[1];

        String rateString = req.getParameter("rate");
        if (isNotValidParam(rateString, resp)) {
            return;
        }

        try {
            double rate = RoundDouble.roundTo6DecimalPlace(Double.parseDouble(rateString));

            if (isCodsNotExist(baseCode, targetCode, resp)) {
                return;
            }

            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.updateExchangeRate(baseCode, targetCode, rate));
            Response.sendOk(resp, dto);

        } catch (SQLException | NoSuchElementException | NumberFormatException e) {
            handleException(resp, e, "Exchange rate", "Rate");
        }
    }

}
