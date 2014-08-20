package org.evilco.bot.powersweeper.brain;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.IGameInterface;
import org.evilco.bot.powersweeper.game.MatrixChunk;
import org.evilco.bot.powersweeper.game.ScreenGameInterface;
import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.generic.BombTile;
import org.evilco.bot.powersweeper.game.tile.generic.FlaggedTile;
import org.evilco.bot.powersweeper.game.tile.generic.NumberTile;
import org.evilco.bot.powersweeper.game.tile.generic.UntouchedTile;

/**
 * Created by Nick on 8/19/2014.
 * <p>
 * Start simple. Look at 1s, try to flag and open the rest of the board.
 * <p>
 * Working progressively, SmartBot does not need to clear an entire chunk --
 * it will only work to what it can do (no guesses) and flags bombs it knows it can,
 * while clearing spaces after the fact.
 */
public class SmartBrain implements IBrain {

    /**
     * Stores the internal logger instance.
     */
    @Getter(AccessLevel.PROTECTED)
    private static final Logger logger = LogManager.getLogger(SmartBrain.class);

    @Override
    public void think(IGameInterface gameInterface) {
        /**
         * So following my humane method of playing minesweeper, I first want to get any
         * blank tiles cleared that wouldn't have any bombs associated with them.
         * This means looking to see if there's a number tile that has that many bombs
         * (or flags) around it, as WELL as having some blank tiles around it.
         */
        MatrixChunk chunk = (MatrixChunk) gameInterface.getChunk();
        for (short s = 0; s < chunk.getWidth(); s++) {
            for (short s1 = 0; s1 < chunk.getHeight(); s1++) {
                ITile tile = chunk.getTile(s, s1);

                if (tile instanceof NumberTile) {
                    NumberTile nt = (NumberTile) tile;
                    short value = nt.getValue();
                    ITile[] neighbors = tile.getLocation().getNeighbors();
                    if (neighbors.length < 6) continue;
                    int bombCount = 0;
                    bombCount += getCount(neighbors, TileType.BOMB);
                    bombCount += getCount(neighbors, TileType.FLAG);

                    if (bombCount == value) {
                        if (getCount(neighbors, TileType.BLANK) > 0) {
                            gameInterface.touchTile(tile.getLocation());
                            return;
                        }
                    } else {
                        if (value == 1 && getCount(neighbors, TileType.BLANK) == 1) {
                            gameInterface.flagTile(tile.getLocation());
                            return;
                        }
                        if (bombCount == (value - 1) && getCount(neighbors, TileType.BLANK) == 1) {
                            gameInterface.flagTile(tile.getLocation());
                            return;
                        }
                    }
                }
            }
        }
        if ((chunk).isBlank()) {
            //pick a random chunk and click
            ((ScreenGameInterface)gameInterface).touchRandomTile();
            return;
        }
        gameInterface.moveToChunk(chunk.getLocation().getRelative(1, 0));

    }


    public int getCount(ITile[] array, TileType type) {
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

    enum TileType {
        BOMB,
        BLANK,
        NUMBER,
        FLAG
    }
}
