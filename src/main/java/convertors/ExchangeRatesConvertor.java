package convertors;

import dto.ExchangeRatesDto;
import model.Currency;
import model.ExchangeRates;


public class ExchangeRatesConvertor {

    public static ExchangeRatesDto toDto(ExchangeRates exchangeRates, Currency base, Currency target) {
        return new ExchangeRatesDto(
                exchangeRates.getId(),
                CurrencyConvertor.toDto(base),
                CurrencyConvertor.toDto(target),
                exchangeRates.getRate());
    }

}
