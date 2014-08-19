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
	 * Stores the current chunk instance.
	 */
	private IChunk currentChunk;

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
	 * Constructs a new CanvasGameInterface instance.
	 * @param parent The parent application.
	 */
	public ScreenGameInterface (@NonNull Powersweeper parent) {
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flagField (short x, short y) {
		if (this.currentChunk == null) return false;
		if (this.currentChunk.getField (x, y) != FieldState.UNTOUCHED) return false;

		// find HTML coordinate
		WebElement html = this.parent.getDriverManager ().getDriver ().findElement (By.name ("html"));

		// build action
		Actions action = new Actions (this.parent.getDriverManager ().getDriver ());
		action.moveToElement (html, this.getRealCoordinate (x), this.getRealCoordinate (y));
		action.contextClick ();

		// perform
		action.build ().perform ();

		// update cache
		this.currentChunk.setField (x, y, FieldState.FLAGGED);
		return true;
	}

	/**
	 * Calculates the absolute coordinate.
	 * @param in The input value.
	 * @return The real coordinate.
	 */
	public short getRealCoordinate (short in) {
		return ((short) ((in * CELL_SIZE) + Math.floor ((CELL_SIZE / 2))));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move (long x, long y) {
		// reset chunk
		this.currentChunk = null;

		// request new URL
		this.parent.movePosition (x, y);

		// update position
		this.currentChunkX = x;
		this.currentChunkY = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IChunk update () {
		// create matrix
		if (this.currentChunk == null) new MatrixChunk (this.currentChunkX, this.currentChunkY, ((short) 20), ((short) 20));

		// parse
		try {
			// get current display
			this.image = ImageIO.read (new ByteArrayInputStream (((TakesScreenshot) this.parent.getDriverManager ().getDriver ()).getScreenshotAs (OutputType.BYTES)));

			// TODO: Parse screen
		} catch (IOException ex) {
			// warn user as we're not updating our data this turn
			// this will cause unexpected behaviour
			getLogger ().warn ("Could process current browser screen: " + ex.getMessage ());
		}

		// return parsed chunk
		return this.currentChunk;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean uncoverField (short x, short y) {
		if (this.currentChunk == null) return false;
		if (this.currentChunk.getField (x, y) != FieldState.UNTOUCHED);

		// find HTML coordinate
		WebElement html = this.parent.getDriverManager ().getDriver ().findElement (By.name ("html"));

		// build action
		Actions action = new Actions (this.parent.getDriverManager ().getDriver ());
		action.moveToElement (html, this.getRealCoordinate (x), this.getRealCoordinate (y));
		action.click ();

		// perform
		action.build ().perform ();

		// update cache
		this.currentChunk.setField (x, y, FieldState.UNCOVERED);
		return true;
	}
}