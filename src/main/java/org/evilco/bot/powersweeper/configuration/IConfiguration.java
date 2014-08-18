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

import org.evilco.bot.powersweeper.brain.IBrain;
import org.evilco.bot.powersweeper.platform.Driver;

import java.io.File;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IConfiguration {

	/**
	 * Returns the brain class.
	 * @return The class.
	 * @throws ClassNotFoundException
	 */
	public Class<? extends IBrain> getBrainClass () throws ClassNotFoundException;

	/**
	 * Returns the bot driver.
	 * @return The driver type.
	 */
	public Driver getDriver ();

	/**
	 * Returns the directory which stores the native directories.
	 * @return The directory reference.
	 */
	public File getNativeLibraryDirectory ();

	/**
	 * Checks whether debugging is enabled.
	 * @return True if debugging is enabled.
	 */
	public boolean isDebugEnabled ();

	/**
	 * Checks whether native automatic download of native libraries is enabled.
	 * @return True if download is enabled.
	 */
	public boolean isNativeDownloadEnabled ();
}