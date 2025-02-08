package service;

import convertor.CurrencyConvertor;
import model.Currency;
import model.ExchangeDto;
import model.ExchangeRates;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class ExchangeDtoService {

    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    CurrencyService currencyService = new CurrencyService();

    public ExchangeDto getExchangeDto(String baseCode, String targetCode, BigDecimal amount) throws SQLException, NoSuchElementException {

        if (isExchangeRateExist(baseCode, targetCode)) {
            return getDefaultExchangeDto(baseCode, targetCode, amount);
        }
        if (isReversedExchangeRateExist(baseCode, targetCode)) {
            return getReversedExchangeDto(targetCode, baseCode, amount);
        }

        String usd = "USD";
        if (isExchangeRateExist(usd, baseCode) && isExchangeRateExist(usd, targetCode)) {

            ExchangeDto usdBase = getDefaultExchangeDto(usd, baseCode, amount);
            ExchangeDto usdTarget = getDefaultExchangeDto(usd, targetCode, amount);

            BigDecimal exchangeRate = usdTarget.getRate().divide(usdBase.getRate(), 6, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_UP);

            return new ExchangeDto(
                    usdBase.getTargetCurrency(),
                    usdTarget.getTargetCurrency(),
                    exchangeRate, amount, convertedAmount);

        }

        else throw new NoSuchElementException();
    }

    private boolean isExchangeRateExist(String baseCode, String targetCode) throws SQLException {
        return exchangeRatesService.existExchangeRateByBaseCurrencyAndTargetCurrency(baseCode, targetCode);
    }

    private boolean isReversedExchangeRateExist(String baseCode, String targetCode) throws SQLException {
        return exchangeRatesService.existExchangeRateByBaseCurrencyAndTargetCurrency(targetCode, baseCode);
    }


    private ExchangeDto getDefaultExchangeDto(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        return getExchangeDtoByCods(baseCode, targetCode, amount, false);
    }

    private ExchangeDto getReversedExchangeDto(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        return getExchangeDtoByCods(baseCode, targetCode, amount, true);
    }

    private ExchangeDto getExchangeDtoByCods(String baseCode, String targetCode, BigDecimal amount, boolean isReverse) throws SQLException {
        ExchangeRates exchangeRate = exchangeRatesService.getExchangeRateByBaseCodeAndTargetCode(baseCode, targetCode);
        Currency base = currencyService.getByCode(baseCode);
        Currency target = currencyService.getByCode(targetCode);
        BigDecimal newRate = (isReverse) ? (BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP)) : exchangeRate.getRate();
        BigDecimal convertedAmount = newRate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
        Currency baseCurrency = base;
        Currency targetCurrency = target;
        if (isReverse) {
            Currency temp = targetCurrency;
            targetCurrency = baseCurrency;
            baseCurrency = temp;
        }
        return new ExchangeDto(
                CurrencyConvertor.toDto(baseCurrency),
                CurrencyConvertor.toDto(targetCurrency),
                newRate, amount, convertedAmount);
    }

}
