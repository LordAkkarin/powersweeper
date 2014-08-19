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

package org.evilco.bot.powersweeper.game.tile.parser.image;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.tile.parser.ITileTemplate;

import java.awt.image.BufferedImage;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RequiredArgsConstructor
public class ImageTileTemplate implements ITileTemplate {

	/**
	 * Stores the tile image.
	 */
	@Getter
	@NonNull
	private final BufferedImage image;

	/**
	 * Stores the template name (if any).
	 */
	@Getter
	private final String name;

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (ImageTileTemplate.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches (ITileTemplate template2) {
		getLogger ().entry ();

		// check type
		if (!(template2 instanceof ImageTileTemplate)) return getLogger ().exit (false);

		// cast
		ImageTileTemplate image = ((ImageTileTemplate) template2);

		// check size
		if (this.image.getWidth () != image.getImage ().getWidth ()) return getLogger ().exit (false);
		if (this.image.getHeight () != image.getImage ().getHeight ()) return getLogger ().exit (false);

		// compare pixels
		for (int x = 0; x < this.image.getWidth (); x++) {
			for (int y = 0; y < this.image.getHeight (); y++) {
				// extract alpha value
				int alpha = (this.image.getRGB (x, y) >> 24);

				// ignore transparent pixels
				if (alpha != 0x00 && this.image.getRGB (x, y) != image.getImage ().getRGB (x, y)) return getLogger ().exit (false);
			}
		}

		// all okay
		return getLogger ().exit (true);
	}
}