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

package org.evilco.bot.powersweeper.game.tile;

import lombok.*;
import org.evilco.bot.powersweeper.game.IChunk;
import org.evilco.bot.powersweeper.game.tile.generic.UntouchedTile;

import java.util.ArrayList;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
@EqualsAndHashCode
public class TileLocation {

	/**
	 * Stores the X-Coordinate.
	 */
	@Getter
	@Setter
	private short x;

	/**
	 * Stores the Y-Coordinate.
	 */
	@Getter
	@Setter
	private short y;

	/**
	 * Stores the chunk.
	 */
	@Getter
	@Setter
	@NonNull
	private IChunk chunk;

    /**
     * Returns the tile.
     *
     * @return The tile.
     */
    public ITile getTile() {
        return this.chunk.getTile(this.getX(), this.getY());
    }

    /**
     * Gets the neighbors of the tile.
     *
     * @return The neighboring tiles of this TileLocation.
     */
    public ITile[] getNeighbors() {
        ArrayList<ITile> neighbors = new ArrayList<>();
        //top
        if (y != 0) {
            if (x != 0) neighbors.add(chunk.getTile((short) (x - 1), (short) (y - 1)));
            neighbors.add(chunk.getTile(x, (short) (y - 1)));
            if (x != chunk.getWidth() - 1) neighbors.add(chunk.getTile((short) (x + 1), (short) (y - 1)));
        }
        //middle
        if (x != 0) neighbors.add(chunk.getTile((short) (x - 1), y));
        if (x != chunk.getWidth() - 1) neighbors.add(chunk.getTile((short) (x + 1), y));
        //bottom
        if (y != chunk.getHeight() - 1) {
            if (x != 0) neighbors.add(chunk.getTile((short) (x - 1), (short) (y + 1)));
            neighbors.add(chunk.getTile((x), (short) (y + 1)));
            if (x != chunk.getWidth() - 1) neighbors.add(chunk.getTile((short) (x + 1), (short) (y + 1)));
        }
        return neighbors.toArray(new ITile[neighbors.size()]);
    }


    public ITile getBlankNeighbor() {
        ITile[] neighbors = getNeighbors();
        if (neighbors.length > 0) {
           for (ITile i : neighbors) {
               if (i instanceof UntouchedTile) return i;
           }
        }
        return null;
    }

	/**
	 * Returns a relative location.
	 * @param x The X-Offset.
	 * @param y The Y-Offset.
	 * @return The relative tile.
	 */
	public TileLocation getRelative (short x, short y) {
		return (new TileLocation (((short) (this.getX () + x)), ((short) (this.getY () + y)), this.getChunk ()));
	}
}