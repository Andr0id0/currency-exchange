package servlet;

import dto.ExchangeRatesRequestDto;
import service.ExchangeRatesConvertorService;
import response.ErrorResponse;
import response.Response;
import repository.ExchangeRatesRepository;
import dto.ExchangeRatesResultDto;
import model.ExchangeRates;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static util.ServletValidationUtils.*;


@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository();
    ExchangeRatesConvertorService convertorService = new ExchangeRatesConvertorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            List<ExchangeRates> exchangeRates = exchangeRatesRepository.getAll();

            List<ExchangeRatesResultDto> dtos = new ArrayList<>();

            for (ExchangeRates rates : exchangeRates) {
                ExchangeRatesResultDto dto = convertorService.toDto(rates);
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

            ExchangeRatesRequestDto requestDto = new ExchangeRatesRequestDto(baseCode, targetCode, rate);
            ExchangeRatesResultDto dto = convertorService.toDto(exchangeRatesRepository.add(requestDto));

            Response.sendCreated(resp, dto);
        } catch (SQLDataException e) {
            ErrorResponse.sendConflict(resp, e.getMessage());
        } catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NumberFormatException e) {
            ErrorResponse.sendBadRequest(resp,"Rate is not number");
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Exchange rate not found");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }
    }






}
