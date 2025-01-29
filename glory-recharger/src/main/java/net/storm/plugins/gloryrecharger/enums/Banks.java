package net.storm.plugins.gloryrecharger.enums;

import lombok.Getter;
import net.storm.api.movement.pathfinder.model.BankLocation;


public enum Banks {
    EDGEVILLE_BANK(BankLocation.EDGEVILLE_BANK),
    CRAFTING_GUILD(BankLocation.CRAFTING_GUILD),
    GRAND_EXCHANGE(BankLocation.GRAND_EXCHANGE_BANK),
    FEROX_ENCLAVE_BANK(BankLocation.FEROX_ENCLAVE_BANK);

    @Getter
    private BankLocation bankLocation;

    Banks(BankLocation bankLocation) {
        this.bankLocation = bankLocation;
    }
}
