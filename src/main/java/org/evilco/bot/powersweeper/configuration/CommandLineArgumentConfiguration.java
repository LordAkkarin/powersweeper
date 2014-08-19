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

package org.evilco.bot.powersweeper.configuration;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.cli.*;
import org.evilco.bot.powersweeper.brain.IBrain;
import org.evilco.bot.powersweeper.platform.Driver;

import java.io.File;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class CommandLineArgumentConfiguration implements IConfiguration {

	/**
	 * Defines the default brain.
	 */
	public static final String DEFAULT_BRAIN = "org.evilco.bot.powersweeper.brain.IdiotBrain";

	/**
	 * Defines the default driver.
	 */
	public static final String DEFAULT_DRIVER = "CHROME";

	/**
	 * Defines the default storage directory for natives.
	 */
	public static final String DEFAULT_NATIVE_DIRECTORY = "natives/";

	/**
	 * Defines valid command line options.
	 */
	public static final Options OPTIONS = (new Options ())
							.addOption (OptionBuilder.withLongOpt ("brain").hasArg ().create ("b"))
							.addOption (OptionBuilder.withLongOpt ("help").create ("h"))
							.addOption (OptionBuilder.withLongOpt ("natives").hasArg ().create ())
							.addOption (OptionBuilder.withLongOpt ("nonativedownload").create ())
							.addOption (OptionBuilder.withLongOpt ("debug").create ())
							.addOption (OptionBuilder.withLongOpt ("dumpunknowntiles").create ())
							.addOption (OptionBuilder.withLongOpt ("driver").hasArg ().create ())
							.addOption (OptionBuilder.hasArg ().create ("x"))
							.addOption (OptionBuilder.hasArg ().create ("y"));

	/**
	 * Stores the parsed command line.
	 */
	@Getter
	private CommandLine commandLine;

	/**
	 * Constructs a new CommandLineArgumentConfiguration instance.
	 * @param arguments The command line arguments.
	 * @throws ParseException Occurs if parsing the input did fail.
	 */
	public CommandLineArgumentConfiguration (@NonNull String[] arguments) throws ParseException {
		this.commandLine = (new PosixParser ()).parse (OPTIONS, arguments);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends IBrain> getBrainClass () throws ClassNotFoundException {
		return Class.forName ((this.commandLine.hasOption ("brain") ? this.commandLine.getOptionValue ("brain") : DEFAULT_BRAIN)).asSubclass (IBrain.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Driver getDriver () {
		return (Driver.valueOf ((this.commandLine.hasOption ("driver") ? this.commandLine.getOptionValue ("driver").toUpperCase () : DEFAULT_DRIVER)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getNativeLibraryDirectory () {
		return (new File ((this.commandLine.hasOption ("natives") ? this.commandLine.getOptionValue ("natives") : DEFAULT_NATIVE_DIRECTORY)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getStartX () {
		return (this.commandLine.hasOption ("x") ? Long.parseLong (this.commandLine.getOptionValue ("x")) : null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getStartY () {
		return (this.commandLine.hasOption ("y") ? Long.parseLong (this.commandLine.getOptionValue ("y")) : null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDebugEnabled () {
		return this.commandLine.hasOption ("debug");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDumpingEnabled () {
		return this.commandLine.hasOption ("dumpunknowntiles");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNativeDownloadEnabled () {
		return !this.commandLine.hasOption ("nonativedownload");
	}

	/**
	 * Prints the command line help.
	 */
	public static void printHelp () {
		(new HelpFormatter ()).printHelp ("Powersweeper <arguments>", OPTIONS);
	}
}