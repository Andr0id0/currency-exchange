package servlet;

import model.ExchangeDto;
import response.ErrorResponse;
import response.Response;
import service.ExchangeDtoService;
import util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static util.ServletUtils.isNotValidParams;
import static util.ServletUtils.handleException;


@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {


    ExchangeDtoService exchangeDtoService = new ExchangeDtoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String baseCode = req.getParameter("from");
        String targetCode = req.getParameter("to");
        String amountSting = req.getParameter("amount");


        if (isNotValidParams(baseCode, targetCode, amountSting, resp)) {
            return;
        }
        baseCode = baseCode.toUpperCase();
        targetCode = targetCode.toUpperCase();

        try {
            double amount = Double.parseDouble(amountSting);

            ExchangeDto dto = exchangeDtoService.getExchangeDto(baseCode, targetCode, amount);

            Response.sendOk(resp, dto);

        }
        catch (SQLException e) {
            ErrorResponse.sendInternalServerError(resp, "Internal Server error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            ErrorResponse.sendNotFound(resp, "Exchange rate not found");
        } catch (NumberFormatException e) {
            ErrorResponse.sendBadRequest(resp, "Amount is not number");
        }
    }




}
