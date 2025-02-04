package model;

public class ExchangeRates {
    int id;
    int baseCurrencyId;
    int targetCurrencyId;
    double rate;


    public ExchangeRates(int id, int baseCurrencyId, int targetCurrencyId, double rate) {
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

    public double getRate() {
        return rate;
    }
}
