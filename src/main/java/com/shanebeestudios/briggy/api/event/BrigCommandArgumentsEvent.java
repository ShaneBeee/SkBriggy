package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;

public class BrigCommandArgumentsEvent extends BrigCommandEvent {

    public BrigCommandArgumentsEvent(BrigCommand brigCommand) {
        super(brigCommand, null);
    }

}
