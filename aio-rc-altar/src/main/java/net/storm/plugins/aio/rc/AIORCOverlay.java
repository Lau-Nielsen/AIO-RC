package net.storm.plugins.aio.rc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.storm.plugins.aio.rc.enums.RunningState;

class AIORCOverlay extends OverlayPanel
{
    private final Client client;
    private final AIORC plugin;
    private final AIORCConfig config;

    @Inject
    public AIORCOverlay(Client client, AIORC plugin, AIORCConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getStateMachine() != null) {
            SharedContext context = plugin.getStateMachine().getContext();
            if (plugin.getRunningState() == RunningState.RUNNING)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("AIO RC RUNNING")
                        .color(Color.GREEN).preferredSize(new Dimension(300,200))
                        .build());
            }
            else
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
                            .right(config.runes().name())
                            .build());
                }
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
        }

        return super.render(graphics);
    }
}
