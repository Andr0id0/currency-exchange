package dto;

import java.math.BigDecimal;

public class ExchangeRatesDto {
    int id;
    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    BigDecimal rate;


    public ExchangeRatesDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public int getId() {
        return id;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

}
