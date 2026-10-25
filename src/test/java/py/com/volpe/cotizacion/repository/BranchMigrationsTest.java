package py.com.volpe.cotizacion.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import py.com.volpe.cotizacion.EmbeddedPostgresConfiguration;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Checks the branch data after all the migrations and the functions used to evolve it
 * (upsert_place, upsert_branch, deactivate_branch).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(EmbeddedPostgresConfiguration.class)
class BranchMigrationsTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PlaceBranchRepository branches;

    @Autowired
    private PlaceRepository places;

    @Test
    void mercosurBranchesHaveTheirOwnRemoteCode() {
        assertThat(activeRemoteCodes("MERCOSUR")).containsExactlyInAnyOrder("1", "2", "5");
    }

    @Test
    void norteCambiosIsLoadedWithTheBranchIdsOfTheSite() {
        Place norte = places.findByCode("NORTE").orElseThrow();
        assertThat(norte.getName()).isEqualTo("Norte Cambios");
        assertThat(norte.getType()).isEqualTo(Place.Type.BUREAU);
        assertThat(activeRemoteCodes("NORTE")).containsExactlyInAnyOrder("1", "2", "3", "4", "5", "6", "8");
    }

    @Test
    void masCambiosIsLoadedWithTheBranchesOfTheSite() {
        Place masCambios = places.findByCode("MASCAMBIOS").orElseThrow();
        assertThat(masCambios.getName()).isEqualTo("Más Cambios");
        assertThat(masCambios.getType()).isEqualTo(Place.Type.BUREAU);
        assertThat(activeRemoteCodes("MASCAMBIOS")).containsExactlyInAnyOrder("matriz", "sucursal1", "sucursal2",
                "sucursal3", "sucursal4", "sucursal5", "sucursal6", "sucursal7", "sucursal8", "sucursal9", "sucursal10",
                "sucursal11", "sucursal12", "sucursal13", "sucursal14");
        assertThat(branches.getByPlaceCodeAndActiveTrue("MASCAMBIOS"))
                .allSatisfy(b -> assertThat(b).extracting(PlaceBranch::getLatitude, PlaceBranch::getLongitude)
                        .doesNotContainNull());
    }

    @Test
    void laMonedaIsLoadedWithTheBranchesOfTheSite() {
        Place laMoneda = places.findByCode("LAMONEDA").orElseThrow();
        assertThat(laMoneda.getName()).isEqualTo("La Moneda Cambios");
        assertThat(laMoneda.getType()).isEqualTo(Place.Type.BUREAU);
        assertThat(activeRemoteCodes("LAMONEDA")).containsExactlyInAnyOrder("casa_matriz", "sucursal_jebai",
                "sucursal_centro", "sucursal_km7", "sucursal_algesa");
    }

    @Test
    void mundialBranchesUseTheTabsOfTheSiteAndKeepTheirData() {
        assertThat(branches.getByPlaceCodeAndActiveTrue("MUNDIAL"))
                .extracting(PlaceBranch::getId, PlaceBranch::getRemoteCode, PlaceBranch::getName)
                .containsExactlyInAnyOrder(
                        tuple(28298260L, "globo-center", "Globo Center"),
                        tuple(28298262L, "jebai", "Jebai Center"),
                        tuple(28298263L, "km-4", "KM 4 - Supercarretera"),
                        tuple(28298264L, "salto-del-guaira", "Salto del Guairá"),
                        tuple(28298266L, "villa-morra", "Villa Morra"));
        assertThat(branches.getByPlaceCodeAndActiveTrue("MUNDIAL"))
                .allSatisfy(b -> assertThat(b).extracting(PlaceBranch::getPhoneNumber, PlaceBranch::getSchedule,
                        PlaceBranch::getLatitude, PlaceBranch::getLongitude).doesNotContainNull());

        // Vendome and Asunción are no longer in the site
        assertThat(jdbc.queryForList(
                "SELECT b.name FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'MUNDIAL' AND NOT b.active",
                String.class)).containsExactlyInAnyOrder("Vendome", "Asunción");
    }

    @Test
    void mundialMigrationCanRunAgain() throws IOException {
        List<Map<String, Object>> before = jdbc.queryForList("SELECT * FROM place_branch ORDER BY id");

        jdbc.execute(new ClassPathResource("db/migration/V10__Mundial_branches.sql").getContentAsString(StandardCharsets.UTF_8));

        assertThat(jdbc.queryForList("SELECT * FROM place_branch ORDER BY id")).isEqualTo(before);
    }

    @Test
    void feCambiosBranchesUseTheUrlPrefixOfTheSite() {
        assertThat(activeRemoteCodes("FeCambios")).containsExactlyInAnyOrder("pal", "vmo", "cde", "slo");
        assertThat(places.findByCode("FeCambios").orElseThrow().getBranches())
                .extracting(PlaceBranch::getRemoteCode)
                .containsExactlyInAnyOrder("pal", "vmo", "cde", "slo");
    }

    @Test
    void feCambiosPedroJuanCaballeroIsDeactivatedButKeepsItsRow() {
        assertThat(jdbc.queryForObject(
                "SELECT b.active FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'FeCambios' AND b.remote_code = '8'",
                Boolean.class)).isFalse();
        assertThat(jdbc.queryForObject(
                "SELECT b.name FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'FeCambios' AND b.remote_code = 'pal'",
                String.class)).isEqualTo("Casa Matriz  - Palma");
    }

    @Test
    void cambiosChacoActiveBranchesAreTheOnesOfTheSite() {
        assertThat(activeRemoteCodes("CAMBIOS_CHACO")).containsExactlyInAnyOrder(
                "1", "2", "3", "30", "6", "7", "8", "9", "11", "13", "14", "16", "22", "23", "24", "27", "28", "29",
                "32", "40", "41");
    }

    @Test
    void cambiosChacoMovedBranchesKeepTheirRowsAndHistory() {
        // V2 created KM 7 (12) as 103 and Curupayty (14) as 105, the site now calls them 32 and 40
        assertThat(branches.getByPlaceCodeAndActiveTrue("CAMBIOS_CHACO"))
                .filteredOn(b -> List.of("32", "40").contains(b.getRemoteCode()))
                .extracting(PlaceBranch::getId, PlaceBranch::getRemoteCode, PlaceBranch::getName)
                .containsExactlyInAnyOrder(
                        tuple(103L, "32", "Ciudad del Este - Km7"),
                        tuple(105L, "40", "Ciudad del Este - Curupayty"));
        assertThat(chacoBranches("b.remote_code IN ('12', '14-old')")).isEmpty();
    }

    @Test
    void cambiosChacoCurupaytyKeepsItsLocation() {
        // the site doesn't publish the location of 40, the one of the old row is kept
        assertThat(jdbc.queryForMap("SELECT latitude, longitude FROM place_branch WHERE id = 105"))
                .isEqualTo(Map.of("latitude", -25.5139662717053177, "longitude", -54.612792916602757));
    }

    @Test
    void cambiosChacoReusedIdsAreNewBranchesAndTheOldOneKeepsItsRow() {
        assertThat(branches.getByPlaceCodeAndActiveTrue("CAMBIOS_CHACO"))
                .filteredOn(b -> List.of("14", "16", "41").contains(b.getRemoteCode()))
                .extracting(PlaceBranch::getRemoteCode, PlaceBranch::getName)
                .containsExactlyInAnyOrder(
                        tuple("14", "Ag. Plaza Madero"),
                        tuple("16", "Hernandarias - Super Carretera"),
                        tuple("41", "Minga Guazu - Abasto Este"));
        assertThat(chacoBranches("b.remote_code = '14'"))
                .extracting(r -> r.get("id"))
                .singleElement()
                .isNotIn(103L, 105L, 115L);

        // the history of Monseñor Rodriguez stays in its original row
        assertThat(chacoBranches("NOT b.active AND b.remote_code LIKE '%-old'"))
                .containsExactly(Map.of("id", 115L, "remote_code", "32-old", "name", "Monseñor Rodriguez - CDE"));
    }

    @Test
    void cambiosChacoBranchesNotInTheSiteAreDeactivatedButKeepTheirRows() {
        assertThat(chacoBranches("NOT b.active AND b.remote_code NOT LIKE '%-old'"))
                .containsExactlyInAnyOrder(
                        Map.of("id", 101L, "remote_code", "10", "name", "Supercarretera KM 4 - CDE"),
                        Map.of("id", 106L, "remote_code", "15", "name", "Toledo - CDE"),
                        Map.of("id", 111L, "remote_code", "19", "name", "EBY  Circuito Comercial - ENC"),
                        Map.of("id", 107L, "remote_code", "20", "name", "Hernandarias"));
    }

    @Test
    void chacoAlberdiMigrationCanRunAgain() throws IOException {
        List<Map<String, Object>> before = jdbc.queryForList("SELECT * FROM place_branch ORDER BY id");

        jdbc.execute(new ClassPathResource("db/migration/V11__Chaco_alberdi_branches.sql").getContentAsString(StandardCharsets.UTF_8));

        assertThat(jdbc.queryForList("SELECT * FROM place_branch ORDER BY id")).isEqualTo(before);
    }

    @Test
    void alberdiSaltoDelGuairaIsDeactivated() {
        assertThat(activeRemoteCodes("ALBERDI_2"))
                .containsExactlyInAnyOrder("villamorra", "ciudaddeleste", "km4", "encarnacion");
        assertThat(jdbc.queryForObject(
                "SELECT b.active FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'ALBERDI_2' AND b.remote_code = 'saltodelguaira'",
                Boolean.class)).isFalse();
    }

    @Test
    void everyBranchHasARemoteCode() {
        assertThat(jdbc.queryForObject("SELECT count(*) FROM place_branch WHERE remote_code IS NULL", Long.class))
                .isZero();
    }

    @Test
    void legacyAlberdiPlaceHasNoActiveBranches() {
        assertThat(activeRemoteCodes("ALBERDI")).isEmpty();
        assertThat(activeRemoteCodes("ALBERDI_2")).isNotEmpty();
    }

    @Test
    void upsertBranchAddsANewBranch() {
        jdbc.execute("SELECT upsert_branch(place_code => 'MUNDIAL', remote_code => 'new', name => 'Nueva', latitude => -25.1, longitude => -57.1)");

        assertThat(activeRemoteCodes("MUNDIAL")).contains("new");
    }

    @Test
    void upsertBranchUpdatesAnExistingBranchWithoutDuplicatingIt() {
        int before = branches.getByPlaceCodeAndActiveTrue("MERCOSUR").size();

        jdbc.execute("SELECT upsert_branch(place_code => 'MERCOSUR', remote_code => '2', name => 'Gran Vía KM4 (renombrada)')");

        assertThat(activeRemoteCodes("MERCOSUR")).hasSize(before);
        assertThat(jdbc.queryForObject(
                "SELECT b.name FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'MERCOSUR' AND b.remote_code = '2'",
                String.class)).isEqualTo("Gran Vía KM4 (renombrada)");
    }

    @Test
    void deactivateBranchHidesItButKeepsTheRow() {
        jdbc.execute("SELECT deactivate_branch('MERCOSUR', '5')");

        assertThat(activeRemoteCodes("MERCOSUR")).containsExactlyInAnyOrder("1", "2");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'MERCOSUR'",
                Long.class)).isEqualTo(3);
    }

    @Test
    void upsertBranchReactivatesADeactivatedBranch() {
        jdbc.execute("SELECT deactivate_branch('MERCOSUR', '5')");
        jdbc.execute("SELECT upsert_branch(place_code => 'MERCOSUR', remote_code => '5', name => 'San Alberto')");

        assertThat(activeRemoteCodes("MERCOSUR")).contains("5");
    }

    @Test
    void deactivatingAnUnknownBranchFails() {
        assertThatThrownBy(() -> jdbc.execute("SELECT deactivate_branch('MERCOSUR', 'does-not-exist')"))
                .hasMessageContaining("does-not-exist");
    }

    @Test
    void upsertPlaceCreatesAPlaceThatCanReceiveBranches() {
        jdbc.execute("SELECT upsert_place(code => 'NUEVA', name => 'Nueva Casa', type => 'BUREAU')");
        jdbc.execute("SELECT upsert_branch(place_code => 'NUEVA', remote_code => '1', name => 'Central')");

        assertThat(activeRemoteCodes("NUEVA")).containsExactly("1");
    }

    @Test
    void theGatherersOnlyReceiveActiveBranches() {
        jdbc.execute("SELECT deactivate_branch('MERCOSUR', '5')");

        assertThat(places.findByCode("MERCOSUR").orElseThrow().getBranches())
                .extracting(PlaceBranch::getRemoteCode)
                .containsExactlyInAnyOrder("1", "2");
    }

    private List<Map<String, Object>> chacoBranches(String condition) {
        return jdbc.queryForList(
                "SELECT b.id, b.remote_code, b.name FROM place_branch b JOIN place p ON p.id = b.place_id WHERE p.code = 'CAMBIOS_CHACO' AND "
                        + condition);
    }

    private List<String> activeRemoteCodes(String placeCode) {
        return branches.getByPlaceCodeAndActiveTrue(placeCode).stream().map(PlaceBranch::getRemoteCode).toList();
    }
}
