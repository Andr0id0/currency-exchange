package servlets;

import dao.CurrencyDao;
import dto.CurrencyDto;
import model.Currency;
import convertors.CurrencyConvertor;
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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = currencyDao.getAllCurrencies();

            List<CurrencyDto> dtos = currencies.stream()
                    .map(CurrencyConvertor::toDto).collect(Collectors.toList());

            JsonUtil.sendJsonResponse(resp, dtos, HttpStatusCode.OK.getValue());

        } catch (SQLException e) {
            JsonUtil.sendErrorResponse(resp, "Internal server error: " +  e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        }

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String code = req.getParameter("code");
            String fullName = req.getParameter("full_name");
            String sign = req.getParameter("sign");

            if (code == null || fullName == null || sign == null ||
                code.isEmpty() || fullName.isEmpty() || sign.isEmpty()) {

                JsonUtil.sendErrorResponse(resp, "Required form field is missing", HttpStatusCode.BAD_REQUEST.getValue());
                return;
            }

            if (currencyDao.existCode(code) || currencyDao.existFullName(fullName) || currencyDao.existSign(sign)) {
                JsonUtil.sendErrorResponse(resp, "Currency already exist", HttpStatusCode.CONFLICT.getValue());
                return;
            }

            Currency newCurrency = new Currency(0, code, fullName, sign);

            int id = currencyDao.addCurrency(code, fullName, sign);
            newCurrency.setId(id);

            CurrencyDto dto = CurrencyConvertor.toDto(newCurrency);

            JsonUtil.sendJsonResponse(resp, dto, HttpStatusCode.CREATED.getValue());

        }  catch (Exception e) {
            JsonUtil.sendErrorResponse(resp, "Internal server error: " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
        }

    }

}
