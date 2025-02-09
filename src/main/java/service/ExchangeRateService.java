package service;

import convertor.CurrencyConvertor;
import model.Currency;
import model.ExchangeDto;
import model.ExchangeRates;
import repository.CurrencyRepository;
import repository.ExchangeRatesRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class ExchangeRateService {

    private static final String CROSS_CODE = "USD";

    ExchangeRatesRepository exchangeRatesRepository = new ExchangeRatesRepository();
    CurrencyRepository currencyRepository = new CurrencyRepository();

    public ExchangeRates getExchangeRate(String baseCode, String targetCode) throws SQLException, NoSuchElementException {

        if (isExchangeRateExist(baseCode, targetCode))
            return defaultExchangeRate(baseCode, targetCode);

        if (isReversedExchangeRateExist(baseCode, targetCode))
            return reversedExchangeRate(targetCode, baseCode);


        if (isCrossExchangeRateExist(baseCode, targetCode, CROSS_CODE))
            return crossExchangeRate(baseCode, targetCode, CROSS_CODE);

        else throw new NoSuchElementException();
    }

    public ExchangeDto getExchange(String baseCode, String targetCode, BigDecimal amount) throws SQLException, NoSuchElementException {

        if (isExchangeRateExist(baseCode, targetCode))
            return defaultExchangeDto(baseCode, targetCode, amount);

        if (isReversedExchangeRateExist(baseCode, targetCode))
            return reversedExchangeDto(targetCode, baseCode, amount);

        if (isCrossExchangeRateExist(baseCode, targetCode, CROSS_CODE))
            return crossExchangeDto(baseCode, targetCode, amount);

        else throw new NoSuchElementException();
    }

    private boolean isExchangeRateExist(String baseCode, String targetCode) throws SQLException {
        return exchangeRatesRepository.exist(baseCode, targetCode);
    }

    private boolean isReversedExchangeRateExist(String baseCode, String targetCode) throws SQLException {
        return exchangeRatesRepository.exist(targetCode, baseCode);
    }

    private boolean isCrossExchangeRateExist(String baseCode, String targetCode, String crossCode) throws SQLException {
        return isExchangeRateExist(crossCode, baseCode) && isExchangeRateExist(crossCode, targetCode);
    }

    private ExchangeRates defaultExchangeRate(String baseCode, String targetCode) throws SQLException {
        return exchangeRate(baseCode, targetCode, false);
    }

    private ExchangeRates reversedExchangeRate(String baseCode, String targetCode) throws SQLException {
        return exchangeRate(baseCode, targetCode, true);
    }

    private ExchangeRates crossExchangeRate(String baseCode, String targetCode, String crossCode) throws SQLException {
        ExchangeRates usdBase = defaultExchangeRate(crossCode, baseCode);
        ExchangeRates usdTarget = defaultExchangeRate(crossCode, targetCode);

        BigDecimal newExchangeRate = usdTarget.getRate().divide(usdBase.getRate(), 6, RoundingMode.HALF_UP);

        return new ExchangeRates(0,
                usdBase.getTargetCurrencyId(),
                usdTarget.getTargetCurrencyId(),
                newExchangeRate);
    }
    private ExchangeDto defaultExchangeDto(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        return exchangeDto(baseCode, targetCode, amount, false);
    }

    private ExchangeDto reversedExchangeDto(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        return exchangeDto(baseCode, targetCode, amount, true);
    }

    private ExchangeDto crossExchangeDto(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        ExchangeDto usdBase = defaultExchangeDto(CROSS_CODE, baseCode, amount);
        ExchangeDto usdTarget = defaultExchangeDto(CROSS_CODE, targetCode, amount);

        BigDecimal exchangeRate = usdTarget.getRate().divide(usdBase.getRate(), 6, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_UP);

        return new ExchangeDto(
                usdBase.getTargetCurrency(),
                usdTarget.getTargetCurrency(),
                exchangeRate, amount, convertedAmount);
    }

    private ExchangeRates exchangeRate(String baseCode, String targetCode, boolean isReverse) throws SQLException {
        ExchangeRates exchangeRate = exchangeRatesRepository.getByCods(baseCode, targetCode);
        Currency base = currencyRepository.getByCode(baseCode);
        Currency target = currencyRepository.getByCode(targetCode);
        BigDecimal newExchangeRate = (isReverse) ? (BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP)) : (exchangeRate.getRate());
        int baseId = base.getId();
        int targetId = target.getId();
        if (isReverse) {
            int temp = targetId;
            targetId = baseId;
            baseId = temp;
        }
        return new ExchangeRates(exchangeRate.getId(),
                baseId,
                targetId,
                newExchangeRate);
    }

    private ExchangeDto exchangeDto(String baseCode, String targetCode, BigDecimal amount, boolean isReverse) throws SQLException {
        ExchangeRates exchangeRate = exchangeRatesRepository.getByCods(baseCode, targetCode);
        Currency base = currencyRepository.getByCode(baseCode);
        Currency target = currencyRepository.getByCode(targetCode);
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
