/*
 * Copyright 2014 Johannes Donath <johannesd@evil-co.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.evilco.bot.powersweeper.game;

import lombok.Getter;
import lombok.NonNull;
import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.generic.NumberTile;
import org.evilco.bot.powersweeper.game.tile.parser.TileCounter;

import java.util.ArrayList;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class MatrixChunk implements IChunk {

	/**
	 * Stores the chunk height.
	 */
	@Getter
	private final short height;

	/**
	 * Stores the chunk location.
	 */
	@Getter
	private final ChunkLocation location;

	/**
	 * Stores all tiles.
	 */
	private ITile[][] tiles;

	/**
	 * Stores the chunk width.
	 */
	@Getter
	private final short width;

	/**
	 * Constructs a new MatrixChunk instance.
	 * @param width The width.
	 * @param height The height.
	 * @param location The location.
	 */
	public MatrixChunk (short width, short height, @NonNull ChunkLocation location) {
		this.width = width;
		this.height = height;
		this.location = location;

		// reset
		this.reset ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITile getTile (short x, short y) {
		return this.tiles[y][x];
	}

	/**
	 * Resets the chunk.
	 */
	public void reset () {
		this.tiles = new ITile[this.height][];

		for (short y = 0; y < this.height; y++) this.tiles[y] = new ITile[this.width];
	}

	/**
	 * Sets a new tile.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @param tile The new tile.
	 */
	public void setTile (short x, short y, ITile tile) {
		this.tiles[y][x] = tile;
	}

    /**
     * Checks to see if the screen is blank (or untouched).
     * @return true if the screen is mostly untouched tiles.
     */
    public boolean isBlank() {
        int count = 0;
        for (ITile[] arr : tiles) {
           count += TileCounter.getCount(arr, TileCounter.TileType.BLANK);
        }
        return count > 250;
    }

    public NumberTile[] getNumberTiles() {
        ArrayList<NumberTile> toReturn = new ArrayList<>();
        for (ITile[] arr : tiles) {
            for (ITile i : arr) {
                if (i instanceof NumberTile) {
                    toReturn.add((NumberTile)i);
                }
            }
        }
        return toReturn.toArray(new NumberTile[toReturn.size()]);
    }

    /**
     * Finds the nearest number tile.
     * @param number The number to look for.
     * @return The tile if found, or null.
     */
    public NumberTile findNumberTile(short number) {
        NumberTile[] numberTiles = getNumberTiles();
        if (numberTiles.length > 0) {
            for (NumberTile t : numberTiles) {
                if (t.getValue() == number) return t;
            }
        }
        return null;
    }

    /**
     * This method finds a viable number tile to start clicking neighbors of.
     *
     * Usually, this can be a "1" square with a bunch of blank neighbors to try clicking.
     *
     * @return A viable exploration tile, or null.
     */
    public NumberTile findViableExplorationTile() {
        NumberTile[] numberTiles = getNumberTiles();
        if (numberTiles.length > 0) {
           for (NumberTile nt : numberTiles) {
              ITile[] neighbors = nt.getLocation().getNeighbors();
               if (neighbors.length > 5 && nt.getValue() < 4 && TileCounter.getCount(neighbors, TileCounter.TileType.BLANK) > 4) {
                   return nt;
               }
           }
        }
        return null;
    }
}