package model;

import dto.CurrencyDto;

public class ExchangeDto {

    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    double rate;
    double amount;
    double convertedAmount;

    public ExchangeDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate, double amount, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }


    public double getAmount() {
        return amount;
    }

    public CurrencyDto getTarget() {
        return targetCurrency;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public CurrencyDto getBase() {
        return baseCurrency;
    }

    public double getRate() {
        return rate;
    }
}
