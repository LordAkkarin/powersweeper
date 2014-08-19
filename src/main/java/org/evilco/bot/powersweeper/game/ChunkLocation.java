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

import lombok.*;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
@EqualsAndHashCode
public class ChunkLocation {

	/**
	 * Stores the X-Coordinate.
	 */
	@Getter
	@Setter
	private long x;

	/**
	 * Stores the Y-Coordinate.
	 */
	@Getter
	@Setter
	private long y;

	/**
	 * Calculates the distance between two locations.
	 * @param location The location.
	 * @return The distance.
	 */
	public ChunkLocation getDistance (@NonNull ChunkLocation location) {
		long x = (this.getX () - location.getX ());
		long y = (this.getY () - location.getY ());

		// sanitize
		if (x < 0) x *= -1;
		if (y < 0) y *= -1;

		return (new ChunkLocation (x, y));
	}

	/**
	 * Gets a relative chunk location.
	 * @param x The X-Offset.
	 * @param y The Y-Offset.
	 * @return The relative location.
	 */
	public ChunkLocation getRelative (long x, long y) {
		return (new ChunkLocation ((this.getX () + x), (this.getY () + y)));
	}
}