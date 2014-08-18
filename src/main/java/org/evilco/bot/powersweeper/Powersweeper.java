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
import org.evilco.bot.powersweeper.configuration.CommandLineArgumentConfiguration;
import org.evilco.bot.powersweeper.configuration.IConfiguration;
import org.evilco.bot.powersweeper.platform.DriverManager;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class Powersweeper {

	/**
	 * Indicates whether the bot is alive.
	 */
	@Getter
	private boolean alive = false;

	/**
	 * Stores the application configuration.
	 */
	@Getter (AccessLevel.PROTECTED)
	private IConfiguration configuration = null;

	/**
	 * Stores the driver manager.
	 */
	@Getter (AccessLevel.PROTECTED)
	private DriverManager driverManager = null;

	/**
	 * Stores the main logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	private static final Logger logger = LogManager.getLogger (Powersweeper.class);

	/**
	 * Constructs a new Powersweeper instance.
	 * @param configuration The application configuration.
	 */
	public Powersweeper (IConfiguration configuration) {
		getLogger ().info ("Powersweeper");
		getLogger ().info ("Copyright (C) 2014 Evil-Co <http://www.evil-co.org>");
		getLogger ().info ("---------------------------------------------------");

		// enable debug logging
		if (configuration.isDebugEnabled ()) {
			// get context & configuration
			LoggerContext context = ((LoggerContext) LogManager.getContext (false));
			Configuration config = context.getConfiguration ();

			// set new level
			config.getLoggerConfig (LogManager.ROOT_LOGGER_NAME).setLevel (Level.ALL);

			// update context
			context.updateLoggers (config);
		}

		// store configuration
		this.configuration = configuration;

		// test logging
		getLogger ().debug ("Debug logging enabled");

		// start driver
		this.driverManager = new DriverManager (configuration);
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
		this.alive = true;
	}
}