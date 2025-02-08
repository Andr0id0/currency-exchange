package model;

import dto.CurrencyDto;

import java.math.BigDecimal;

public class ExchangeDto {

    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;

    public ExchangeDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
