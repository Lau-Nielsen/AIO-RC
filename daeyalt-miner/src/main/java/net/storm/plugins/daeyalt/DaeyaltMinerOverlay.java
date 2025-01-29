package net.storm.plugins.daeyalt;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.storm.plugins.daeyalt.enums.RunningState;

import java.awt.*;
import java.text.DecimalFormat;

class DaeyaltMinerOverlay extends OverlayPanel
{
    private final SharedContext context;

    public DaeyaltMinerOverlay(SharedContext context)
    {
        this.context = context;
    }

    DecimalFormat df = new DecimalFormat("#.00k");

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (context != null) {;
            if (context.getCurrentRunningState() == RunningState.RUNNING) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Daeyalt Miner RUNNING")
                        .color(Color.GREEN).preferredSize(new Dimension(300,200))
                        .build());
            } else
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Daeyalt Miner PAUSED")
                        .color(Color.YELLOW)
                        .build());
            }

            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time elapsed:")
                        .right(context.formatTime())
                        .build());
            }

            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("State:")
                        .right(context.getCurrentState())
                        .build());
            }

            {
                panelComponent.getChildren().add(LineComponent.builder().build());
            }

            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Shards mined:")
                        .right((context.getShardsMined() > 10000 ? df.format((double) context.getShardsMined() / 1000) : context.getShardsMined()) +  " | " + context.calculateRatePerHour(context.getShardsMined()) +"/hr")
                        .build());
            }
        }

        return super.render(graphics);
    }
}
