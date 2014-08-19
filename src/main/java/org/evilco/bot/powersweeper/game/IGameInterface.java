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

import org.evilco.bot.powersweeper.game.tile.TileLocation;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IGameInterface {

	/**
	 * Flags a tile.
	 * @param location The tile location.
	 */
	public void flagTile (TileLocation location);

	/**
	 * Returns the underlying chunk.
	 * @return The chunk.
	 */
	public IChunk getChunk ();

	/**
	 * Moves the interface to a new chunk.
	 * @param location The chunk location.
	 */
	public void moveToChunk (ChunkLocation location);

	/**
	 * Touches a tile.
	 * @param location The tile location.
	 */
	public void touchTile (TileLocation location);

	/**
	 * Updates the game state.
	 */
	public void update ();
}