package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundDouble {

    public static double roundTo6DecimalPlace(double number) {
        BigDecimal bigDecimal = new BigDecimal(number).setScale(6, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
    public static double roundTo2decimalPlace(double number) {
        BigDecimal bigDecimal = new BigDecimal(number).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

}
