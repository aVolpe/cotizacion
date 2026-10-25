package py.com.volpe.cotizacion.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * The price of a currency: one unit of {@link #isoCode} (USD, ARS, BRL...) is worth {@link #amount} guaranies.
 * <p>
 * The amount is always in guaranies and it is stored with two decimals (the ARS is worth a few guaranies, '3,45'
 * can't be stored as 3). The constructor rounds it to {@link #SCALE} decimals ({@link RoundingMode#HALF_UP}), so
 * the {@code equals} of the record compares the values: {@code Quote.of("ARS", "3.4")} equals
 * {@code Quote.of("ARS", "3.40")}.
 *
 * @param isoCode the quoted currency, never null
 * @param amount  the guaranies that one unit of the currency is worth, never null
 */
public record Quote(String isoCode, BigDecimal amount) {

    public static final int SCALE = 2;

    public Quote {
        Objects.requireNonNull(isoCode, "isoCode");
        Objects.requireNonNull(amount, "amount");
        amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Quote of(String isoCode, BigDecimal amount) {
        return new Quote(isoCode, amount);
    }

    public static Quote of(String isoCode, long amount) {
        return new Quote(isoCode, BigDecimal.valueOf(amount));
    }

    /**
     * Parses an amount with '.' as decimal separator and without thousand separator: '9400.00', '4.20'.
     */
    public static Quote of(String isoCode, String amount) {
        return new Quote(isoCode, new BigDecimal(amount.trim()));
    }

    /**
     * Parses an amount in the local format, '.' as thousand separator and ',' as decimal separator:
     * '7.028,72', '3,45', '5.660', '140'.
     */
    public static Quote parseLocal(String isoCode, String amount) {
        return of(isoCode, amount.replace(".", "").replace(",", "."));
    }

    /**
     * True if both quotes have the same amount, regardless of the scale.
     *
     * @throws IllegalArgumentException if the quotes are of different currencies, they can't be compared
     */
    public boolean sameAmount(Quote other) {
        if (!isoCode.equals(other.isoCode)) {
            throw new IllegalArgumentException("Can't compare a " + isoCode + " quote with a " + other.isoCode + " one");
        }
        return amount.compareTo(other.amount) == 0;
    }
}
