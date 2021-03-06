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

import org.evilco.bot.powersweeper.game.tile.ITile;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IChunk {

	/**
	 * Returns the chunk height.
	 * @return The height.
	 */
	public short getHeight ();

	/**
	 * Returns the chunk location.
	 * @return The location.
	 */
	public ChunkLocation getLocation ();

	/**
	 * Returns a tile.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return The tile.
	 */
	public ITile getTile (short x, short y);

	/**
	 * Returns the chunk width.
	 * @return The width.
	 */
	public short getWidth ();
}