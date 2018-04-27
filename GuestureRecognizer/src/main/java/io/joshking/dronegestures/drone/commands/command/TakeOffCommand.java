package io.joshking.dronegestures.drone.commands.command;

import io.joshking.dronegestures.drone.commands.BaseCommand;

public class TakeOffCommand extends BaseCommand {
    @Override
    public Class getTypeClass() {
        return getClass();
    }
}
