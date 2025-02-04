package model;

public class Currency {
    int id;
    String code;
    String fullName;
    String sign;

    public Currency(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public int getId() {
        return id;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        this.id = id;
    }
}
