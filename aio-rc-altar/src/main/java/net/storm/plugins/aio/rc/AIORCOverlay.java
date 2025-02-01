package net.storm.plugins.aio.rc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.storm.plugins.commons.enums.RunningState;


class AIORCOverlay extends OverlayPanel
{
    private final SharedContext context;
    private final AIORCConfig config;

    public AIORCOverlay(SharedContext context)
    {
        this.context = context;
        this.config = context.getConfig();
    }

    DecimalFormat df = new DecimalFormat("#.00k");

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (context != null) {;
            if (context.getCurrentRunningState() == RunningState.RUNNING) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("AIO RC RUNNING")
                        .color(Color.GREEN).preferredSize(new Dimension(300,200))
                        .build());
            } else
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("AIO RC PAUSED")
                        .color(Color.YELLOW)
                        .build());
            }

            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time elapsed:")
                        .right(context.formatTime())
                        .build());
            }
            if (!config.isRunner() && context.getCurrentlyCrafting() != null) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Currently crafting:")
                            .right(context.getCurrentlyCrafting().name())
                            .build());
                }
            } else {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Current altar:")
                            .right(config.altar().name())
                            .build());
                }
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
                        .left("Bank stock:")
                        .build());
            }
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Essences:")
                        .right(context.getEssenceInBank().toString())
                        .build());
            }
            if (config.useStamina()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Stamina doses:")
                            .right(context.getStaminaDoses().toString())
                            .build());
                }
            }

            if (config.bringBindingNecklace()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Binding necklace:")
                            .right(context.getBindingNecklacesInBank().toString())
                            .build());
                }
            }

            if (!config.bringBindingNecklace() && context.getTalismanNeededForComboRunes() != null) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Talismans:")
                            .right(context.getTalismansRemaining().toString())
                            .build());
                }
            }

            if (context.isUsingGlories()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Glories:")
                            .right(context.getGloriesInBank().toString())
                            .build());
                }
            }

            if (context.isUsingDuelingRings()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Dueling rings:")
                            .right(context.getDuelingRingsInBank().toString())
                            .build());
                }
            }

            if (context.isUsingRingOfElements()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("RotE charges:")
                            .right(context.getChargesOnRingOfElement().toString())
                            .build());
                }
            }

            {
                panelComponent.getChildren().add(LineComponent.builder().build());
            }

            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Metrics:")
                        .build());
            }
            if (config.isRunner()) {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Essence traded:")
                            .right(context.getEssencesTraded().toString() +  " | " + context.calculateRatePerHour(context.getEssencesTraded()) +"/hr")
                            .build());
                }
            } else {
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Exp gained:")
                            .right((context.getExpGained() > 10000 ? df.format((double) context.getExpGained() / 1000) : context.getExpGained()) +  " | " + context.calculateRatePerHour(context.getExpGained()) +"/hr")
                            .build());
                }
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Runes crafted:")
                            .right((context.getRunesCrafted() > 10000 ? df.format((double) context.getRunesCrafted() / 1000) : context.getRunesCrafted()) +  " | " + context.calculateRatePerHour(context.getRunesCrafted()) +"/hr")
                            .build());
                }
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Est. gp earned:")
                            .right(df.format(context.getEstimatedGpEarned() / 1000) +  " | " + context.calculateRatePerHour((long) context.getEstimatedGpEarned()) +"/hr")
                            .build());
                }
            }
        }

        return super.render(graphics);
    }
}
