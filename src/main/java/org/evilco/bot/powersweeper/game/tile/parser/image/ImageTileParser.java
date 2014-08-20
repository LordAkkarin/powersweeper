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

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.TileLocation;
import org.evilco.bot.powersweeper.game.tile.error.TileException;
import org.evilco.bot.powersweeper.game.tile.generic.*;
import org.evilco.bot.powersweeper.game.tile.parser.ITileParser;
import org.evilco.bot.powersweeper.game.tile.parser.ITileTemplate;
import org.evilco.bot.powersweeper.game.tile.parser.error.TileInitializationException;
import org.evilco.bot.powersweeper.game.tile.parser.error.UnknownTileException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class ImageTileParser implements ITileParser {

	/**
	 * Stores all tile templates.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Map<String, ImageTileTemplate> TEMPLATES;

	/**
	 * Stores all tile mappings.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Map<String, Class<? extends ITile>> TILES;

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (ImageTileParser.class);

	/**
	 * Static Initializer
	 */
	static {
		// create builders
		ImmutableMap.Builder<String, ImageTileTemplate> templateBuilder = new ImmutableMap.Builder<> ();
		ImmutableMap.Builder<String, Class<? extends ITile>> tileBuilder = new ImmutableMap.Builder<> ();

		// load waiting
		try {
			templateBuilder.put ("waiting", new ImageTileTemplate (ImageIO.read (ImageTileParser.class.getResourceAsStream ("/tile/waiting.png")), "waiting"));
			tileBuilder.put ("waiting", WaitingTile.class);
		} catch (IOException ex) {
			getLogger ().warn ("Could not load tile \"waiting.png\": " + ex.getMessage ());
		}

		// load untouched
		try {
			templateBuilder.put ("untouched", new ImageTileTemplate (ImageIO.read (ImageTileParser.class.getResourceAsStream ("/tile/untouched.png")), "untouched"));
			tileBuilder.put ("untouched", UntouchedTile.class);
		} catch (IOException ex) {
			getLogger ().warn ("Could not load tile \"bomb.png\": " + ex.getMessage ());
		}

		// load numbers
		for (short i = 0; i <= 8; i++) {
			try {
				templateBuilder.put ("number-" + i, new ImageTileTemplate (ImageIO.read (ImageTileParser.class.getResourceAsStream ("/tile/number-" + i + ".png")), "number-" + i));
				tileBuilder.put ("number-" + i, NumberTile.class);
			} catch (Exception ex) {
				getLogger ().warn ("Could not load tile \"number-" + i + ".png\": " + ex.getMessage ());
			}
		}

		// load bomb
		try {
			templateBuilder.put ("bomb", new ImageTileTemplate (ImageIO.read (ImageTileParser.class.getResourceAsStream ("/tile/bomb.png")), "bomb"));
			tileBuilder.put ("bomb", BombTile.class);
		} catch (Exception ex) {
			getLogger ().warn ("Could not load tile \"bomb.png\": " + ex.getMessage ());
		}

		// build map
		TEMPLATES = templateBuilder.build ();
		TILES = tileBuilder.build ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITileTemplate getTemplate (String name) {
		if (!TEMPLATES.containsKey (name)) return null;
		return TEMPLATES.get (name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITile parse (@NonNull ITileTemplate sourceTile, @NonNull TileLocation location) throws TileException {
		getLogger ().entry ();

		// find template
		for (Map.Entry<String, ImageTileTemplate> entry : TEMPLATES.entrySet ()) {
			// skip non-matching tiles
			if (!entry.getValue ().matches (sourceTile)) continue;

			// verify template
			if (!TILES.containsKey (entry.getKey ())) throw new UnknownTileException ("Could not find tile for template \"" + entry.getKey () + "\".");

			// get class
			Class<? extends ITile> clazz = TILES.get (entry.getKey ());

			// log
			getLogger ().trace ("Found matching template \"" + entry.getKey () + "\" which is assigned to tile " + clazz.getName () + ".");

			// construct
			try {
				// find constructor
				Constructor<? extends ITile> constructor = clazz.getConstructor (TileLocation.class, ITileTemplate.class, ITileParser.class);

				// ensure constructor is accessible
				constructor.setAccessible (true);

				// create a new instance
				return getLogger ().exit (constructor.newInstance (location, entry.getValue (), this));
			} catch (NoSuchMethodException ex) {
				// warn
				getLogger ().warn ("Could not find appropriate constructor (TileLocation, ITileTemplate, ITileParser) for tile \"" + clazz.getName () + "\".");

				// fuck over everyone
				throw new TileInitializationException ("Could not find appropriate constructor (TileLocation, ITileTemplate, ITileParser) for tile \"" + clazz.getName () + "\".");
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
				// warn
				getLogger ().warn ("Could not construct tile \"" + clazz.getName () + "\": " + ex.getMessage (), ex);

				// fuck over everyone
				throw new TileInitializationException ("Could not construct tile \"" + clazz.getName () + "\": " + ex.getMessage (), ex);
			}
		}

		// warn
		//getLogger ().warn ("Could not find matching tile for location " + location.getX () + "," + location.getY () + " in chunk " + location.getChunk ().getLocation ().getX () + "," + location.getChunk ().getLocation ().getY () + ". Assuming flag.");

		// assume flag
		return getLogger ().exit (new FlaggedTile (location, null, this));
	}
}