package net.storm.plugins.gloryrecharger.enums;

import lombok.Getter;

public enum Obelisk {
    FEROX_NE(5),
    FEROX_SE(4),
    GOD_WARS_DUNGEON(2),
    HUNTER_AREA(3),
    KDB(1),
    ROUGES_CASTE(6);

    @Getter
    int varbitValue;

    Obelisk(int varbitValue) {
        this.varbitValue = varbitValue;
    }
}
