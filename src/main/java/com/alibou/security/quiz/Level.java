package com.alibou.security.quiz;

public enum Level {
    Easy,
    Medium,
    Hard,
    Mixed,
    Default;

    public static int toInt(Level level) {
        return switch (level) {
            case Easy -> 1;
            case Medium -> 3;
            case Hard -> 5;
            case Mixed -> 1;
            default -> 0;
        };
    }
}
