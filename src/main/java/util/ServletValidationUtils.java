package util;

import response.ErrorResponse;
import repository.CurrencyRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


public class ServletValidationUtils {

    private static final CurrencyRepository CURRENCY_REPOSITORY = new CurrencyRepository();


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
