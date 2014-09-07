package org.evilco.bot.powersweeper.game.tile.parser;

import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.generic.BombTile;
import org.evilco.bot.powersweeper.game.tile.generic.FlaggedTile;
import org.evilco.bot.powersweeper.game.tile.generic.NumberTile;
import org.evilco.bot.powersweeper.game.tile.generic.UntouchedTile;

/**
 * Created by Nick on 9/7/2014.
 */
public class TileCounter {

    public static int getCount(ITile[] array, TileType type) {
        int count = 0;
        switch (type) {
            case BOMB:
                for (ITile i : array) {
                    if (i != null && i instanceof BombTile) count++;
                }
                break;
            case BLANK:
                for (ITile i : array) {
                    if (i != null && i instanceof UntouchedTile) count++;
                }
                break;
            case NUMBER:
                for (ITile i : array) {
                    if (i != null && i instanceof NumberTile) count++;
                }
                break;
            case FLAG:
                for (ITile i : array) {
                    if (i != null && i instanceof FlaggedTile) count++;
                }
                break;
        }
        return count;
    }

    public static enum TileType {
        BOMB,
        BLANK,
        NUMBER,
        FLAG
    }

}
