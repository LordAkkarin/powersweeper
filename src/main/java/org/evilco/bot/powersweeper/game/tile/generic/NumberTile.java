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

package org.evilco.bot.powersweeper.game.tile.generic;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.tile.TileLocation;
import org.evilco.bot.powersweeper.game.tile.parser.ITileParser;
import org.evilco.bot.powersweeper.game.tile.parser.ITileTemplate;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class NumberTile extends AbstractTile {

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (NumberTile.class);

	/**
	 * Stores the tile value.
	 */
	@Getter
	private final short value;

	/**
	 * Constructs a new NumberTile instance.
	 * @param location The tile location.
	 * @param template The tile template.
	 * @param parser The tile parser.
	 */
	public NumberTile (TileLocation location, ITileTemplate template, ITileParser parser) {
		super (location, template, parser);

		// get number
		short value = -1;

		for (short i = 0; i <= 8; i++) {
			try {
				// store
				if (parser.getTemplate ("number-" + i).matches (template)) {
					value = i;
					break;
				}
			} catch (NullPointerException ex) {
				getLogger ().warn ("Tile template for number " + i + " seems to be missing.");
			}
		}

		this.value = value;

		// log
		getLogger ().trace ("Set value to " + this.value + ".");
	}
}