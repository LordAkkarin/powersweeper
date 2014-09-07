package org.evilco.bot.powersweeper.brain;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.IGameInterface;
import org.evilco.bot.powersweeper.game.MatrixChunk;
import org.evilco.bot.powersweeper.game.ScreenGameInterface;
import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.generic.NumberTile;
import org.evilco.bot.powersweeper.game.tile.generic.UntouchedTile;
import org.evilco.bot.powersweeper.game.tile.parser.TileCounter;

import java.util.ArrayList;
import java.util.Collections;

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

    ArrayList<NumberTile> toFlag = new ArrayList<>();
    ArrayList<NumberTile> toClear = new ArrayList<>();

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
        int initialSize = gameInterface.getActionQueue().size();
        if (numberTiles.length > 0) {
            if (handleNumberTiles(numberTiles, true)) {
                if (!toFlag.isEmpty()) {
                    sortCollectedTiles(toFlag, true);
                    for (NumberTile nt : toFlag) {
                        gameInterface.flagTile(nt.getLocation());
                    }
                    toFlag.clear();
                }
                if (!toClear.isEmpty()) {
                    sortCollectedTiles(toClear, false);
                    for (NumberTile nt : toClear) {
                        gameInterface.touchTile(nt.getLocation());
                    }
                    toClear.clear();
                }
                return;
            }

            if (initialSize == gameInterface.getActionQueue().size()) {//no growth
                NumberTile nt = chunk.findViableExplorationTile();
                if (nt != null) {
                    gameInterface.touchTile(nt.getLocation().getBlankNeighbor().getLocation());
                    return;
                } else if (chunk.isBlank()) {
                    ((ScreenGameInterface) gameInterface).touchRandomTile(null);
                    return;
                }
            } else {
                return;
            }
        } else {
            ((ScreenGameInterface) gameInterface).touchRandomTile(null);
            return;
        }
        gameInterface.moveToChunk(chunk.getLocation().getRelative(1, 0));

    }

    public boolean handleNumberTiles(NumberTile[] ntarr, boolean flagging) {
        boolean flagged = false;
        boolean marked = false;
        for (NumberTile nt : ntarr) {
            short value = nt.getValue();
            ITile[] neighbors = nt.getLocation().getNeighbors();
            if (neighbors.length < 6) continue;
            int bombCount = 0;
            bombCount += TileCounter.getCount(neighbors, TileCounter.TileType.BOMB);
            bombCount += TileCounter.getCount(neighbors, TileCounter.TileType.FLAG);
            int blankCount = TileCounter.getCount(neighbors, TileCounter.TileType.BLANK);

            if (flagging) {//flagging first
                if (bombCount != value) {
                    if (value == 1 && blankCount == 1) {//easy corner picking
                        toFlag.add(nt);
                        flagged = true;
                    }
                    if (bombCount == (value - 1) && blankCount == 1) {//one blank left, has to be the bomb
                        toFlag.add(nt);
                        flagged = true;
                    }
                    if (blankCount > 0 && (bombCount + blankCount == value)) {//bombs and blanks add up to the number
                        toFlag.add(nt);
                        flagged = true;
                    }
                }
            } else {
                if (bombCount == value) {
                    if (blankCount > 0) {
                        toClear.add(nt);
                        marked = true;
                    }
                }
            }
        }
        if (flagging) {
            return flagged || handleNumberTiles(ntarr, false);
        } else {
            return marked;
        }
    }


    //This method will go through and compare every ITile to see if they don't overlap.
    public void sortCollectedTiles(ArrayList<NumberTile> list, boolean flags) {
        ArrayList<NumberTile> toRemove = new ArrayList<>();
        if (!list.isEmpty()) {
            Collections.sort(list);
            Collections.reverse(list);
            for (NumberTile nt : list) {
                if (toRemove.contains(nt)) continue;
                ITile[] neighbors = nt.getLocation().getNeighbors();
                NumberTile[] possibleMatches = arrayContains(neighbors, list, flags, nt);
                if (possibleMatches.length > 1) {
                    Collections.addAll(toRemove, possibleMatches);
                }
            }
            if (!toRemove.isEmpty()) {
                for (NumberTile nt : toRemove) {
                    list.remove(nt);
                }
            }
        }
        toRemove.clear();
    }

    //checks to see if the list contains any of the neighbors in the array
    private NumberTile[] arrayContains(ITile[] array, ArrayList<NumberTile> list, boolean flags, NumberTile parent) {
        ArrayList<NumberTile> toReturn = new ArrayList<>();
        for (ITile t : array) {
            if (parent != null && parent.equals(t)) continue;
            if (flags) {
               if (t instanceof UntouchedTile) {
                  Collections.addAll(toReturn, arrayContains(t.getLocation().getNeighbors(), list, false, parent));
               }
            } else {
                for (NumberTile nt : list) {
                    if (nt.equals(t)) toReturn.add((NumberTile)t);
                }
            }
        }
        return toReturn.toArray(new NumberTile[toReturn.size()]);
    }
}
