package com.wylxbot.wylx.Commands.Fight.Util;

public class FightMessages {
    /*
     * {p1} Attacker
     * {p2} Victim
     * {r} Random number
     */

    public static String[] attackMessages = {
        // Below responses by Micheal from 4150
        "{p2} was hit on the head by {p1}",
        "{p2} was kicked by {p1}",
        "{p2} was slammed into a wall by {p1}",
        "{p2} was drop kicked by {p1}",
        "{p2} was DDoSed by {p1}",
        "{p2} was choke slammed by {p1}",
        "{p2} was run over with a robot by {p1}",
        "{p2} had their IQ dropped 15 points by {p1}",
        "{p2} had a heavy object dropped on them by {p1}",
        "{p2} was beat up by {p1}",
        // Below responses from Dragonite
        "{p2} was swept off their feet by {p1}",
        "{p2} was hit by a hammer by {p1}",
        "{p2} was given a G20 violation by {p1}",
        "{p2} had their dexterity dropped 2 by {p1}",
        // Responses below from 5468 (Chaos Theory)
        "{p2} was stabbed by a screwdriver by {p1}",
        "{p2} had Endgame spoiled for them by {p1}",
        "{p2} had GOT spoiled for them by {p1}",
        "{p2} was given a RED card by {p1}",
        "{p2} was given a YELLOW card by {p1}",
        "{p2} was called out for not wearing safety glasses in the pits by {p1}",
        "{p2} was forced to waste their time on a failing robot design for most of build season by {p1}",
        "{p2} was told it was programming's fault at least 76 times by {p1}",
        // Responses below by Ben from 2976
        "{p2} was bopped on the nose by {p1}",
        // Responses below by wither (2898)
        "{p2} was forced to re-tune their PID loop by {p1}",
        "{p2} had their eyes burnt out by {p1}'s LED ring",
        // Responses below by Macro (2898)
        "{p2} was forced to re-machine {r} parts by {p1}",
        "{p1} broke {p2}'s SD card",
        // Responses below by WaterGame2023
        "{p2} was told to use Windows by {p1}",
        "{p2} was forced to update Windows by {p1}",
        "{p1} used rm -rf on {p2}",
        "{p2} got knocked off the Hab by {p1}",
        "{p2} lost connection to the field courtesy of {p1}",
        "{p2} was forced to use C++ for their robot code by {p1}",
        "{p2} was told to touch grass by {p1}",
        "{p2} DDoSed {p1}",
        "{p2} got pushed off the hangar by {p1}",
    };

    public static String[] finisherMessages = {

    };

    // User tries attacking everyone
    public static String[] everyoneMessages = {

    };

    // User attacking Wylx (or another Bot)
    public static String[] botMessages = {
        "{p1} enters bullet time and knocks out {p2}",
        "{p1} was hacked and had to be shutdown by {p2}",
    };

    // User fighting themselves
    public static String[] duplicateFighterMessages = {
        "{p1} slaps themselves...it isn't very effective",
        "{p1} creates a duplicate of themselves - the duplicate refuses to fight",
    };
}
