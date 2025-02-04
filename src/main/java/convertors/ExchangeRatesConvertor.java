package convertors;

import dto.ExchangeRatesDto;
import model.ExchangeRates;


public class ExchangeRatesConvertor {

    public static ExchangeRatesDto toDto(ExchangeRates exchangeRates) {
        return new ExchangeRatesDto(
                exchangeRates.getId(),
                exchangeRates.getBaseCurrencyId(),
                exchangeRates.getTargetCurrencyId(),
                exchangeRates.getRate());
    }

}
