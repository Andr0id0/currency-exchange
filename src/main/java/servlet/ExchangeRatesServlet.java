package servlet;

import convertor.ExchangeRatesConvertor;
import response.ErrorResponse;
import response.Response;
import service.CurrencyService;
import service.ExchangeRatesService;
import dto.ExchangeRatesDto;
import model.ExchangeRates;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static util.ServletUtils.*;


@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            List<ExchangeRates> exchangeRates = exchangeRatesService.getAllExchangeRates();

            List<ExchangeRatesDto> dtos = new ArrayList<>();

            for (ExchangeRates rates : exchangeRates) {
                ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(rates);
                dtos.add(dto);
            }

            Response.sendOk(resp, dtos);

        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCode = validateParam(req, resp,"baseCurrencyCode");
        String targetCode = validateParam(req, resp,"targetCurrencyCode");
        String rateString = validateParam(req, resp,"rate");

        baseCode = baseCode.toUpperCase();
        targetCode = targetCode.toUpperCase();

        try {
            BigDecimal rate = new BigDecimal(rateString).setScale(6, RoundingMode.HALF_UP);
            isCodsUsed(baseCode, targetCode, resp);

            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.addExchangeRateByCurrenciesCods(baseCode, targetCode, rate));
            Response.sendCreated(resp, dto);
        }  catch (SQLException | NoSuchElementException | NumberFormatException e) {
            handleException(resp, e, "Exchange rate", "Rate");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }
    }





}
