package org.company.spacedrepetitionbot.constants;

import lombok.Getter;

@Getter
public enum Quality {
    AGAIN(0),
    HARD(3),
    GOOD(4),
    EASY(5);

    private final int quality;

    Quality(int quality) {
        this.quality = quality;
    }
}
