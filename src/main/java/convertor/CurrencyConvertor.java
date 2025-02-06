package convertor;

import dto.CurrencyDto;
import model.Currency;

public class CurrencyConvertor {

    public static CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign());
    }
}
