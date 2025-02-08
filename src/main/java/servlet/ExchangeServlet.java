package servlet;

import model.ExchangeDto;
import response.ErrorResponse;
import response.Response;
import service.ExchangeDtoService;
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

import static util.ServletUtils.*;


@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {


    ExchangeDtoService exchangeDtoService = new ExchangeDtoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String baseCode = validateParam(req, resp,"from");
            String targetCode = validateParam(req, resp,"to");
            String amountSting = validateParam(req, resp,"amount");

            baseCode = baseCode.toUpperCase();
            targetCode = targetCode.toUpperCase();

            BigDecimal amount = new BigDecimal(amountSting).setScale(2, RoundingMode.HALF_UP);

            ExchangeDto dto = exchangeDtoService.getExchangeDto(baseCode, targetCode, amount);

            Response.sendOk(resp, dto);

        } catch (SQLException | NoSuchElementException | NumberFormatException e) {
            handleException(resp, e, "Exchange rate", "Amount");
        } catch (IllegalArgumentException e) {
            ErrorResponse.sendBadRequest(resp, "Currency pathInfo is missing");
        }
    }




}
