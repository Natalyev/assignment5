package restaurant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import restaurant.facade.ChainProgress;
import restaurant.subsystem.c.FinalizationRecord;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceReloadTest {

    @TempDir
    Path tempDir;

    @Test
    void facadeContinuesAfterRestartFromPersistedProgress() {
        RestaurantScenario firstRun = new RestaurantScenario(tempDir);

        String id = firstRun.aConfirmedOrderExists("pasta", 2);

        assertEquals(
                ChainProgress.Status.PENDING_B,
                firstRun.facade.getProgress(id).getStatus()
        );

        RestaurantScenario secondRun = new RestaurantScenario(tempDir);
        secondRun.whenTheChainContinues(id);

        assertEquals(
                ChainProgress.Status.COMPLETED,
                secondRun.facade.getProgress(id).getStatus()
        );

        assertEquals(
                FinalizationRecord.Status.COMPLETED,
                secondRun.finalization.find(id).get().getStatus()
        );
    }
}
