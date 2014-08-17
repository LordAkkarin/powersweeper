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

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public enum Platform {
	LINUX,
	MAC_OS_X,
	SOLARIS,
	WINDOWS,
	UNKNOWN;

	/**
	 * Guesses the current platform.
	 * @return
	 */
	public static Platform guessPlatform () {
		String osName = System.getProperty("os.name").toLowerCase();

		// detect system
		if (osName.contains("win"))
			return WINDOWS;
		if (osName.contains("mac"))
			return MAC_OS_X;
		if (osName.contains("solaris") || osName.contains("sunos"))
			return SOLARIS;
		if (osName.contains("linux"))
			return LINUX;
		if (osName.contains("unix"))
			return LINUX;

		// unknown system
		return UNKNOWN;
	}
}