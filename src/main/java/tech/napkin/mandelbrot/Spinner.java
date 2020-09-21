/*
 * Copyright (C) 2020 SirNapkin1334
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The author can be contacted via:
 * 	Email: sirnapkin@protonmail.com
 * 	Twitter: @SirNapkin1334
 * 	Discord: @SirNapkin1334#7960
 * 	Reddit: u/SirNapkin1334
 * 	IRC: SirNapkin1334; Registered on Freenode, EFNet, possibly others
 *
 * If you wish to use this software in a way violating the terms, please
 * contact the author, as an exception can be made.
 */

package tech.napkin.mandelbrot;

import org.jetbrains.annotations.NotNull;

/**
 * A Class that lets you create a spinning progress indicator using any Object.
 *
 * It is recommended that you use a String or Character, and call {@link Spinner#next()} every time you wish to update
 * your current progress shown to the user.
 *
 * @param <T> The type of the Objects that will make up the progress indicator
 */
public class Spinner<T> {

	/** This stores the current location in the progress array. */
	private int counter = 0;

	/** This stores the available Objects that can be used in the progress indicator. */
	@NotNull
	private final T[] bars;

	/**
	 * Initializes the Spinner.
	 *
	 * @param bars an array of the Objects that will be used in the progress indicator.
	 */
	@SafeVarargs
	Spinner(@NotNull T...bars) { this.bars = bars; }

	/**
	 * Gets the next Object based on the last one.
	 *
	 * @return the next part of the Spinner.
	 */
	@NotNull
	T next() {
		if (counter >= bars.length) counter =0;
		return bars[counter++];
	}

	/** Resets the spinner's position. */
	void reset() { this.counter = 0; }

}
