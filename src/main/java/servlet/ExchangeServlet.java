package servlet;

import model.ExchangeDto;
import response.ErrorResponse;
import response.Response;
import service.ExchangeRateService;
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

import static util.ServletValidationUtils.*;


@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {


    ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String baseCode = validateParam(req, resp,"from");
            String targetCode = validateParam(req, resp,"to");
            String amountSting = validateParam(req, resp,"amount");

            baseCode = baseCode.toUpperCase();
            targetCode = targetCode.toUpperCase();

            BigDecimal amount = new BigDecimal(amountSting).setScale(2, RoundingMode.HALF_UP);

            ExchangeDto dto = exchangeRateService.getExchange(baseCode, targetCode, amount);

            Response.sendOk(resp, dto);

        }  catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NumberFormatException e) {
            ErrorResponse.sendBadRequest(resp,"Amount is not number");
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Exchange rate not found");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }
    }




}
