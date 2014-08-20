package org.evilco.bot.powersweeper.brain;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.IChunk;
import org.evilco.bot.powersweeper.game.IGameInterface;
import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.generic.BombTile;
import org.evilco.bot.powersweeper.game.tile.generic.FlaggedTile;
import org.evilco.bot.powersweeper.game.tile.generic.NumberTile;
import org.evilco.bot.powersweeper.game.tile.generic.UntouchedTile;

import java.util.HashSet;

/**
 * Created by Nick on 8/19/2014.
 *
 * Start simple. Look at 1s, try to flag and open the rest of the board.
 *
 * Working progressively, SmartBot does not need to clear an entire chunk --
 * it will only work to what it can do (no guesses) and flags bombs it knows it can,
 * while clearing spaces after the fact.
 */
public class SmartBrain implements IBrain {

    static HashSet<Action> actionTiles = new HashSet<>();

    /**
     * Stores the internal logger instance.
     */
    @Getter(AccessLevel.PROTECTED)
    private static final Logger logger = LogManager.getLogger(SmartBrain.class);

    @Override
    public void think(IGameInterface gameInterface) {
        /**
         * Let's use a simple, human-friendly algorithm.
         *
         * First, let's see if there isn't something we've already calculated to do.
         */
        //if (actionTiles.isEmpty()) {
            /**
             * Okay good.
             * So following my humane method of playing minesweeper, I first want to get any
             * blank tiles cleared that wouldn't have any bombs associated with them.
             * This means looking to see if there's a number tile that has that many bombs
             * (or flags) around it, as WELL as having some blank tiles around it.
             */
            IChunk chunk = gameInterface.getChunk();
            for (short s = 0; s < chunk.getWidth(); s++) {
                for (short s1 = 0; s1 < chunk.getHeight(); s1++) {
                    ITile tile = chunk.getTile(s, s1);
                    //TODO start by clearing some space if there isn't any number tiles

                    if (tile instanceof NumberTile) {
                        NumberTile nt = (NumberTile) tile;
                        short value = nt.getValue();
                        ITile[] neighbors = tile.getLocation().getNeighbors();
                        int bombCount = 0;
                        bombCount += getCount(neighbors, TileType.BOMB);
                        bombCount += getCount(neighbors, TileType.FLAG);

                        if (bombCount == value) {
                           if (getCount(neighbors, TileType.BLANK) > 0) {
                              actionTiles.add(new Action(tile, false));
                               gameInterface.touchTile(tile.getLocation());
                               return;
                           }
                        } else {
                            if (value == 1 && getCount(neighbors, TileType.BLANK) == 1) {
                                actionTiles.add(new Action(tile, true));
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
            /*if (actionTiles.isEmpty()) {
                System.out.println("NO ACTION READY; LET'S MOVE");
                gameInterface.moveToChunk(chunk.getLocation().getRelative(1, 0));
                actionTiles.clear();
                return;
            }*/
        //}
        /*if (!actionTiles.isEmpty()) {
            Iterator<Action> b = actionTiles.iterator();
            Action a = b.next();
            if (a.flag) {
                gameInterface.flagTile(a.tile.getLocation());
            } else {
                gameInterface.touchTile(a.tile.getLocation());
            }
            b.remove();
        }*/
    }


    class Action {

        boolean flag;
        ITile tile;

        Action(ITile tile, boolean flag) {
           this.tile = tile;
            this.flag = flag;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof Action) && this.tile.equals(((Action) other).tile)
                    && ((Action) other).flag == this.flag;
        }
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
