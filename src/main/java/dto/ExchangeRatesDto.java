package dto;

public class ExchangeRatesDto {
    int id;
    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    double rate;


    public ExchangeRatesDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public CurrencyDto getBaseCurrencyId() {
        return baseCurrency;
    }

    public int getId() {
        return id;
    }

    public CurrencyDto getTargetCurrencyId() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

}
