package servlet;

import dto.ExchangeRatesRequestDto;
import service.ExchangeRateService;
import service.ExchangeRatesConvertorService;
import response.ErrorResponse;
import response.Response;
import repository.ExchangeRatesRepository;
import dto.ExchangeRatesResultDto;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static util.ServletValidationUtils.*;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository();
    ExchangeRateService exchangeRateService = new ExchangeRateService();
    ExchangeRatesConvertorService convertorService = new ExchangeRatesConvertorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = validateTwoPathCods(resp, req.getPathInfo());

            String[] codes = extractCurrencyCodes(pathInfo);
            String baseCode = codes[0];
            String targetCode = codes[1];

            ExchangeRatesRequestDto requestDto = new ExchangeRatesRequestDto(baseCode, targetCode, new BigDecimal(0));
            ExchangeRatesResultDto dto = convertorService.toDto(exchangeRateService.getExchangeRate(requestDto));
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

            ExchangeRatesResultDto dto = convertorService.toDto(exchangeRatesRepository.update(baseCode, targetCode, rate));
            Response.sendOk(resp, dto);

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


    private String getRequestBodyParam(HttpServletRequest req, String param) throws IOException, IllegalArgumentException {
        String body = req.getReader().lines().collect(Collectors.joining());

        return Arrays.stream(body.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2 && arr[0].equals(param))
                .map(arr -> URLDecoder.decode(arr[1], StandardCharsets.UTF_8))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing required parameter: " + param));
    }



}
