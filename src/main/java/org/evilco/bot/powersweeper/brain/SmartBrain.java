package org.evilco.bot.powersweeper.brain;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.IChunk;
import org.evilco.bot.powersweeper.game.IGameInterface;

/**
 * Created by Nick on 8/19/2014.
 */
public class SmartBrain implements IBrain {


    /**
     * Stores the internal logger instnace.
     */
    @Getter(AccessLevel.PROTECTED)
    private static final Logger logger = LogManager.getLogger(SmartBrain.class);

    @Override
    public void think(IChunk chunk, IGameInterface gameInterface) {

    }
}
