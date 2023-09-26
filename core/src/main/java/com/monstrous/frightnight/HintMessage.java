package com.monstrous.frightnight;

// enumerates hint messages with associated text and voice line


public enum HintMessage     {

    FRIGHT (Sounds.VOICE_BRAVE_KNIGHT, "Oh brave knight, what a fright\nHow will you survive this night?"),
    HELL_HOUND(Sounds.VOICE_HOUNDS,"The hounds of hell may lead to your untimely end\nBut could also prove to be a man's best friend"),
    UNDEAD (Sounds.VOICE_UNDEAD, "The undead wander lonely in the night\nLet them too close and you`ll have a fright"),
    NO_CREATURE (Sounds.VOICE_NO_CREATURE, "No creature may survive this haunted night\nOnly you, oh righteous knight"),
    CAR (Sounds.VOICE_CAR, "A hulk of metal screeches past\nEvery loop the same as the last"),
    VOID (Sounds.VOICE_VOID, "Though the idea must have been enjoyed\nThere is no escape to the empty void"),
    CARRIAGE (Sounds.VOICE_CARRIAGE, "The nightmare abates\nYour carriage awaits"),
    GLORY (Sounds.VOICE_GLORY, "Glory be the righteous knight\nLone survivor of the night of fright\nHe lives to see another day\nNow it`s time to go away"),
    WEATHER (Sounds.VOICE_WEATHER, "  For reasons beyond comprehension\nThe weather is removed from the tension"),
    F1_FOR_HELP (Sounds.MENU_CLICK, "Press H for hints"),
    HINT_1 (Sounds.MENU_CLICK, "Though you have no weapons, you need to eliminate all the creatures"),
    HINT_2 (Sounds.MENU_CLICK, "There are no weapons to be found"),
    HINT_3 (Sounds.MENU_CLICK, "The enemies of your enemies are your friends"),
    HINT_4 (Sounds.MENU_CLICK, "You can get the hell hounds to follow you"),
    HINT_5 (Sounds.MENU_CLICK, "The hell hounds will attack the zombies if you lure them close by"),
    HINT_6 (Sounds.MENU_CLICK, "Hell hounds should be more careful crossing the road"),
    HINT_7 (Sounds.MENU_CLICK, "You can escape only when all zombies and hell hounds are dead"),
    QUICKSAVE (Sounds.MENU_CLICK, "Quick Save"),
    QUICKLOAD (Sounds.MENU_CLICK, "Quick Load");

    public String text;
    public int soundId;

    HintMessage(int soundId, String txt){
        text = txt;
        this.soundId = soundId;
    }
}

//    Oh brave knight, what a fright
//    How will you survive this night
//
//    The hounds of hell may cause your untimely end
//    But could also prove to be a mans best friend
//
//    The undead wander lonely in the night
//    Let them too close and you’ll have a fright
//
//    No creature may survive this haunted night
//    Only you, oh righteous knight
//
//    Now we see the master of this feast
//    Who’ll rid us from this trouble some priest
//
//    A hulk of metal screeches past
//    Every loop the same as the last
//
//    Though t’ idea must have been enjoyed
//    There is no escape to the empty void
//
//    Hide in the corn if you must
//    The depths of the refuge you can not trust
//
//    Seek refuge in e field of corn
//    Go to far and ye’ll not return
//
//    The nightmare abates
//    Your carriage awaits
//
//    Glory be the righteous knight
//    Lone survivor of the night of fright
//    He lives to see another day
//    Now it it’s time to go away
