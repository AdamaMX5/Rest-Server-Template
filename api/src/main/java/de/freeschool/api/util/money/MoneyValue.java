package de.freeschool.api.util.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A dedicated class for handling money values.
 * <p>
 * Values are always rounded down to two decimal places. Instances are immutable.
 * </p>
 */
public class MoneyValue {

    private final BigDecimal value;

    public static MoneyValue of(String value) {
        return new MoneyValue(value);
    }

    public MoneyValue(String value) {
        this(new BigDecimal(value));
    }

    public MoneyValue(double value) {
        this(BigDecimal.valueOf(value));
    }

    public MoneyValue(BigDecimal value) {
        this.value = value.setScale(2, RoundingMode.DOWN);
    }

    public long getMajor() {
        return value.longValue();
    }

    public byte getMinor() {
        return value.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).byteValueExact();
    }

    public BigDecimal getDecimalValue() {
        return value;
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public MoneyValue add(MoneyValue other) {
        return new MoneyValue(value.add(other.value));
    }

    public MoneyValue subtract(MoneyValue other) {
        return new MoneyValue(value.subtract(other.value));
    }

    public MoneyValue multiply(double multiplier) {
        return new MoneyValue(value.multiply(BigDecimal.valueOf(multiplier)));
    }

    public MoneyValue divide(double divisor) {
        return new MoneyValue(value.divide(BigDecimal.valueOf(divisor)));
    }

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.DOWN).toString();
    }

    public boolean isGreaterThan(MoneyValue other) {
        return value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(MoneyValue other) {
        return value.compareTo(other.value) < 0;
    }

    public boolean isEqual(MoneyValue other) {
        return value.compareTo(other.value) == 0;
    }

    public boolean isLessThanOrEqual(MoneyValue other) {
        return value.compareTo(other.value) <= 0;
    }

    public boolean isGreaterThanOrEqual(MoneyValue other) {
        return value.compareTo(other.value) >= 0;
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MoneyValue that = (MoneyValue) obj;
        return isEqual(that);
    }

}
