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

import lombok.Getter;
import org.evilco.bot.powersweeper.game.IGameInterface;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class DumpBrain extends IdiotBrain {

	/**
	 * Stores the maximum amount of moves performed until the bot will move on.
	 */
	public static final int THRESHOLD = 150;

	/**
	 * Stores the click counter.
	 */
	@Getter
	private int counter = 0;

	@Override
	public void think (IGameInterface gameInterface) {
		super.think (gameInterface);

		// update counter
		this.counter++;

		// log
		getLogger ().debug (this.counter + " moves out of " + THRESHOLD + " performed.");

		// check
		if (this.counter >= THRESHOLD) {
			// log
			getLogger ().info ("Finished current field. Moving on.");

			// move field
			gameInterface.moveToChunk (gameInterface.getChunk ().getLocation ().getRelative (1, 0));

			// reset counter
			this.counter = 0;
		}
	}
}