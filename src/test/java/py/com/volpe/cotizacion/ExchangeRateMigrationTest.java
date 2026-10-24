package py.com.volpe.cotizacion;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * V5 turns the query_response/query_response_detail of every query into an exchange_rate per price.
 * <p>
 * The legacy rows are loaded in a database migrated up to V4, then V5 runs over them.
 */
class ExchangeRateMigrationTest {

    private static JdbcTemplate jdbc;

    @BeforeAll
    static void migrateLegacyData() {
        String name = "v5_" + UUID.randomUUID().toString().replace("-", "");
        new JdbcTemplate(EmbeddedPostgresConfiguration.server().getPostgresDatabase())
                .execute("CREATE DATABASE " + name);
        DataSource ds = EmbeddedPostgresConfiguration.server().getDatabase("postgres", name);
        jdbc = new JdbcTemplate(ds);

        Flyway.configure().dataSource(ds).target("4").load().migrate();

        long mundial = id("SELECT id FROM place WHERE code = 'MUNDIAL'");
        long branch = id("SELECT min(id) FROM place_branch WHERE place_id = " + mundial);
        long gnb = id("SELECT id FROM place WHERE code = 'GNB'");

        // before the executions existed
        query(1, mundial, branch, null, "2018-07-01 10:00", "USD", 5700, 5750, "EUR", 6600, 6700);
        // production has details moved by hand to old_* tables, they are part of the history
        query(2, mundial, branch, null, "2018-07-01 10:10");
        jdbc.execute("CREATE TABLE old_query_response_detail (id bigint, iso_code varchar(255), purchase_price bigint, purchase_trend bigint, sale_price bigint, sale_trend bigint, query_response_id bigint)");
        jdbc.update("INSERT INTO old_query_response_detail VALUES (1000, 'USD', 5690, 0, 5750, 0, 2)");
        execution(10, "2018-07-23 10:00");
        query(11, mundial, branch, 10L, "2018-07-23 10:00", "USD", 5700, 5750, "EUR", 6600, 6750);
        query(12, gnb, null, 10L, "2018-07-23 10:00", "USD", 5710, 5740);
        execution(20, "2018-07-23 10:10");
        // the second USD is a gatherer bug, only the first one is kept
        query(21, mundial, branch, 20L, "2018-07-23 10:10", "USD", 5710, 5750, "EUR", 6600, 6750, "USD", 5710, 0);
        query(22, gnb, null, 20L, "2018-07-23 10:10", "USD", 5710, 5740);
        execution(30, "2018-07-23 10:20");
        query(31, mundial, branch, 30L, "2018-07-23 10:20", "USD", 5700, 5750);

        Flyway.configure().dataSource(ds).load().migrate();
    }

    @Test
    void thereIsARowPerPriceOfEachPlaceBranchAndCurrency() {
        assertThat(rates("USD"))
                .extracting(r -> r.get("place"), r -> r.get("purchase_price"), r -> r.get("first_seen"),
                        r -> r.get("last_seen"), r -> r.get("last_execution_id"), r -> r.get("latest"))
                .containsExactly(
                        tuple("GNB", 5710L, "2018-07-23 10:00", "2018-07-23 10:10", 20L, true),
                        tuple("MUNDIAL", 5700L, "2018-07-01 10:00", "2018-07-01 10:00", null, false),
                        tuple("MUNDIAL", 5690L, "2018-07-01 10:10", "2018-07-01 10:10", null, false),
                        tuple("MUNDIAL", 5700L, "2018-07-23 10:00", "2018-07-23 10:00", 10L, false),
                        tuple("MUNDIAL", 5710L, "2018-07-23 10:10", "2018-07-23 10:10", 20L, false),
                        tuple("MUNDIAL", 5700L, "2018-07-23 10:20", "2018-07-23 10:20", 30L, true));
    }

    @Test
    void aCurrencyRepeatedInTheSameQueryKeepsTheFirstRate() {
        assertThat(rates("USD")).extracting(r -> r.get("sale_price")).doesNotContain(0L);
    }

    @Test
    void aChangeInTheSalePriceAlsoStartsANewRow() {
        assertThat(rates("EUR"))
                .extracting(r -> r.get("sale_price"), r -> r.get("first_seen"), r -> r.get("last_seen"),
                        r -> r.get("latest"))
                .containsExactly(
                        tuple(6700L, "2018-07-01 10:00", "2018-07-01 10:00", false),
                        tuple(6750L, "2018-07-23 10:00", "2018-07-23 10:10", true));
    }

    @Test
    void theOldTablesAreGone() {
        assertThat(jdbc.queryForList("SELECT tablename FROM pg_tables WHERE schemaname = 'public'", String.class))
                .contains("exchange_rate", "execution")
                .doesNotContain("query_response", "query_response_detail", "old_query_response_detail");
    }

    private static List<Map<String, Object>> rates(String iso) {
        return jdbc.queryForList("""
                SELECT p.code AS place, r.purchase_price, r.sale_price,
                       to_char(r.first_seen, 'YYYY-MM-DD HH24:MI') AS first_seen,
                       to_char(r.last_seen, 'YYYY-MM-DD HH24:MI') AS last_seen,
                       r.last_execution_id, r.latest
                FROM exchange_rate r JOIN place p ON p.id = r.place_id
                WHERE r.iso_code = ?
                ORDER BY p.code, r.first_seen""", iso);
    }

    private static void execution(long id, String date) {
        jdbc.update("INSERT INTO execution (id, date) VALUES (?, ?::timestamp)", id, date);
    }

    /**
     * A query_response with its details, given as (iso, purchase, sale) triples.
     */
    private static void query(long id, long place, Long branch, Long execution, String date, Object... details) {
        jdbc.update("INSERT INTO query_response (id, date, place_id, branch_id, execution_id, full_data) VALUES (?, ?::timestamp, ?, ?, ?, '<html/>')",
                id, date, place, branch, execution);
        for (int i = 0; i < details.length; i += 3) {
            jdbc.update("""
                            INSERT INTO query_response_detail (id, query_response_id, iso_code, purchase_price, sale_price, purchase_trend, sale_trend)
                            VALUES (?, ?, ?, ?, ?, 0, 0)""",
                    id * 100 + i, id, details[i], ((Number) details[i + 1]).longValue(), ((Number) details[i + 2]).longValue());
        }
    }

    private static long id(String sql) {
        return jdbc.queryForObject(sql, Long.class);
    }
}
