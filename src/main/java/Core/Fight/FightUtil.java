package Core.Fight;

public class FightUtil {

    public static double calcMult(int lvl) {
        // mult = ((x^1.35) / 10) + 1
        return (Math.pow(lvl, 1.35) / 10) + 1;
    }

    public static int calcEXPForLevel(int lvl) {
        lvl--; // Calculate level from zero

        // 5x^2 + 50x + 100
        return 5 * lvl * lvl + 50 * lvl + 100;
    }
}
