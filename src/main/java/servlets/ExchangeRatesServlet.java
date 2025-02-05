package servlets;

import convertors.ExchangeRatesConvertor;
import dao.CurrencyService;
import dao.ExchangeRatesService;
import dto.ExchangeRatesDto;
import model.Currency;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


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

          JsonUtil.sendJsonResponse(resp, dtos, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal Server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String baseCode = req.getParameter("base_code");
        String targetCode = req.getParameter("target_code");
        String rateString = req.getParameter("rate");

        if (baseCode == null || targetCode == null || rateString == null ||
            baseCode.isEmpty() || targetCode.isEmpty() || rateString.isEmpty()) {
            JsonUtil.sendErrorResponse(resp, "Required form field is missing", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }


        double rate = Double.parseDouble(rateString);

        try {
            if (!currencyService.existCode(baseCode)) {
                if (!currencyService.existCode(targetCode)) {
                    JsonUtil.sendErrorResponse(resp, "Both currency from a currency pair does not exist", HttpStatusCode.NOT_FOUND.getValue());
                    return;
                }
                JsonUtil.sendErrorResponse(resp, "Currency does not exist", HttpStatusCode.NOT_FOUND.getValue());
                return;
            }

            if (exchangeRatesService.existExchangeRateByBaseCurrencyAndTargetCurrency(baseCode, targetCode)) {
                JsonUtil.sendErrorResponse(resp, "Exchange rate already exist", HttpStatusCode.CONFLICT.getValue());
                return;
            }


            ExchangeRatesDto dto = ExchangeRatesConvertor.toDto(exchangeRatesService.addExchangeRateByCurrenciesCods(baseCode, targetCode, rate));
            JsonUtil.sendJsonResponse(resp, dto, HttpStatusCode.CREATED.getValue());
        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal Server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        } catch (NoSuchElementException e) {
            JsonUtil.sendErrorResponse(resp, "Exchange rate not found", HttpStatusCode.NOT_FOUND.getValue());
        }

    }
}
