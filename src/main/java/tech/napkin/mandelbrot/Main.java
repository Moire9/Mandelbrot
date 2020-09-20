/* 	Copyright (C) 2020 SirNapkin1334
 *
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * 	The author can be contacted via:
 * 		Email: sirnapkin@protonmail.com
 * 		Twitter: @SirNapkin1334
 * 		Discord: @SirNapkin1334#7960
 * 		Reddit: u/SirNapkin1334
 * 		IRC: SirNapkin1334; Registered on Freenode, EFNet, possibly others
 *
 * 	If you wish to use this software in a way violating the terms, please
 * 	contact the author, as an exception can be made.
 */

package tech.napkin.mandelbrot;

import ar.com.hjg.pngj.ImageLineInt;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Config config = new Config(args);

		// ==== INIT WRITING VARS ====

		final int[][] scanlines;

		if (config.optimized) scanlines = new int[config.height / 2][config.width * 3];
		else scanlines = new int[0][0]; // don't actually need this but java (or me) is stupid

		long totalRowTime = 0, totalPixelTime = 0;

		// ==== RENDERING CODE ====

 		long start = System.nanoTime();
		int row;
		for (row = 0; row < (config.optimized ? config.height / 2 + 1 : config.height); ++row) {
			System.out.print(config.usePercent ? new StringBuilder("\rCalculating... ").append(Math.round(row / (double) config.height * 100)).append("%  ").toString() : new StringBuilder("\rCalculating... ").append(row + (config.optimized ? 0 : 1)).append("/").append(config.height / (config.optimized ? 2 : 1)).append("  ").toString());
			long rowStart = System.nanoTime();
			final double imaginaryCoordinate = (row - config.height / 2.0) * 4.0 / config.width;
			int[] scanline = new int[(int) (config.width * 3)];
			pixels: for (int pixel = 0; pixel < config.width; ++pixel) {
				System.out.print(new StringBuilder().append("\b").append(config.spinner.next()).toString());
				long pixelStart = System.nanoTime();
				final double real_c = (pixel - config.width / 2.0) * 4.0 / config.width;

				double x = 0, y = 0;
				long iterations = 0;

				while (Math.pow(x, 2) + Math.pow(y, 2) < 4.0 && iterations < config.max_iterations) {
					double x_new = Math.pow(x, 2) - Math.pow(y, 2) + real_c;
					y = 2.0 * x * y + imaginaryCoordinate;
					x = x_new;
					++iterations;
					if (iterations >= config.max_iterations) {
						scanline[pixel * 3] = scanline[pixel * 3 + 1] = scanline[pixel * 3 + 2] = 0;
						continue pixels;
					}
				}

//				double zn = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
//				double nsmooth = iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0);

//				Color c = Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1);
//				_image.setRGB(pixel, row, Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1).getRGB());

				Color color = Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1);

				scanline[pixel * 3] = color.getRed();
				scanline[pixel * 3 + 1] = color.getGreen();
				scanline[pixel * 3 + 2] = color.getBlue();

				long pixelEnd = System.nanoTime();
				totalPixelTime += pixelEnd - pixelStart;
			}
			if (config.optimized && row < config.height / 2) scanlines[row] = scanline;
			config.writer.writeRow(new ImageLineInt(config.info, scanline));
			long rowEnd = System.nanoTime();
			totalRowTime += rowEnd - rowStart;
		}


		if (config.optimized) {
			ArrayUtils.reverse(scanlines);
			for (int[] scanline : Arrays.copyOfRange(scanlines, 0, scanlines.length - 1)) {
				config.writer.writeRow(new ImageLineInt(config.info, scanline));
			}
		}

		long end = System.nanoTime();

		System.out.println("\b \nFinished in " + Math.round((end - start) / 1000000d) / 1000d + "s");
		System.out.println("Average row time: " + Math.round((totalRowTime / (double) config.height) / 1000d) / 1000d + "ms");
		System.out.println("Average pixel time: " + Math.round((totalPixelTime / (double) (config.width * config.height))) / 1000d + "µs");


		config.writer.close();

		System.out.println("\nSaved to " + config.filename);
	}
}
