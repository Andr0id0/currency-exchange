package dto;

import java.math.BigDecimal;

public class ExchangeRequestDto {

    String baseCurrencyCode;
    String tagetCurrencyCode;
    BigDecimal amount;

    public ExchangeRequestDto(String baseCurrencyCode, String tagetCurrencyCode, BigDecimal amount) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.tagetCurrencyCode = tagetCurrencyCode;
        this.amount = amount;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTagetCurrencyCode() {
        return tagetCurrencyCode;
    }

    public void setTagetCurrencyCode(String tagetCurrencyCode) {
        this.tagetCurrencyCode = tagetCurrencyCode;
    }
}
