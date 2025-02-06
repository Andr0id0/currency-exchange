package servlet;

import convertor.ExchangeRatesConvertor;
import response.ErrorResponse;
import response.Response;
import service.CurrencyService;
import service.ExchangeRatesService;
import dto.ExchangeRatesDto;
import model.ExchangeRates;
import util.RoundDouble;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import static util.ServletUtils.isNotValidParams;
import static util.ServletUtils.isCodsNotExist;
import static util.ServletUtils.isCodsUsed;


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
        String baseCode = req.getParameter("base_code");
        String targetCode = req.getParameter("target_code");
        String rateString = req.getParameter("rate");
        if (isNotValidParams(baseCode, targetCode, rateString, resp)) {
            return;
        }

        baseCode = baseCode.toUpperCase();
        targetCode = targetCode.toUpperCase();

        try {
            double rate = RoundDouble.roundTo6DecimalPlace(Double.parseDouble(rateString));

            if (isCodsNotExist(baseCode, targetCode, resp))
                return;
            if (isCodsUsed(baseCode, targetCode, resp))
                return;

            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.addExchangeRateByCurrenciesCods(baseCode, targetCode, rate));
            Response.sendCreated(resp, dto);
        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Exchange rate not found");
        } catch (NumberFormatException e) {
            ErrorResponse.sendBadRequest(resp, "Rate is not number");
        }
    }





}
