package dto;

import java.math.BigDecimal;

public class ExchangeRatesRequestDto {

    String baseCurrencyCode;
    String tagetCurrencyCode;
    BigDecimal rate;


    public ExchangeRatesRequestDto(String baseCurrencyCode, String tagetCurrencyCode, BigDecimal rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.tagetCurrencyCode = tagetCurrencyCode;
        this.rate = rate;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getTagetCurrencyCode() {
        return tagetCurrencyCode;
    }

    public void setTagetCurrencyCode(String tagetCurrencyCode) {
        this.tagetCurrencyCode = tagetCurrencyCode;
    }
}
