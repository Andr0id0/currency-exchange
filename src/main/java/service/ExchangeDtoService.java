package service;

import convertor.CurrencyConvertor;
import model.Currency;
import model.ExchangeDto;
import model.ExchangeRates;
import util.RoundDouble;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public class ExchangeDtoService {

    ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
    CurrencyService currencyService = new CurrencyService();

    public ExchangeDto getExchangeDto(String baseCode, String targetCode, double amount) throws SQLException, NoSuchElementException {

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

            double exchangeRate = RoundDouble.roundTo6DecimalPlace(usdTarget.getRate() / usdBase.getRate());
            double convertedAmount = RoundDouble.roundTo2decimalPlace(exchangeRate * amount);

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


    private ExchangeDto getDefaultExchangeDto(String baseCode, String targetCode, double amount) throws SQLException {
        return getExchangeDtoByCods(baseCode, targetCode, amount, false);
    }

    private ExchangeDto getReversedExchangeDto(String baseCode, String targetCode, double amount) throws SQLException {
        return getExchangeDtoByCods(baseCode, targetCode, amount, true);
    }

    private ExchangeDto getExchangeDtoByCods(String baseCode, String targetCode, double amount, boolean isReverse) throws SQLException {
        ExchangeRates exchangeRate = exchangeRatesService.getExchangeRateByBaseCodeAndTargetCode(baseCode, targetCode);
        Currency base = currencyService.getByCode(baseCode);
        Currency target = currencyService.getByCode(targetCode);
        double newRate = RoundDouble.roundTo6DecimalPlace(isReverse ? (1 / exchangeRate.getRate()) : exchangeRate.getRate());
        double convertedAmount = RoundDouble.roundTo2decimalPlace(newRate * amount);
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
