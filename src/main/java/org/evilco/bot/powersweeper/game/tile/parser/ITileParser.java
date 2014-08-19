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

package org.evilco.bot.powersweeper.game.tile.parser;

import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.TileLocation;
import org.evilco.bot.powersweeper.game.tile.error.TileException;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface ITileParser {

	/**
	 * Returns a tile template.
	 * @param name The template name.
	 * @return The template.
	 */
	public ITileTemplate getTemplate (String name);

	/**
	 * Parses a tile.
	 * @param sourceTile The source tile.
	 * @param location The tile location.
	 * @return The tile.
	 * @throws TileException Occurs if a tile could not be parsed properly.
	 */
	public ITile parse (ITileTemplate sourceTile, TileLocation location) throws TileException;
}