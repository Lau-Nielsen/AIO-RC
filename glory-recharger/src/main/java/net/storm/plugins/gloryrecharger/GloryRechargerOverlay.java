package net.storm.plugins.gloryrecharger;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.storm.plugins.gloryrecharger.enums.FountainTransportation;

import java.awt.*;

class GloryRechargerOverlay extends OverlayPanel
{
    SharedContext context;
    GloryRechargerConfig config;

    public GloryRechargerOverlay(SharedContext context)
    {
        this.context = context;
        this.config = context.getConfig();
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (context != null && config.showOverlay()) {
            {
                panelComponent.setPreferredSize(new Dimension(200,100));
                panelComponent.setBackgroundColor(new Color(11, 40, 184, 30));
            }
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Glory Charger: " + context.getCurrentRunningState())
                        .color(Color.GREEN)
                        .build());
            }
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time elapsed:")
                        .right(context.getTrackingUtils().getFormatedTime())
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
                        .right(context.getTrackingUtils().getTotalAmountAndRate(context.getGloriesCharged()))
                        .build());
            }
            if(config.showStock()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder().build());
                }
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Stock:")
                            .build());
                }
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Glories:")
                            .right(context.getGlories().toString())
                            .build());
                }
                if (config.useStamina()) {
                    {
                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Stamina pots:")
                                .right(context.getStaminas().toString())
                                .build());
                    }
                }
                if (config.fountainTransport() == FountainTransportation.ANNAKARL_TABLET) {
                    {
                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Annakarl tabs:")
                                .right(context.getAnnakarlTabs().toString())
                                .build());
                    }
                }
                if (config.fountainTransport() == FountainTransportation.WILDERNESS_SWORD) {
                    {
                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Wildy swords:")
                                .right(context.getWildySwords().toString())
                                .build());
                    }
                }
                if (config.fountainTransport() == FountainTransportation.ANNAKARL_TP) {
                    {
                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Law runes:")
                                .right(context.getLawRunes().toString())
                                .build());
                    }
                    {
                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Blood runes:")
                                .right(context.getBloodRunes().toString())
                                .build());
                    }
                }
            }

        }

        return super.render(graphics);
    }
}
