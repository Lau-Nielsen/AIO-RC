package net.storm.plugins.daeyalt;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

class DaeyaltMinerOverlay extends OverlayPanel
{
    private final SharedContext context;

    public DaeyaltMinerOverlay(SharedContext context)
    {
        this.context = context;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (context != null) {
            {
                panelComponent.setPreferredSize(new Dimension(250,200));
                panelComponent.setBackgroundColor(new Color(0, 60, 60, 50));
            }
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Daeyalt Miner: " + context.getCurrentRunningState())
                        .color(Color.GREEN).preferredSize(new Dimension(300,200))
                        .build());
            }
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time elapsed:")
                        .right(context.trackingUtils.getFormatedTime())
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
                        .right(context.getTrackingUtils().getTotalAmountAndRate(context.getShardsMined()))
                        .build());
            }
        }

        return super.render(graphics);
    }
}
