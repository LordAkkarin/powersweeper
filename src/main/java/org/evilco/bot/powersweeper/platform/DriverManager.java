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

package org.evilco.bot.powersweeper.platform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.configuration.IConfiguration;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class DriverManager {

	/**
	 * Defines the default window dimensions.
	 */
	public static final Dimension WINDOW_DIMENSIONS = new Dimension (800, 600);

	/**
	 * Stores the application configuration.
	 */
	@Getter (AccessLevel.PROTECTED)
	private IConfiguration configuration;

	/**
	 * Stores the web driver.
	 */
	@Getter
	private WebDriver driver = null;

	/**
	 * Stores the internal logger instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	public static final Logger logger = LogManager.getLogger (DriverManager.class);

	/**
	 * Constructs a new DriverManager instance.
	 * @param configuration The configuration.
	 */
	public DriverManager (@NonNull IConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the driver file.
	 * @return The driver file.
	 */
	public File getDriverNativeFile () {
		// create builder
		StringBuilder builder = new StringBuilder ("chromedriver");

		// append extension
		if (Platform.guessPlatform () == Platform.WINDOWS) builder.append (".exe");

		// return finished path
		return (new File (this.configuration.getNativeLibraryDirectory (), builder.toString ()));
	}

	/**
	 * Initializes the web driver.
	 */
	public void initializeDriver () {
		getLogger ().entry ();

		// prepare capabilities
		DesiredCapabilities capabilities = new DesiredCapabilities ();
		capabilities.setJavascriptEnabled (true);

		// prepare driver
		switch (this.configuration.getDriver ()) {
			case CHROME:
				// set driver path
				System.setProperty ("webdriver.chrome.driver", this.getDriverNativeFile ().getAbsolutePath ());

				// start driver
				this.driver = new ChromeDriver (capabilities);
				break;
			case FIREFOX:
				this.driver = new FirefoxDriver (capabilities);
				break;
		}

		// log
		getLogger ().info ("Loaded driver of type " + this.driver.getClass ().getName () + ".");
		getLogger ().info ("Setting window properties ...");

		// set window dimension
		this.driver.manage ().window ().setSize (WINDOW_DIMENSIONS);

		// log
		getLogger ().info ("Browser is ready for operations.");

		// trace
		getLogger ().exit ();
	}

	/**
	 * Downloads all natives.
	 */
	public void downloadNatives () {
		getLogger ().entry ();

		// TODO
		getLogger ().warn ("Automatic download of natives is currently not available.");

		// trace
		getLogger ().exit ();
	}
}