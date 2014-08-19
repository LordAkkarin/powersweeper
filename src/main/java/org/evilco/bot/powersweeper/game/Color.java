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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
@EqualsAndHashCode
public class Color {

	/**
	 * The red value.
	 */
	@Getter
	@Setter
	private int r;

	/**
	 * The green value.
	 */
	@Getter
	@Setter
	private int g;

	/**
	 * The blue value.
	 */
	@Getter
	@Setter
	private int b;

	/**
	 * Constructs a new Color instance.
	 * @param value The value.
	 */
	public Color (int value) {
		this (((value >> 16) & 0xFF), ((value >> 8) & 0xFF), (value & 0xFF));
	}

	/**
	 * Returns an RGB value.
	 * @return The value.
	 */
	public int getValue () {
		// initialize
		int value = 0;

		// add values
		value |= ((this.r & 0xFF) << 16);
		value |= ((this.g & 0xFF) << 8);
		value |= (this.b & 0xFF);

		// return the RGB value.
		return value;
	}

	/**
	 * Checks whether the color is a tone of gray (e.g. in between 0xFFFFFF and 0x000000).
	 * @return True if the color is within bounds.
	 */
	public boolean isGray () {
		return ((this.r == this.g) && (this.r == this.b));
	}
}