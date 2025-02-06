package util;

import response.ErrorResponse;
import service.CurrencyService;
import service.ExchangeRatesService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


public class ServletUtils {

    private static final ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    private static final CurrencyService currencyService = new CurrencyService();

    public static boolean isNotValidPathInfo(HttpServletResponse resp, String pathInfo) throws IOException {
        if (pathInfo == null || pathInfo.equals("/")) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
            return true;
        }

        return false;
    }
    public static boolean isNotValidPathParam(HttpServletResponse resp, String pathInfo) throws IOException {
        pathInfo = pathInfo.substring(1);
        if (pathInfo.length() != 3) {
            ErrorResponse.sendBadRequest(resp, "Use format: XXX");
            return true;
        }
        return false;
    }

    public static boolean isNotValidTwoPathParams(HttpServletResponse resp, String pathInfo) throws IOException {
        pathInfo = pathInfo.substring(1);
        if (pathInfo.length() != 6) {
            ErrorResponse.sendBadRequest(resp, "Use format: XXXYYY");
            return true;
        }
        return false;
    }

    public static String[] extractCurrencyCodes(String pathInfo) {
        String baseCode = pathInfo.substring(0, 3).toUpperCase();
        String targetCode = pathInfo.substring(3, 6).toUpperCase();
        return new String[]{baseCode, targetCode};
    }

    public static boolean isNotValidParams(String baseCode, String targetCode, String digit, HttpServletResponse resp) throws IOException {
        if (baseCode == null || targetCode == null || digit == null ||
                baseCode.isEmpty() || targetCode.isEmpty() || digit.isEmpty()) {
            ErrorResponse.sendBadRequest(resp, "Required form field is missing");
            return true;
        }
        return false;
    }
    public static boolean isNotValidParam(String string, HttpServletResponse resp) throws IOException {
        if (string == null || string.isEmpty()) {
            ErrorResponse.sendBadRequest(resp, "Required form field is missing");
            return true;
        }
        return false;
    }

    public static boolean isCodsNotExist(String baseCode, String targetCode, HttpServletResponse resp) throws SQLException, IOException {
        if (!currencyService.existCode(baseCode) && !currencyService.existCode(targetCode)) {
            ErrorResponse.sendNotFound(resp, "Both currency from a currency pair does not exist");
            return true;
        }
        if (!currencyService.existCode(baseCode) || !currencyService.existCode(targetCode)) {
            ErrorResponse.sendNotFound(resp, "Currency does not exist");
            return true;
        }
        return false;
    }

    public static boolean isCodsUsed(String baseCode, String targetCode, HttpServletResponse resp) throws IOException, SQLException {
        if (exchangeRatesService.existExchangeRateByBaseCurrencyAndTargetCurrency(baseCode, targetCode)) {
            ErrorResponse.sendConflict(resp, "Exchange rate already exist");
            return true;
        }
        return false;
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

}
