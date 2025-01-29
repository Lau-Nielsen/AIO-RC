package net.storm.plugins.gloryrecharger;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.storm.plugins.gloryrecharger.enums.RunningState;

import java.awt.*;
import java.text.DecimalFormat;

class GloryRechargerOverlay extends OverlayPanel
{
    private final SharedContext context;

    public GloryRechargerOverlay(SharedContext context)
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
                        .text("Glory Charger RUNNING")
                        .color(Color.GREEN).preferredSize(new Dimension(300,200))
                        .build());
            } else
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Glory Charger PAUSED")
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
                        .left("Glories charged:")
                        .right((context.getGloriesCharged() > 10000 ? df.format((double) context.getGloriesCharged() / 1000) : context.getGloriesCharged()) +  " | " + context.calculateRatePerHour(context.getGloriesCharged()) +"/hr")
                        .build());
            }
        }

        return super.render(graphics);
    }
}
