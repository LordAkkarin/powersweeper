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
public interface IChunk {

	/**
	 * Returns the chunk X coordinate.
	 * @return The X coordinate.
	 */
	public long getChunkX ();

	/**
	 * Returns the chunk Y coordinate.
	 * @return The Y coordinate.
	 */
	public long getChunkY ();

	/**
	 * Returns the field type at location X, Y.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return The current field type.
	 */
	public FieldType getField (int x, int y);

	/**
	 * Returns the uncovered value (the numeric value) of a field.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return The uncovered value.
	 */
	public short getUncoveredValue (int x, int y);

	/**
	 * Resets the chunk information.
	 */
	public void reset ();

	/**
	 * Sets the field at location X, Y.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @param fieldType The new field type.
	 */
	public void setField (int x, int y, FieldType fieldType);
}