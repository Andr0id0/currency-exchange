package servlets;

import convertors.ExchangeRatesConvertor;
import dao.ExchangeRatesDao;
import dto.ExchangeRatesDto;
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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

          List<ExchangeRates> exchangeRates = exchangeRatesDao.getAllExchangeRates();

          List<ExchangeRatesDto> dtos = exchangeRates.stream()
                  .map(ExchangeRatesConvertor::toDto).collect(Collectors.toList());

          JsonUtil.sendJsonResponse(resp, dtos, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal Server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        }

    }

}
