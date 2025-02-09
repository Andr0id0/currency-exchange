package service;

import convertor.CurrencyConvertor;
import dto.CurrencyDto;
import dto.ExchangeRatesResultDto;
import model.Currency;
import model.ExchangeRates;
import repository.CurrencyRepository;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;


public class ExchangeRatesConvertorService {

    private static final CurrencyRepository CURRENCY_REPOSITORY = new CurrencyRepository();


    public static ExchangeRatesResultDto toDto(ExchangeRates exchangeRates) throws SQLException, NoSuchElementException {
        return new ExchangeRatesResultDto(
                exchangeRates.getId(),
                getCurrencyDtoById(exchangeRates.getBaseCurrencyId()),
                getCurrencyDtoById(exchangeRates.getTargetCurrencyId()),
                exchangeRates.getRate());
    }

    private static CurrencyDto getCurrencyDtoById(int id) throws SQLException, NoSuchElementException {
        Optional<Currency> currency = CURRENCY_REPOSITORY.getById(id);
        if (currency.isEmpty()) {
            throw new NoSuchElementException();
        }
        return CurrencyConvertor.toDto(currency.get());
    }

}
