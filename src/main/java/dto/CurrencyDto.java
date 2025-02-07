package dto;

public class CurrencyDto {
    int id;
    String code;
    String name;
    String sign;

    public CurrencyDto(int id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
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

    public String getName() {
        return name;
    }

}
