package net.storm.plugins.daeyalt;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldArea;
import net.storm.plugins.daeyalt.enums.RunningState;

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
    private DaeyaltMinerConfig config;

    public SharedContext (DaeyaltMinerConfig config){ this.config = config;}

    public void start() {
        if (!isTimeTracking) {
            this.startTime = System.currentTimeMillis();
            this.isTimeTracking = true;
        }
    }

    public void pause() {
        if (isTimeTracking) {
            this.totalElapsedTime += System.currentTimeMillis() - startTime;
            this.isTimeTracking = false;
        }
    }

    public long getElapsedTimeSeconds() {
        if (isTimeTracking) {
            return (totalElapsedTime + (System.currentTimeMillis() - startTime)) / 1000;
        } else {
            return totalElapsedTime / 1000;
        }
    }

    public String formatTime() {
        long totalTime = this.getElapsedTimeSeconds();

        long hours = totalTime / 3600;
        long minutes = (totalTime % 3600) / 60;
        long seconds = (totalTime % 60);

        // Format as HH:MM:SS.mmm with leading zeros
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String calculateRatePerHour(long amount) {
        double elapsedTimeHours = (double) getElapsedTimeSeconds() / 3600;

        if (elapsedTimeHours == 0) {
            return "0k";
        }

        double rate = (amount / elapsedTimeHours) / 1000;

        DecimalFormat df = new DecimalFormat("#.00k");

        return df.format(rate);
    }

    public void initTickManipMap() {
        tickManipPoints.put("east", Arrays.asList(new WorldArea(3686,9757,1,1,2), new WorldArea(3686,9756,1,1,2)));
        tickManipPoints.put("south-east", Arrays.asList(new WorldArea(3674,9750,1,1,2), new WorldArea(3674,9751,1,1,2)));
        tickManipPoints.put("south-north", Arrays.asList(new WorldArea(3671,9753,1,1,2), new WorldArea(3672,9753,1,1,2)));
        tickManipPoints.put("north", Arrays.asList(new WorldArea(3674,9764,1,1,2), new WorldArea(3675,9764,1,1,2)));
    }


}