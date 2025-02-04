package dto;

public class CurrencyDto {
    int id;
    String code;
    String fullName;
    String sign;

    public CurrencyDto(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getSign() {
        return sign;
    }

    public String getFullName() {
        return fullName;
    }

}
