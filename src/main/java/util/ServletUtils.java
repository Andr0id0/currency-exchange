package util;

import response.ErrorResponse;
import service.CurrencyService;
import service.ExchangeRatesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class ServletUtils {

    private static final ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    private static final CurrencyService currencyService = new CurrencyService();


    public static String validateParam(HttpServletRequest req, HttpServletResponse resp, String param) throws IOException, IllegalArgumentException {
        String parameter = req.getParameter(param);
        if (parameter == null || parameter.isEmpty() ) {
            ErrorResponse.sendBadRequest(resp, "Required form field is missing");
            throw new IllegalArgumentException("Missing required parameter: " + param);
        }
        return parameter;
    }

    public static String validatePathCode(HttpServletResponse resp, String pathInfo) throws IOException, IllegalArgumentException {
        if (pathInfo == null || pathInfo.equals("/")) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
            throw new IllegalArgumentException("Missing required parameter");
        }
        pathInfo = pathInfo.substring(1);
        if (pathInfo.length() != 3) {
            ErrorResponse.sendBadRequest(resp, "Use format: XXX");
            throw new IllegalArgumentException("Missing required parameter");
        }
        return pathInfo;
    }

    public static String validateTwoPathCods(HttpServletResponse resp, String pathInfo) throws IOException, IllegalArgumentException {
        if (pathInfo == null || pathInfo.equals("/")) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
            throw new IllegalArgumentException("Missing required parameter");
        }
        pathInfo = pathInfo.substring(1);
        if (pathInfo.length() != 6) {
            ErrorResponse.sendBadRequest(resp, "Use format: XXXYYY");
            throw new IllegalArgumentException("Missing required parameter");
        }
        return pathInfo;
    }

    public static String[] extractCurrencyCodes(String pathInfo) {
        String baseCode = pathInfo.substring(0, 3).toUpperCase();
        String targetCode = pathInfo.substring(3, 6).toUpperCase();
        return new String[]{baseCode, targetCode};
    }


    public static void isCodsNotExist(String baseCode, String targetCode, HttpServletResponse resp) throws SQLException, IOException {
        if (!currencyService.existCode(baseCode) && !currencyService.existCode(targetCode)) {
            ErrorResponse.sendNotFound(resp, "Both currency from a currency pair does not exist");
            throw new NoSuchElementException();
        }
        if (!currencyService.existCode(baseCode) || !currencyService.existCode(targetCode)) {
            ErrorResponse.sendNotFound(resp, "Currency does not exist");
            throw new NoSuchElementException();
        }
    }

    public static void isCodsUsed(String baseCode, String targetCode, HttpServletResponse resp) throws IOException, SQLException {
        if (exchangeRatesService.existExchangeRateByBaseCurrencyAndTargetCurrency(baseCode, targetCode)) {
            ErrorResponse.sendConflict(resp, "Exchange rate already exist");
            throw new NoSuchElementException();
        }
    }

     public static void handleException(HttpServletResponse resp, Exception e, String noSuchElement, String noCastNumber) throws IOException {
        if (e instanceof SQLException) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } else if (e instanceof NoSuchElementException) {
            ErrorResponse.sendNotFound(resp,  noSuchElement + " not found");
        } else if (e instanceof NumberFormatException) {
            ErrorResponse.sendBadRequest(resp, noCastNumber + " is not number");
        }
    }

    public static String getRequestBodyParam(HttpServletRequest req, String param) throws IOException, IllegalArgumentException {
        String body = req.getReader().lines().collect(Collectors.joining());

        return Arrays.stream(body.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2 && arr[0].equals(param))
                .map(arr -> URLDecoder.decode(arr[1], StandardCharsets.UTF_8))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing required parameter: " + param));
    }


}
