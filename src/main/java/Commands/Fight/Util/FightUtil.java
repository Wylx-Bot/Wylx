package Commands.Fight.Util;

public class FightUtil {

    /**
     * Calculate multiplier for damage, health, etc
     *
     * @param lvl Skill level of modifier
     * @return Multiplier (Returns 1 at lvl 0)
     */
    public static double calcMultiplier(int lvl) {
        // mult = ((x^1.35) / 10) + 1
        return (Math.pow(lvl, 1.35) / 10) + 1;
    }

    /**
     * Calculate EXP needed to reach lvl + 1
     *
     * @param lvl Overall level of user
     * @return EXP needed to reach lvl + 1
     */
    public static int calcEXPForLevel(int lvl) {
        lvl--; // Calculate level from zero

        // 5x^2 + 50x + 100
        return 5 * lvl * lvl + 50 * lvl + 100;
    }
}
