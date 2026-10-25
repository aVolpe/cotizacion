package py.com.volpe.cotizacion.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuoteTest {

    @Test
    void theAmountIsRoundedHalfUpToTwoDecimals() {
        assertEquals(new BigDecimal("3.40"), Quote.of("ARS", new BigDecimal("3.4")).amount());
        assertEquals(new BigDecimal("78.31"), Quote.of("ARS", new BigDecimal("78.3148")).amount());
        assertEquals(new BigDecimal("3.46"), Quote.of("ARS", new BigDecimal("3.455")).amount());
        assertEquals(new BigDecimal("7000.00"), Quote.of("USD", 7000).amount());
    }

    @Test
    void theIsoCodeAndTheAmountAreRequired() {
        assertThrows(NullPointerException.class, () -> Quote.of(null, 7000));
        assertThrows(NullPointerException.class, () -> Quote.of("USD", (BigDecimal) null));
    }

    @Test
    void quotesWithOtherScaleAreEqual() {
        assertEquals(Quote.of("ARS", "3.4"), Quote.of("ARS", "3.40"));
        assertEquals(Quote.of("ARS", new BigDecimal("5")), Quote.of("ARS", 5));
        assertEquals(Quote.of("ARS", "3.4").hashCode(), Quote.of("ARS", "3.40").hashCode());
        assertNotEquals(Quote.of("ARS", "3.45"), Quote.of("ARS", "3.46"));
        assertNotEquals(Quote.of("ARS", 5), Quote.of("BRL", 5));
    }

    @Test
    void ofParsesAPlainDecimal() {
        assertEquals(new BigDecimal("9400.00"), Quote.of("USD", "9400.00").amount());
        assertEquals(new BigDecimal("4.20"), Quote.of("ARS", " 4.2 ").amount());
    }

    @Test
    void parseLocal() {
        assertEquals(Quote.of("USD", "7028.72"), Quote.parseLocal("USD", "7.028,72"));
        assertEquals(Quote.of("ARS", "3.45"), Quote.parseLocal("ARS", " 3,45 "));
        assertEquals(Quote.of("USD", 5660), Quote.parseLocal("USD", "5.660"));
        assertEquals(Quote.of("USD", "1234567.50"), Quote.parseLocal("USD", "1.234.567,5"));
        assertEquals(Quote.of("ARS", 140), Quote.parseLocal("ARS", "140"));
        assertEquals(Quote.of("ARS", "6.50"), Quote.parseLocal("ARS", "6,50"));
    }

    @Test
    void sameAmountComparesTheValueNotTheScale() {
        assertTrue(Quote.of("ARS", "3.4").sameAmount(Quote.of("ARS", "3.40")));
        assertFalse(Quote.of("ARS", "3.45").sameAmount(Quote.of("ARS", "3.46")));
    }

    @Test
    void quotesOfOtherCurrenciesCantBeCompared() {
        assertThrows(IllegalArgumentException.class, () -> Quote.of("USD", 5).sameAmount(Quote.of("ARS", 5)));
    }

    @Test
    void theIsoCodeOfADetailIsTheOneOfItsQuotes() {
        QueryResponseDetail detail = new QueryResponseDetail(Quote.of("ARS", "3.40"), Quote.of("ARS", "3.80"));

        assertEquals("ARS", detail.getIsoCode());
        assertEquals(detail, detail.dup());
        assertThrows(IllegalArgumentException.class, () -> new QueryResponseDetail(Quote.of("USD", 7000), Quote.of("ARS", 5)));
    }

    @Test
    void anExchangeRateWithOtherScaleHasTheSamePrices() {
        ExchangeRate rate = new ExchangeRate();
        rate.setIsoCode("ARS");
        rate.setPurchasePrice(new BigDecimal("3.4"));
        rate.setSalePrice(new BigDecimal("5"));

        assertEquals(Quote.of("ARS", "3.40"), rate.getPurchase());
        assertTrue(rate.hasTheSamePrices(new QueryResponseDetail(Quote.of("ARS", "3.40"), Quote.of("ARS", "5.00"))));
        assertFalse(rate.hasTheSamePrices(new QueryResponseDetail(Quote.of("ARS", "3.41"), Quote.of("ARS", "5.00"))));
    }
}
