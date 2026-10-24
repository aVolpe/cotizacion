package py.com.volpe.cotizacion.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import py.com.volpe.cotizacion.EmbeddedPostgresConfiguration;
import py.com.volpe.cotizacion.domain.Place;
import py.com.volpe.cotizacion.domain.PlaceBranch;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private List<String> activeRemoteCodes(String placeCode) {
        return branches.getByPlaceCodeAndActiveTrue(placeCode).stream().map(PlaceBranch::getRemoteCode).toList();
    }
}
