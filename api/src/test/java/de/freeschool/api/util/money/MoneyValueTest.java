package de.freeschool.api.util.money;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoneyValueTest {

    private static List<Object[]> testDoubleConstructorTestCases() {
        return List.of(new Object[]{100.50, 100, 50}, new Object[]{100.555, 100, 55}, new Object[]{100.559, 100, 55},
                new Object[]{100.554, 100, 55}, new Object[]{100.55, 100, 55}, new Object[]{100.0, 100, 0},
                new Object[]{0.0, 0, 0}, new Object[]{-100.50, -100, -50}, new Object[]{-100.555, -100, -55},
                new Object[]{-100.559, -100, -55}, new Object[]{-100.554, -100, -55}, new Object[]{-100.55, -100, -55},
                // Large numbers
                new Object[]{999999999.0, 999999999, 0}, new Object[]{1000000000.0, 1000000000, 0},
                // Small numbers
                new Object[]{0.0001, 0, 0}, new Object[]{0.0002, 0, 0},
                // Mixed large and small numbers
                new Object[]{123456789.0, 123456789, 0}, new Object[]{0.000000001, 0, 0},
                // 100 billion
                new Object[]{100000000000.0, 100000000000L, 0}
        );
    }

    @Test
    void getMajor() {
        MoneyValue moneyValue = new MoneyValue("100.50");
        assertEquals(100, moneyValue.getMajor());
    }

    @Test
    void getMinor() {
        MoneyValue moneyValue = new MoneyValue("100.50");
        assertEquals(50, moneyValue.getMinor());
    }

    @Test
    void add() {
        MoneyValue moneyValue1 = new MoneyValue("100.50");
        MoneyValue moneyValue2 = new MoneyValue("100.50");
        MoneyValue result = moneyValue1.add(moneyValue2);
        assertEquals(201, result.getMajor());
        assertEquals(0, result.getMinor());
    }

    @Test
    void subtract() {
        MoneyValue moneyValue1 = new MoneyValue("100.50");
        MoneyValue moneyValue2 = new MoneyValue("100.50");
        MoneyValue result = moneyValue1.subtract(moneyValue2);
        assertEquals(0, result.getMajor());
        assertEquals(0, result.getMinor());
    }

    @Test
    void multiply() {
        MoneyValue moneyValue = new MoneyValue("100.50");
        MoneyValue result = moneyValue.multiply(2);
        assertEquals(201, result.getMajor());
        assertEquals(0, result.getMinor());
    }

    @Test
    void divide() {
        MoneyValue moneyValue = new MoneyValue("100.50");
        MoneyValue result = moneyValue.divide(2);
        assertEquals(50, result.getMajor());
        assertEquals(25, result.getMinor());
    }

    @Test
    void testToString() {
        MoneyValue moneyValue = new MoneyValue("100.50");
        assertEquals("100.50", moneyValue.toString());
    }

    @Test
    void testNegativeValues() {
        MoneyValue moneyValue1 = new MoneyValue("-100.50");
        assertEquals(-100, moneyValue1.getMajor());
        assertEquals(-50, moneyValue1.getMinor());

        MoneyValue moneyValue2 = new MoneyValue("100.50");
        MoneyValue result = moneyValue1.add(moneyValue2);
        assertEquals(0, result.getMajor());
        assertEquals(0, result.getMinor());
        assertEquals("0.00", result.toString());
    }

    @Test
    void testValuesWithThreeDecimalPlaces() {
        MoneyValue moneyValue1 = new MoneyValue("100.555");
        assertEquals(100, moneyValue1.getMajor());
        assertEquals(55, moneyValue1.getMinor());

        MoneyValue sum = moneyValue1.add(moneyValue1);
        assertEquals(201, sum.getMajor());
        assertEquals(10, sum.getMinor());
        assertEquals("201.10", sum.toString());

        MoneyValue moneyValue2 = new MoneyValue("100.559");
        MoneyValue diff = moneyValue1.subtract(moneyValue2);
        assertEquals(0, diff.getMajor());
        assertEquals(0, diff.getMinor());
        assertEquals("0.00", diff.toString());

        MoneyValue moneyValue3 = new MoneyValue("-100.559");
        assertEquals(-100, moneyValue3.getMajor());
        assertEquals(-55, moneyValue3.getMinor());
        diff = moneyValue1.add(moneyValue3);
        assertEquals(0, diff.getMajor());
        assertEquals(0, diff.getMinor());
        assertEquals("0.00", diff.toString());
    }

    @Test
    void testComparions() {
        MoneyValue moneyValue1 = new MoneyValue("100.50");
        MoneyValue moneyValue2 = new MoneyValue("100.50");
        MoneyValue moneyValue3 = new MoneyValue("100.51");
        MoneyValue moneyValue4 = new MoneyValue("100.49");
        MoneyValue moneyValue5 = new MoneyValue("100.51");
        MoneyValue moneyValue6 = new MoneyValue("100.49");

        assertTrue(moneyValue1.isEqual(moneyValue2));
        assertTrue(moneyValue3.isGreaterThan(moneyValue2));
        assertTrue(moneyValue4.isLessThan(moneyValue2));
        assertTrue(moneyValue5.isGreaterThan(moneyValue6));
        assertTrue(moneyValue6.isLessThan(moneyValue5));

        assertFalse(moneyValue1.isGreaterThan(moneyValue1));
        assertFalse(moneyValue1.isLessThan(moneyValue1));
        assertFalse(moneyValue1.isEqual(moneyValue3));

        // test less than or equal
        assertTrue(moneyValue1.isLessThanOrEqual(moneyValue3));
        assertTrue(moneyValue1.isLessThanOrEqual(moneyValue1));
        assertFalse(moneyValue1.isLessThanOrEqual(moneyValue4));

        // test greater than or equal
        assertTrue(moneyValue1.isGreaterThanOrEqual(moneyValue4));
        assertTrue(moneyValue1.isGreaterThanOrEqual(moneyValue1));
        assertFalse(moneyValue1.isGreaterThanOrEqual(moneyValue3));

    }

    @Test
    void testIsPositiveOrNegative() {
        MoneyValue positive = new MoneyValue("100.50");
        MoneyValue negative = new MoneyValue("-100.50");
        MoneyValue zero = new MoneyValue("0");

        assertTrue(positive.isPositive());
        assertFalse(negative.isPositive());
        assertFalse(zero.isPositive());

        assertTrue(negative.isNegative());
        assertFalse(positive.isNegative());
        assertFalse(zero.isNegative());
    }

    @ParameterizedTest
    @MethodSource("testDoubleConstructorTestCases")
    void testDoubleConstructor(double value, long expectedMajor, int expectedMinor) {
        MoneyValue moneyValue = new MoneyValue(value);
        assertEquals(expectedMajor, moneyValue.getMajor());
        assertEquals(expectedMinor, moneyValue.getMinor());
        assertEquals(BigDecimal.valueOf(value).setScale(2, RoundingMode.DOWN), moneyValue.getDecimalValue());
    }

}
