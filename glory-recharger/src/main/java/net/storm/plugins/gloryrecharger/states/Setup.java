package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.storm.api.magic.SpellBook;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.Banks;
import net.storm.plugins.gloryrecharger.enums.FountainTransportation;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.game.Skills;
import net.storm.sdk.game.Vars;
import net.storm.sdk.quests.Quests;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;

public class Setup implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;
    private boolean forceAddressErrors = false;

    public Setup(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Override
    public void handleState(StateMachine stateMachine) {

        if (config.fountainTransport() == FountainTransportation.ANNAKARL_TP && SpellBook.getCurrent() != SpellBook.ANCIENT) {
            MessageUtils.addMessage("You need to be on Ancients to cast Annakarl TP...", Color.RED);
            this.forceAddressErrors = true;
        }

        if (config.fountainTransport() == FountainTransportation.ANNAKARL_TP && Skills.getLevel(Skill.MAGIC) < 90) {
            MessageUtils.addMessage("You need a magic level of 90 or above to cast Annakarl TP...", Color.RED);
            this.forceAddressErrors = true;
        }

        if (config.fountainTransport() == FountainTransportation.WILDERNESS_SWORD && Vars.getBit(Varbits.DIARY_WILDERNESS_HARD) != 1) {
            MessageUtils.addMessage("You shouldn't be using the wilderness sword without the wildy elite diary...", Color.RED);
            this.forceAddressErrors = true;
        }

        if (config.bank() == Banks.CRAFTING_GUILD && Skills.getLevel(Skill.CRAFTING) < 99) {
            MessageUtils.addMessage("Don't bank at the crafting guild without the crafting cape...", Color.RED);
            this.forceAddressErrors = true;
        }

        if (!Quests.isFinished(Quest.HEROES_QUEST)) {
            MessageUtils.addMessage("You have to have heroes guild completed to rechagre glories...", Color.RED);
            this.forceAddressErrors = true;
        }

        if (!forceAddressErrors) {
            stateMachine.setState(new Banking(context), false);
        }
    }

    @Override
    public States getStateName() {
        if (forceAddressErrors) {
            return States.ForceAwaitErrors;
        } else {
            return States.Setup;
        }
    }
}