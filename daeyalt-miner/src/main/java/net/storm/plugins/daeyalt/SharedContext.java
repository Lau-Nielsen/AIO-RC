package net.storm.plugins.daeyalt;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldArea;
import net.storm.plugins.commons.enums.RunningState;
import net.storm.plugins.commons.utils.TrackingUtils;

import javax.sound.midi.Track;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Singleton
public class SharedContext {
    private Integer shardsMined = 0; // done
    private String currentState; // done;
    private RunningState currentRunningState = RunningState.AWAITING_START; // done;
    private Map<String, List<WorldArea>> tickManipPoints = new HashMap<>();

    private long startTime;
    private long totalElapsedTime = 0;
    private boolean isTimeTracking = false;

    @Getter
    TrackingUtils trackingUtils = new TrackingUtils();


    @Getter
    private DaeyaltMinerConfig config;

    public SharedContext (DaeyaltMinerConfig config){ this.config = config;}

    public void initTickManipMap() {
        tickManipPoints.put("east", Arrays.asList(new WorldArea(3686,9757,1,1,2), new WorldArea(3686,9756,1,1,2)));
        tickManipPoints.put("south-east", Arrays.asList(new WorldArea(3674,9750,1,1,2), new WorldArea(3674,9751,1,1,2)));
        tickManipPoints.put("south-north", Arrays.asList(new WorldArea(3671,9753,1,1,2), new WorldArea(3672,9753,1,1,2)));
        tickManipPoints.put("north", Arrays.asList(new WorldArea(3674,9764,1,1,2), new WorldArea(3675,9764,1,1,2)));
    }


}