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

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class MatrixChunk implements IChunk {

	/**
	 * Stores the chunk X-Coordinate.
	 */
	@Getter
	private final long chunkX;

	/**
	 * Stores the chunk Y-Coordinate.
	 */
	@Getter
	private final long chunkY;

	/**
	 * Stores the height.
	 */
	@Getter
	private final short height;

	/**
	 * Stores the field type.
	 */
	private FieldState type[][];

	/**
	 * Stores the field value.
	 */
	private short value[][];

	/**
	 * Stores the width.
	 */
	@Getter
	private final short width;

	/**
	 * Constructs a new MatrixChunk instance.
	 * @param width The width.
	 * @param height The height.
	 */
	public MatrixChunk (long x, long y, short width, short height) {
		this.chunkX = x;
		this.chunkY = y;

		this.width = width;
		this.height = height;

		// reset
		this.reset ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FieldState getField (short x, short y) {
		return this.type[y][x];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getUncoveredValue (short x, short y) {
		return this.value[y][x];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset () {
		// reset root
		this.type = new FieldState[this.height][];
		this.value = new short[this.height][];

		// reset rows
		for (short y = 0; y < this.height; y++) {
			this.type[y] = new FieldState[this.width];
			this.value[y] = new short[this.width];

			for (short x = 0; x < this.width; x++) {
				this.setField (x, y, FieldState.UNTOUCHED);
				this.setValue (x, y, ((short) -1));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setField (short x, short y, @NonNull FieldState fieldState) {
		this.type[y][x] = fieldState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue (short x, short y, short value) {
		this.value[y][x] = value;
	}
}