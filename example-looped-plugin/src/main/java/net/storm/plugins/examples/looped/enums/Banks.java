package net.storm.plugins.examples.looped.enums;

import lombok.Getter;
import net.storm.api.movement.pathfinder.model.BankLocation;

public enum Banks {
    EDGEVILLE_BANK(BankLocation.EDGEVILLE_BANK),
    CASTLE_WARS_BANK(BankLocation.CASTLE_WARS_BANK),
    CRAFTING_GUILD(BankLocation.CRAFTING_GUILD),
    FARMING_GUILD_SOUTH(BankLocation.FARMING_GUILD_SOUTH),
    HUNTER_GUILD(BankLocation.HUNTER_GUILD),
    FEROX_ENCLAVE_BANK(BankLocation.FEROX_ENCLAVE_BANK),
    SEERS_VILLAGE_BANK(BankLocation.SEERS_VILLAGE_BANK),
    MYTHS_BUILD(BankLocation.MYTHS_GUILD);

    @Getter
    private BankLocation bankLocation;

    Banks(BankLocation bankLocation) {
        this.bankLocation = bankLocation;
    }
}
