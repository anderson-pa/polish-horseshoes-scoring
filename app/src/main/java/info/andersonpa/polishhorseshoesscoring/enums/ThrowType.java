package info.andersonpa.polishhorseshoesscoring.enums;

public final class ThrowType {
    public static final int BOTTLE = 0;
    public static final int CUP = 1;
    public static final int POLE = 2;
    public static final int STRIKE = 3;
    public static final int STANDARD_OUTER = 4;
    public static final int STANDARD_INNER = 5;
    public static final int BALL_HIGH = 6;
    public static final int BALL_HIGH_RIGHT = 7;
    public static final int BALL_RIGHT = 8;
    public static final int BALL_LOW_RIGHT = 9;
    public static final int BALL_LOW = 10;
    public static final int BALL_LOW_LEFT = 11;
    public static final int BALL_LEFT = 12;
    public static final int BALL_HIGH_LEFT = 13;
    public static final int SHORT = 14;
    public static final int TRAP = 15;
    public static final int TRAP_REDEEMED = 16;
    public static final int NOT_THROWN = 17;
    public static final int FIRED_ON = 18;

    public static final String[] typeString = {"Bottle", "Cup", "Pole", "Strike", "Outer Standard",
            "Inner Standard", "High", "High Right", "Right", "Low Right", "Low", "Low Left", "Left",
            "High Left", "Short", "Trap", "Redeemed Trap", "Not thrown", "Fired on"};

    public static boolean isBall(int i) {
        boolean b = (i == BALL_HIGH || i == BALL_HIGH_RIGHT || i == BALL_RIGHT || i == BALL_LOW_RIGHT ||
                i == BALL_LOW || i == BALL_LOW_LEFT || i == BALL_LEFT || i == BALL_HIGH_LEFT);
        return b;
    }

    public static boolean isStackHit(int i) {
        boolean b = (i == BOTTLE || i == CUP || i == POLE);
        return b;
    }

    public static String getString(int throwType) {
        return typeString[throwType];
    }
}
