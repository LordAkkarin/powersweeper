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
        NumberTile[] numberTiles = chunk.getNumberTiles();

        if (numberTiles.length > 50) {
            for (NumberTile nt : numberTiles) {
                if (handleNumberTile(gameInterface, nt)) {
                    return;
                }
            }
        } else {
            if (numberTiles.length > 0) {
                for (NumberTile nt : numberTiles) {
                    if (!handleNumberTile(gameInterface, nt)) {//this is expected to not work, but cool if it does
                        ITile[] neighbors = nt.getLocation().getNeighbors();
                        if (neighbors.length > 5) {
                            if (getCount(neighbors, TileType.BLANK) > nt.getValue()) {
                                gameInterface.touchTile(nt.getLocation().getBlankNeighbor().getLocation());
                                return;
                            }
                        }
                    } else {
                        return;
                    }
                }
            } else {
                ((ScreenGameInterface)gameInterface).touchRandomTile();
                return;
            }
        }
        if (chunk.isBlank()) {
            //pick a random chunk and click
            ((ScreenGameInterface)gameInterface).touchRandomTile();
            return;
        }
        gameInterface.moveToChunk(chunk.getLocation().getRelative(1, 0));

    }

    public boolean handleNumberTile(IGameInterface gameInterface, NumberTile nt) {
        short value = nt.getValue();
        ITile[] neighbors = nt.getLocation().getNeighbors();
        if (neighbors.length < 6) return false;
        int bombCount = 0;
        bombCount += getCount(neighbors, TileType.BOMB);
        bombCount += getCount(neighbors, TileType.FLAG);
        int blankCount = getCount(neighbors, TileType.BLANK);

        if (bombCount == value) {//there's that many bombs around it
            if (blankCount > 0) {
                gameInterface.touchTile(nt.getLocation());
                return true;
            }
        } else {//there's still some bombs, let's see if we can flag
            if (value == 1 && blankCount == 1) {//easy corner picking
                gameInterface.flagTile(nt.getLocation());
                return true;
            }
            if (bombCount == (value - 1) && blankCount == 1) {//one blank left, has to be the bomb
                gameInterface.flagTile(nt.getLocation());
                return true;
            }
            if (blankCount > 0 && (bombCount + blankCount == value)) {//bombs and blanks add up to the number
                gameInterface.flagTile(nt.getLocation());
                return true;
            }
        }
        return false;
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
