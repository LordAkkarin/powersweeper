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

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IGameInterface {

	/**
	 * Flags a field.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return True if the field was flagged.
	 */
	public boolean flagField (short x, short y);

	/**
	 * Returns the current chunk.
	 * @return The chunk.
	 */
	public IChunk getCurrentChunk ();

	/**
	 * Moves the screen towards a new chunk.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 */
	public void move (long x, long y);

	/**
	 * Uncovers a field.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return True if the field was uncovered.
	 */
	public boolean uncoverField (short x, short y);

	/**
	 * Updates the screen.
	 * @return The current chunk instance.
	 */
	public IChunk update ();
}