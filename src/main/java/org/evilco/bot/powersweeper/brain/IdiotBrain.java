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
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.IChunk;
import org.evilco.bot.powersweeper.game.IGameInterface;

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
	public void think (@NonNull IChunk chunk, @NonNull IGameInterface gameInterface) {
		// create a random
		Random random = new Random ();

		// select a random field within chunk bounds
		short x = ((short) random.nextInt (chunk.getWidth ()));
		short y = ((short) random.nextInt (chunk.getHeight ()));

		// click random tile
		boolean success = gameInterface.uncoverField (x, y);
		// boolean success = gameInterface.flagField (x, y);

		// log
		getLogger ().info ("Uncovering field " + x + "," + y + " (success: " + success + ").");
	}
}