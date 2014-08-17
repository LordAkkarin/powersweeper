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

import java.io.File;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class CommandLineArgumentConfiguration implements IConfiguration {

	/**
	 * Defines the default storage directory for natives.
	 */
	public static final String DEFAULT_NATIVE_DIRECTORY = "natives/";

	/**
	 * Defines valid command line options.
	 */
	public static final Options OPTIONS = (new Options ())
							.addOption (OptionBuilder.withLongOpt ("help").create ("h"))
							.addOption (OptionBuilder.withLongOpt ("natives").hasArg ().create ())
							.addOption (OptionBuilder.withLongOpt ("nonativedownload").create ())
							.addOption (OptionBuilder.withLongOpt ("debug").create ());

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
	public File getNativeLibraryDirectory () {
		return (new File ((this.commandLine.hasOption ("natives") ? DEFAULT_NATIVE_DIRECTORY : this.commandLine.getOptionValue ("natives"))));
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