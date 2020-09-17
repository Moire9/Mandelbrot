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
