package model;

import java.math.BigDecimal;

public class ExchangeRates {
    int id;
    int baseCurrencyId;
    int targetCurrencyId;
    BigDecimal rate;


    public ExchangeRates(int id, int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }


    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public int getId() {
        return id;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
