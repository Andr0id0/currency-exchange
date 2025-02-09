package service;

import convertor.CurrencyConvertor;
import dto.CurrencyDto;
import dto.ExchangeRatesDto;
import model.Currency;
import model.ExchangeRates;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;


public class ExchangeRatesConvertorService {

    private static final CurrencyService currencyService = new CurrencyService();


    public static ExchangeRatesDto toDto(ExchangeRates exchangeRates) throws SQLException, NoSuchElementException {
        return new ExchangeRatesDto(
                exchangeRates.getId(),
                getCurrencyDtoById(exchangeRates.getBaseCurrencyId()),
                getCurrencyDtoById(exchangeRates.getTargetCurrencyId()),
                exchangeRates.getRate());
    }

    private static CurrencyDto getCurrencyDtoById(int id) throws SQLException, NoSuchElementException {
        Optional<Currency> currency = currencyService.getById(id);
        if (currency.isEmpty()) {
            throw new NoSuchElementException();
        }
        return CurrencyConvertor.toDto(currency.get());
    }

}
