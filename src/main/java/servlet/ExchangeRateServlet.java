package servlet;

import convertor.ExchangeRatesConvertor;
import response.ErrorResponse;
import response.Response;
import service.ExchangeRatesService;
import dto.ExchangeRatesDto;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static util.ServletUtils.*;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = validateTwoPathCods(resp, req.getPathInfo());

            String[] codes = extractCurrencyCodes(pathInfo);
            String baseCode = codes[0];
            String targetCode = codes[1];

            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.getExchangeRate(baseCode, targetCode));
            Response.sendOk(resp, dto);

        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Exchange rate not found");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
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
        try {
            String rateString = getRequestBodyParam(req, "rate");
            String pathInfo = validateTwoPathCods(resp, req.getPathInfo());

            String[] codes = extractCurrencyCodes(pathInfo);
            String baseCode = codes[0];
            String targetCode = codes[1];

            BigDecimal rate = new BigDecimal(rateString).setScale(6, RoundingMode.HALF_UP);

            isCodsNotExist(baseCode, targetCode, resp);

            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.updateExchangeRate(baseCode, targetCode, rate));
            Response.sendOk(resp, dto);

        } catch (SQLException | NoSuchElementException | NumberFormatException e) {
            handleException(resp, e, "Exchange rate", "Rate");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }
    }




}
