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
package org.evilco.bot.powersweeper;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.evilco.bot.powersweeper.brain.IBrain;
import org.evilco.bot.powersweeper.configuration.CommandLineArgumentConfiguration;
import org.evilco.bot.powersweeper.configuration.IConfiguration;
import org.evilco.bot.powersweeper.game.ChunkLocation;
import org.evilco.bot.powersweeper.game.IGameInterface;
import org.evilco.bot.powersweeper.game.ScreenGameInterface;
import org.evilco.bot.powersweeper.platform.DriverManager;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Stack;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Powersweeper {

	/**
	 * Stores the maximum amount of time stored for calculating averages.
	 */
	public static final int AVERAGE_ACCURACY = 15;

	/**
	 * Defines the minimum average wait time.
	 */
	public static final long WAIT_TIME_THRESHOLD = 800;

	/**
	 * Indicates whether the bot is alive.
	 */
	@Getter
	private boolean alive = false;

	/**
	 * Stores the currently active brain instance.
	 */
	@Getter
	private IBrain brain = null;

	/**
	 * Stores the application configuration.
	 */
	@Getter
	private IConfiguration configuration = null;

	/**
	 * Stores the driver manager.
	 */
	@Getter
	private DriverManager driverManager = null;

	/**
	 * Stores the active game interface.
	 */
	@Getter
	private IGameInterface gameInterface = null;

	/**
	 * Stores the main logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (Powersweeper.class);

	/**
	 * Stores a stack for keeping track of wait times.
	 */
	private Stack<Long> timeStack = new Stack<> ();

	/**
	 * Constructs a new Powersweeper instance.
	 * @param configuration The application configuration.
	 */
	public Powersweeper (IConfiguration configuration) {
		getLogger ().info ("Powersweeper");
		getLogger ().info ("Copyright (C) 2014 Evil-Co <http://www.evil-co.org>");
		getLogger ().info ("---------------------------------------------------");

		// enable debug logging
		if (configuration.isDebugEnabled () || configuration.isTracingEnabled ()) {
			// get context & configuration
			LoggerContext context = ((LoggerContext) LogManager.getContext (false));
			Configuration config = context.getConfiguration ();

			// set new level
			config.getLoggerConfig (LogManager.ROOT_LOGGER_NAME).setLevel ((configuration.isTracingEnabled () ? Level.ALL : Level.DEBUG));
			// update context
			context.updateLoggers (config);
		}

		// store configuration
		this.configuration = configuration;

		// test logging
		getLogger ().debug ("Debug logging enabled");

		// start driver
		this.driverManager = new DriverManager (configuration);

		// download natives
		this.driverManager.downloadNatives ();

		// initialize gamer interface
		this.gameInterface = new ScreenGameInterface (this);

		// trace
		getLogger ().exit ();
	}

	/**
	 * Adds a new wait time to stack.
	 * @param time The time.
	 */
	protected void addWaitTime (long time) {
		// add to stack
		this.timeStack.add (time);

		// check maximum
		if (this.timeStack.size () > AVERAGE_ACCURACY) this.timeStack.remove (0);
	}

	/**
	 * Returns the average wait time.
	 * @return The average time.
	 */
	public long getAverageWaitTime () {
		// no data available?
		if (this.timeStack.size () == 0) return 0;

		// initialize
		long time = 0;

		// add up all times
		for (long current : this.timeStack) time += current;

		// calculate average
		return (time / this.timeStack.size ());
	}

	/**
	 * Main Entry Point
	 * @param arguments The arguments.
	 */
	public static void main (String[] arguments) {
		try {
			// parse configuration
			CommandLineArgumentConfiguration configuration = new CommandLineArgumentConfiguration (arguments);

			// check for help argument
			if (configuration.getCommandLine ().hasOption ("help")) {
				CommandLineArgumentConfiguration.printHelp ();
				System.exit (0);
			}

			// create bot instance
			Powersweeper powersweeper = new Powersweeper (configuration);

			// execute
			powersweeper.think ();
		} catch (ParseException ex) {
			CommandLineArgumentConfiguration.printHelp ();
			System.exit (-1);
		}
	}

	/**
	 * Starts thinking.
	 */
	public void think () {
		getLogger ().entry ();

		// store new state
		this.alive = true;

		// initialize drivers
		this.driverManager.initializeDriver ();

		// initialize brain
		try {
			this.brain = this.configuration.getBrainClass ().newInstance ();
		} catch (Exception ex) {
			// log
			getLogger ().error ("Could not load brain implementation: " + ex.getMessage (), ex);

			// exit
			System.exit (-10);
		}

		// get start coordinates
		Long x = this.configuration.getStartX ();
		Long y = this.configuration.getStartY ();

		// generate initial coordinates
		Random random = new SecureRandom ();

		if (x == null) x = ((long) (1337 + random.nextInt (3000)));
		if (y == null) y = ((long) (1337 + random.nextInt (3000)));

		// move
		this.gameInterface.moveToChunk (new ChunkLocation (x, y));

		// enter main loop
		while (this.alive) {
			// trace
			getLogger ().trace ("Entering processing loop.");

            //if it doesn't have a queue, or does have a queue and is empty
            if (!gameInterface.hasQueue() || (gameInterface.hasQueue() && gameInterface.getActionQueue().isEmpty())) {

                // update interface
                this.gameInterface.update();

                // call AI
                this.brain.think(this.gameInterface);

            } else if (gameInterface.hasQueue() && !gameInterface.getActionQueue().isEmpty()) { //the queue exists and it has actions
                //TODO update this to a Stack and do #pop() ?
                gameInterface.getActionQueue().get(0).perform();//go one-by-one
                gameInterface.getActionQueue().remove(0);
            }

			// wait for some time
			try {
				// initialize variables
				long waitTime = 750;

				/*// get current average
				long average = this.getAverageWaitTime ();

				// get wait time depending on current average
				if (average < WAIT_TIME_THRESHOLD)
					waitTime = (2000 + random.nextInt (15000));
				else
					waitTime = (250 + random.nextInt (1500));

				// append time
				this.addWaitTime (waitTime);

				// trace
				getLogger ().trace ("Waiting for " + waitTime + " ms to ensure we're not being detected (average is " + average + " ms).");*/

				// sleep
				Thread.sleep (waitTime);
			} catch (InterruptedException ex) {
				getLogger ().warn ("Our sleep was interrupted by aliens: " + ex.getMessage (), ex);
			}

			// trace
			getLogger ().trace ("Exiting processing loop.");
		}

		// trace
		getLogger ().exit ();
	}
}