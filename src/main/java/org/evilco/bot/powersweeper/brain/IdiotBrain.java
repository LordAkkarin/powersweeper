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

package org.evilco.bot.powersweeper.brain;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.IGameInterface;
import org.evilco.bot.powersweeper.game.tile.TileLocation;

import java.util.Random;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class IdiotBrain implements IBrain {

	/**
	 * Stores the internal logger instnace.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (IdiotBrain.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void think (IGameInterface gameInterface) {
		// create a random
		Random random = new Random ();

		// select a random field within chunk bounds
		short x = ((short) random.nextInt (gameInterface.getChunk ().getWidth ()));
		short y = ((short) random.nextInt (gameInterface.getChunk ().getHeight ()));

		// click random tile
		gameInterface.touchTile (new TileLocation (x, y, gameInterface.getChunk ()));

		// log
		getLogger ().info ("Uncovering field " + x + "," + y + ".");
	}
}