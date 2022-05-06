package com.example.myminesweeper;


public final class DefMinefieldSettings {
    public static final int SPACING =  1;

    public static final String LEVEL_EASY =  "Easy";
    public static final String LEVEL_MEDIUM =  "Medium";
    public static final String LEVEL_HARD =  "Hard";
    public static final String LEVEL_CUSTOM =  "Custom";
    public static final String LEVEL_DEFAULT =  LEVEL_EASY;

    public static final class Easy{
        public static final int WIDTH =  8;
        public static final int HEIGHT =  13;
        public static final int MINES_PERCENT =  17;
    }

    public static final class Medium{
        public static final int WIDTH = 12;
        public static final int HEIGHT =  18;
        public static final int MINES_PERCENT =  17;
    }

    public static final class Hard{
        public static final int WIDTH =  16;
        public static final int HEIGHT =  24;
        public static final int MINES_PERCENT =  17;
    }

    public static final class Default{
        public static final int WIDTH =  8;
        public static final int HEIGHT =  13;
        public static final int MINES_PERCENT =  17;
    }
}
