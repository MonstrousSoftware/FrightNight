package com.monstrous.frightnight;

public enum HintMessage     {

    FRIGHT ("Oh brave knight, what a fright\nHow will you survive this night?", 1),
    HELLHOUND("The hounds of hell may cause your untimely end\nBut could also prove to be a man's best friend", 2);

    public String text;
    public int soundId;

    HintMessage(String t, int id){
        text = t;
        soundId = id;
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
