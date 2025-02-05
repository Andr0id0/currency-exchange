package servlets;

import dao.ExchangeRatesService;
import dto.ExchangeRatesDto;
import util.HttpStatusCode;
import util.JsonUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendErrorResponse(resp, "Currency pathInfo is missing", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        pathInfo = pathInfo.substring(1);

        if (pathInfo.length() != 6) {
            JsonUtil.sendErrorResponse(resp, "Use format: XXXYYY", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        String base = pathInfo.substring(0, 3);
        String target = pathInfo.substring(3, 6);

        try {
            ExchangeRatesDto dto = exchangeRatesService.getExchangeRateDtoByBaseCurrencyAndTargetCurrency(base, target);

            JsonUtil.sendJsonResponse(resp, dto, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        } catch (NoSuchElementException e) {
            JsonUtil.sendErrorResponse(resp, "Exchange rate not found", HttpStatusCode.NOT_FOUND.getValue());
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

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendErrorResponse(resp, "Currency pathInfo is missing", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        pathInfo = pathInfo.substring(1);

        if (pathInfo.length() != 6) {
            JsonUtil.sendErrorResponse(resp, "Use format: XXXYYY", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        String base = pathInfo.substring(0, 3);
        String target = pathInfo.substring(3, 6);


        String rateString = req.getParameter("rate");
        if (rateString == null || rateString.isEmpty()) {
            JsonUtil.sendErrorResponse(resp, "Required form field is missing", HttpStatusCode.BAD_REQUEST.getValue());
            return;
        }

        double rate = Double.parseDouble(rateString);

        try {

            ExchangeRatesDto dto = exchangeRatesService.updateExchangeRate(base, target, rate);
            JsonUtil.sendJsonResponse(resp, dto, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        } catch (NoSuchElementException e) {
            JsonUtil.sendErrorResponse(resp, "Exchange rate not found", HttpStatusCode.NOT_FOUND.getValue());
        }


        resp.getWriter().write("PATCH request handled");
    }

    private boolean isPatchRequest(HttpServletRequest req) {
        return "PATCH".equals(req.getMethod());
    }

}
