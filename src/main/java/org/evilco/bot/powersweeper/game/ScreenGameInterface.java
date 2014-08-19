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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.Powersweeper;
import org.evilco.bot.powersweeper.game.tile.ITile;
import org.evilco.bot.powersweeper.game.tile.TileLocation;
import org.evilco.bot.powersweeper.game.tile.error.TileException;
import org.evilco.bot.powersweeper.game.tile.parser.ITileParser;
import org.evilco.bot.powersweeper.game.tile.parser.image.ImageTileParser;
import org.evilco.bot.powersweeper.game.tile.parser.image.ImageTileTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RequiredArgsConstructor
public class ScreenGameInterface implements IGameInterface {

	/**
	 * Defines the border width.
	 */
	private static final int BORDER_WIDTH = 2;

	/**
	 * Defines the cell size.
	 */
	private static final int CELL_SIZE = 32;

	/**
	 * Defines the game URL template.
	 */
	public static final String GAME_URL = "http://mienfield.com/%s_%s";

	/**
	 * Stores the current chunk.
	 */
	@Getter
	private IChunk chunk = null;

	/**
	 * Stores the current chunk location.
	 */
	@Getter
	private ChunkLocation chunkLocation = null;

	/**
	 * Stores the parent application instance.
	 */
	@Getter
	@NonNull
	private final Powersweeper powersweeper;

	/**
	 * Stores the current screen.
	 */
	@Getter
	private BufferedImage screen = null;

	/**
	 * Stores the tile parser.
	 * @todo Move this to core and make it replaceable.
	 */
	@Getter
	private ITileParser tileParser = new ImageTileParser ();

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (ScreenGameInterface.class);

	/**
	 * Builds a tile related browser action.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return The action.
	 */
	public Actions buildTileAction (short x, short y) {
		// create action
		Actions action = new Actions (this.powersweeper.getDriverManager ().getDriver ());

		// find HTML element
		WebElement html = this.powersweeper.getDriverManager ().getDriver ().findElement (By.tagName ("html"));

		// get real coordinate
		int realX = this.getRealCoordinate (x);
		int realY = this.getRealCoordinate (y);

		realX += (CELL_SIZE / 2);
		realY += (CELL_SIZE / 2);

		// move cursor
		action.moveToElement (html, realX, realY);

		// return action
		return action;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flagTile (TileLocation location) {
		// check chunk location
		if (this.chunkLocation == null || !this.chunkLocation.equals (location.getChunk ().getLocation ())) this.moveToChunk (location.getChunk ().getLocation ());

		// prepare action
		Actions actions = this.buildTileAction (location.getX (), location.getY ());

		// click
		actions.contextClick ();

		// perform
		actions.build ().perform ();
	}

	/**
	 * Returns a real screen coordinate based on the cell location.
	 * @param coordinate The coordinate.
	 * @return The absolute coordinate.
	 */
	protected int getRealCoordinate (short coordinate) {
		return (coordinate * CELL_SIZE);
	}

	/**
	 * Guesses a single tile.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 */
	protected void guessTile (short x, short y) {
		// get real coordinates
		int realX = this.getRealCoordinate (x);
		int realY = this.getRealCoordinate (y);

		// extract tile
		BufferedImage tile = this.getScreen ().getSubimage (realX, realY, (CELL_SIZE - BORDER_WIDTH), (CELL_SIZE - BORDER_WIDTH));

		// calculate average color
		// TODO: Re-Add averages to simplify the process
		/* int averageR = 0;
		int averageG = 0;
		int averageB = 0;

		for (int pixelX = 0; pixelX < tile.getWidth (); pixelX++) {
			for (int pixelY = 0; pixelY < tile.getHeight (); pixelY++) {
				// parse color
				Color color = new Color (tile.getRGB (pixelX, pixelY));

				// append
				averageR += color.getR ();
				averageG += color.getG ();
				averageB += color.getB ();
			}
		}

		int size = (tile.getWidth () * tile.getHeight ());
		Color average = new Color (averageR, averageG, averageB); */

		// guess tile
		try {
			ITile parsedTile = this.getTileParser ().parse (new ImageTileTemplate (tile, null), new TileLocation (x, y, this.chunk));

			// store
			((MatrixChunk) this.getChunk ()).setTile (x, y, parsedTile);
		} catch (TileException ex) {
			getLogger ().warn ("Could not parse tile " + x + "," + y + ": " + ex.getMessage (), ex);
			return;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveToChunk (@NonNull ChunkLocation location) {
		getLogger ().entry ();

		// TODO: Do some sane movement

		// open new URL
		this.getPowersweeper ().getDriverManager ().getDriver ().get (String.format (GAME_URL, location.getX (), location.getY ()));

		// wait for a few seconds
		try {
			Thread.sleep (5000);
		} catch (Exception ex) {
			getLogger ().warn ("Aliens wake us up to early.");
		}

		// update location
		this.chunkLocation = location;

		// force update
		this.update ();

		// trace
		getLogger ().exit ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void touchTile (TileLocation location) {
		// check chunk location
		if (this.chunkLocation == null || !this.chunkLocation.equals (location.getChunk ().getLocation ())) this.moveToChunk (location.getChunk ().getLocation ());

		// prepare action
		Actions actions = this.buildTileAction (location.getX (), location.getY ());

		// click
		actions.click ();

		// perform
		actions.build ().perform ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update () {
		getLogger ().entry ();

		// re-create chunk
		this.chunk = new MatrixChunk (((short) 20), ((short) 20), this.chunkLocation);

		// update
		try {
			// move the curser out of the way
			this.buildTileAction (((short) 30), ((short) 30)).build ().perform ();

			// clear popups
			this.getPowersweeper ().getDriverManager ().getExecutor ().executeScript ("$('.popup').hide ();");

			// pull screen
			this.screen = ImageIO.read (new ByteArrayInputStream (((TakesScreenshot) this.getPowersweeper ().getDriverManager ().getDriver ()).getScreenshotAs (OutputType.BYTES)));

			// iterate over all fields
			for (short x = 0; x < this.chunk.getWidth (); x++) {
				for (short y = 0; y < this.chunk.getHeight (); y++) {
					this.guessTile (x, y);
				}
			}
		} catch (IOException ex) {
			getLogger ().error ("Could not pull a new version of the current screen: " + ex.getMessage (), ex);
		}
	}
}