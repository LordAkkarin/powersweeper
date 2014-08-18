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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.evilco.bot.powersweeper.configuration.IConfiguration;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class DriverManager {

	/**
	 * Defines the chrome driver URL.
	 */
	public static final String CHROME_DRIVER_URL = "http://chromedriver.storage.googleapis.com/2.9/chromedriver_%s.zip";

	/**
	 * Defines the default window dimensions.
	 */
	public static final Dimension WINDOW_DIMENSIONS = new Dimension (800, 800);

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
		// skip other browsers
		if (this.configuration.getDriver () != Driver.CHROME) return null;

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

		// skip download if unneeded
		if (this.configuration.getDriver () != Driver.CHROME) {
			// debug
			getLogger ().debug ("Driver download is not required for " + this.configuration.getDriver ().toString () + " driver implementation.");

			// exit
			getLogger ().exit ();
			return;
		}

		// check
		if (!this.configuration.isNativeDownloadEnabled ()) {
			// log
			getLogger ().info ("Automatic download of driver natives is disabled.");

			// log possible error
			if (!this.getDriverNativeFile ().exists ()) getLogger ().warn ("The native could not be found! Proceed with caution.");

			// exit
			getLogger ().exit ();
			return;
		}

		// check driver file
		if (this.getDriverNativeFile ().exists ()) {
			// log
			getLogger ().info ("Driver file seems to exist. Skipping download.");

			// exit
			getLogger ().exit ();
			return;
		}

		// log
		getLogger ().info ("Starting download of natives.");

		// get platform
		Platform platform = Platform.guessPlatform ();
		boolean is64Bit = System.getProperty ("os.arch").endsWith ("64");

		// build download URL
		String filename = "potato";

		switch (platform) {
			case LINUX:
			case SOLARIS:
			case UNKNOWN:
				filename = "linux" + (is64Bit ? "64" : 32);
				break;
			case MAC_OS_X:
				filename = "mac32";
				break;
			case WINDOWS:
				filename = "win32";
				break;
		}

		// download and extract
		FileOutputStream outputStream = null;

		// ensure directory exists
		this.getDriverNativeFile ().getParentFile ().mkdirs ();

		try {
			URL downloadURL = new URL (String.format (CHROME_DRIVER_URL, filename));

			// log
			getLogger ().info ("Downloading driver from " + downloadURL.toString () + " ...");

			// create file reference
			File zipFile = new File (this.getDriverNativeFile ().getParentFile (), filename + ".zip");

			// start download
			ReadableByteChannel readableByteChannel = Channels.newChannel (downloadURL.openStream ());
			outputStream = new FileOutputStream (zipFile);
			outputStream.getChannel ().transferFrom (readableByteChannel, 0, Long.MAX_VALUE);

			// extract zip contents
			this.extract (zipFile);

			// log
			getLogger ().info ("Finished native download.");

			// delete zip
			if (zipFile.delete ())
				getLogger ().info ("Removed temporary archive.");
			else
				getLogger ().warn ("Could not remove temporary archive.");
		} catch (IOException ex) {
			getLogger ().error ("Could not download file from URL \"" + String.format (CHROME_DRIVER_URL, filename) + "\": " + ex.getMessage (), ex);
		} finally {
			if (outputStream != null) IOUtils.closeQuietly (outputStream);
		}

		// trace
		getLogger ().exit ();
	}

	/**
	 * Extracts a driver archive.
	 * @param file The archive file.
	 * @throws IOException
	 */
	protected void extract (File file) throws IOException {
		// get input stream
		ZipInputStream inputStream = new ZipInputStream (new FileInputStream (file));
		FileOutputStream outputStream = null;

		try {
			// initialize variable
			ZipEntry entry;

			// copy all files
			while ((entry = inputStream.getNextEntry ()) != null) {
				// log
				getLogger ().info ("Extracting file " + entry.getName () + " from driver archive " + file.getName () + ".");

				// create file reference
				File outputFile = new File (this.getDriverNativeFile ().getParentFile (), entry.getName ());

				// ensure parent exists
				outputFile.getParentFile ().mkdirs ();

				// copy file
				ReadableByteChannel readableByteChannel = Channels.newChannel (inputStream);
				outputStream = new FileOutputStream (outputFile);
				outputStream.getChannel ().transferFrom (readableByteChannel, 0, Long.MAX_VALUE);

				// close stream
				outputStream.close ();
				outputStream = null;
			}
		} finally {
			if (inputStream != null) IOUtils.closeQuietly (inputStream);
			if (outputStream != null) IOUtils.closeQuietly (outputStream);
		}
	}
}