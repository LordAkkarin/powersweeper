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

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.Powersweeper;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class ScreenGameInterface implements IGameInterface {

	/**
	 * Defines the cell size.
	 */
	public static final short CELL_SIZE = 32;

	/**
	 * Stores the bomb tile.
	 */
	public static final BufferedImage TILE_BOMB;

	/**
	 * Stores the tile map.
	 */
	public static final Map<Short, BufferedImage> TILE_MAP;

	/**
	 * Stores the current chunk instance.
	 */
	private IChunk currentChunk = null;

	/**
	 * Stores the current chunk X-Coordinate.
	 */
	private long currentChunkX = 0;

	/**
	 * Stores the current chunk Y-Coordinate.
	 */
	private long currentChunkY = 0;

	/**
	 * Stores the current browser image.
	 */
	@Getter
	private BufferedImage image;

	/**
	 * Stores the logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (ScreenGameInterface.class);

	/**
	 * Stores the parent application instance.
	 */
	private final Powersweeper parent;

	/**
	 * Static Initialization
	 */
	static {
		// create builder
		ImmutableMap.Builder<Short, BufferedImage> tileBuilder = new ImmutableMap.Builder<> ();

		// fill with known values
		for (short i = 1; i <= 8; i++) {
			try {
				tileBuilder.put (i, ImageIO.read (ScreenGameInterface.class.getResourceAsStream ("/tile/number-" + i + ".png")));
			} catch (Exception ex) {
				getLogger ().warn ("Could not load tile for number " + i + ": " + ex.getMessage (), ex);
			}
		}

		// load bomb tile
		BufferedImage bomb = null;

		try {
			bomb = ImageIO.read (ScreenGameInterface.class.getResourceAsStream ("/tile/bomb.png"));
		} catch (Exception ex) {
			getLogger ().warn ("Could not load bomb tile: " + ex.getMessage (), ex);
		}

		// store tile
		TILE_BOMB = bomb;

		// build map
		TILE_MAP = tileBuilder.build ();
	}

	/**
	 * Constructs a new CanvasGameInterface instance.
	 * @param parent The parent application.
	 */
	public ScreenGameInterface (@NonNull Powersweeper parent) {
		getLogger ().entry ();

		// store arguments
		this.parent = parent;

		// trace
		getLogger ().exit ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flagField (short x, short y) {
		getLogger ().entry ();

		// validate
		if (this.currentChunk == null) return getLogger ().exit (false);
		if (this.currentChunk.getField (x, y) != FieldState.UNTOUCHED) return getLogger ().exit (false);

		// find HTML coordinate
		WebElement html = this.parent.getDriverManager ().getDriver ().findElement (By.tagName ("html"));

		// build action
		Actions action = new Actions (this.parent.getDriverManager ().getDriver ());
		action.moveToElement (html, this.getRealCoordinate (x), this.getRealCoordinate (y));
		action.contextClick ();

		// perform
		action.build ().perform ();

		// update cache
		this.currentChunk.setField (x, y, FieldState.FLAGGED);
		return getLogger ().exit (true);
	}

	/**
	 * Returns a pixel value.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return The pixel color.
	 */
	public Color getPixel (int x, int y) {
		getLogger ().entry ();

		// decode color
		return getLogger ().exit (new Color (this.getRawPixel (x, y)));
	}

	/**
	 * Returns the raw pixel value.
	 * @param x The X-Coordinate.
	 * @param y The Y-Coordinate.
	 * @return The color value.
	 */
	public int getRawPixel (int x, int y) {
		getLogger ().entry ();

		// get value
		return getLogger ().exit ((this.image.getRGB (x, y) & 0x00FFFFFF)); // drop alpha bit
	}

	/**
	 * Calculates the absolute coordinate.
	 * @param in The input value.
	 * @return The real coordinate.
	 */
	public short getRealCoordinate (short in) {
		getLogger ().entry ();

		// calculate
		return getLogger ().exit (((short) ((in * CELL_SIZE) + Math.floor ((CELL_SIZE / 2)))));
	}

	/**
	 * Guesses a field state.
	 * @param fieldX The X-Coordinate.
	 * @param fieldY The Y-Coordinate.
	 */
	public void guessFieldState (short fieldX, short fieldY) {
		getLogger ().entry ();

		// calculate absolute position
		int x = (fieldX * 32);
		int y = (fieldY * 32);

		// extract tile
		BufferedImage tile = this.image.getSubimage (x, y, 30, 30);

		// calculate average color
		int averageR = 0;
		int averageG = 0;
		int averageB = 0;

		for (int currentX = 0; currentX < tile.getWidth (); currentX++) {
			for (int currentY = 0; currentY < tile.getHeight (); currentY++) {
				// parse color
				Color color = new Color (tile.getRGB (currentX, currentY));

				// add to average
				averageR += color.getR ();
				averageG += color.getG ();
				averageB += color.getB ();
			}
		}

		averageR /= 900;
		averageG /= 900;
		averageB /= 900;

		// re-create color
		Color average = new Color (averageR, averageG, averageB);

		// check for obvious values
		// Untouched field
		if (average.getValue () == 0xDBDBDB) {
			// update cache
			this.currentChunk.setField (fieldX, fieldY, FieldState.UNTOUCHED);
			this.currentChunk.setValue (fieldX, fieldY, ((short) -1));

			// trace
			getLogger ().trace ("Set value for field " + fieldX + "," + fieldY + " to UNTOUCHED:-1.");

			// skip further execution
			return;
		}

		// Flag
		if (average.getValue () == 0xC6B7B5) {
			// update cache
			this.currentChunk.setField (fieldX, fieldY, FieldState.FLAGGED);
			this.currentChunk.setValue (fieldX, fieldY, ((short) -1));

			// trace
			getLogger ().trace ("Set value for field " + fieldX + "," + fieldY + " to FLAGGED:-1.");

			// skip further execution
			return;
		}

		// check whether color average is within known bounds
		if (average.isGray ()) {
			// log
			getLogger ().trace ("Detected a perfect gray value. Checking for number tiles.");

			// guess number
			short number = this.guessNumber (tile, average);

			// store data
			if (number >= 0) {
				// update cache
				this.currentChunk.setField (fieldX, fieldY, FieldState.NUMBER);
				this.currentChunk.setValue (fieldX, fieldY, number);

				// trace
				getLogger ().trace ("Set value for field " + fieldX + "," + fieldY + " to NUMBER:" + number + ".");

				// skip further execution
				return;
			}

			// check for bombs
			if (this.tileMatches (tile, TILE_BOMB)) {
				// update cache
				this.currentChunk.setField (fieldX, fieldY, FieldState.BOMB);
				this.currentChunk.setValue (fieldX, fieldY, ((short) -1));

				// trace
				getLogger ().trace ("Set value for field " + fieldX + "," + fieldY + " to BOMB:-1.");

				// skip further execution
				return;
			}
		}

		// warn
		getLogger ().debug ("Could not guess value for field " + fieldX + "," + fieldY + ". Assuming flagged.");

		// set value to flagged
		this.currentChunk.setField (fieldX, fieldY, FieldState.FLAGGED);
		this.currentChunk.setValue (fieldX, fieldY, ((short) -1));

		// trace
		getLogger ().exit ();
	}

	/**
	 * Guesses a tile number.
	 * @param tile The tile.
	 * @param average The average color.
	 * @return The tile number (-1 if unknown).
	 */
	public short guessNumber (@NonNull BufferedImage tile, @NonNull Color average) {
		getLogger ().entry ();

		// check for empty field
		if (average.getValue () == 0xFFFFFF) return 0;

		// check numbers
		for (Map.Entry<Short, BufferedImage> preset : TILE_MAP.entrySet ()) {
			if (this.tileMatches (tile, preset.getValue ())) return preset.getKey ();
		}

		// return unknown value
		return getLogger ().exit (((short) -1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move (long x, long y) {
		getLogger ().entry ();

		// reset chunk
		this.currentChunk = null;

		// request new URL
		this.parent.movePosition (x, y);

		// update position
		this.currentChunkX = x;
		this.currentChunkY = y;

		// trace
		getLogger ().exit ();
	}

	/**
	 * Checks whether two tiles match exactly.
	 * @param i1 Tile 1.
	 * @param i2 Tile 2.
	 * @return True if both tiles are exactly the same.
	 */
	public boolean tileMatches (@NonNull BufferedImage i1, @NonNull BufferedImage i2) {
		// check size
		if (i1.getWidth () != i2.getWidth ()) return false;
		if (i1.getHeight () != i2.getHeight ()) return false;

		// check pixels
		for (int x = 0; x < i1.getWidth (); x++) {
			for (int y = 0; y < i1.getHeight (); y++) {
				// check pixel
				if (i1.getRGB (x, y) != i2.getRGB (x, y)) return false;
			}
		}

		// everything matches
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IChunk update () {
		getLogger ().entry ();

		// create matrix
		if (this.currentChunk == null) this.currentChunk = new MatrixChunk (this.currentChunkX, this.currentChunkY, ((short) 20), ((short) 20));

		// focefully close all popups
		this.parent.getDriverManager ().getExecutor ().executeScript ("$('.popup').hide ();");

		// parse
		try {
			// get current display
			this.image = ImageIO.read (new ByteArrayInputStream (((TakesScreenshot) this.parent.getDriverManager ().getDriver ()).getScreenshotAs (OutputType.BYTES)));

			// parse screen
			for (short x = 0; x < 20; x++) {
				for (short y = 0; y < 20; y++) {
					this.guessFieldState (x, y);
				}
			}
		} catch (IOException ex) {
			// warn user as we're not updating our data this turn
			// this will cause unexpected behaviour
			getLogger ().warn ("Could process current browser screen: " + ex.getMessage ());
		}

		// return parsed chunk
		return getLogger ().exit (this.currentChunk);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean uncoverField (short x, short y) {
		getLogger ().entry ();

		// verify
		if (this.currentChunk == null) getLogger ().exit (false);
		if (this.currentChunk.getField (x, y) != FieldState.UNTOUCHED) return getLogger ().exit (false);

		// find HTML coordinate
		WebElement html = this.parent.getDriverManager ().getDriver ().findElement (By.tagName ("html"));

		// build action
		Actions action = new Actions (this.parent.getDriverManager ().getDriver ());
		action.moveToElement (html, this.getRealCoordinate (x), this.getRealCoordinate (y));
		action.click ();

		// perform
		action.build ().perform ();

		// update cache
		this.currentChunk.setField (x, y, FieldState.UNCOVERED);
		return getLogger ().exit (true);
	}
}