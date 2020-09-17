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

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class Main { // todo: make oop or something i dunno

	//

	public static void main(String[] _args) {
		System.out.println("Initializing...");

		final String[] args;
		final String filename;
		final double width, height, max_iterations;
		// This stores the entire first half of the mandelbrot in memory and flips it around for the second time,
		// drastically increasing memory usage but halving the required time.
		// edit: it's like 25% more
		final boolean halveSpeed;

		// ==== CONFIG CODE ====

		if (_args.length == 3) _args = new String[]{_args[0], _args[1], _args[2], "false"};
		if (halveSpeed = Boolean.parseBoolean(_args[3])) {
			_args[3] = _args[4];
			args = Arrays.copyOfRange(_args, 0, 4);
		} else args = _args;

		if (args.length == 4) filename = args[3].endsWith(".png") ? args[3] : args[3] + ".png";
		else if (args.length == 3) filename = "image.png";
		else {
			die(args);
			return;
		}

		{
			int[] conf = new int[3];
			for (int i = 0; i < 3; ++i) {
				try {
					conf[i] = Integer.parseInt(args[i]);
					assert conf[i] > 0;
				} catch (NumberFormatException | AssertionError e) {
					die(args);
					return;
				}
			}
			width = conf[0];
			height = conf[1];
			max_iterations = conf[2];
		}

		// ==== INIT WRITING VARS ====

		final ImageInfo imageInfo = new ImageInfo((int) width,
				(int) height,
				8, false);


		PngWriter image = new PngWriter(new File(filename), imageInfo, true);

		final Spinner<Character> spinner = new Spinner<>('|', '/', '—', '\\');

		final int[][] scanlines;

		if (halveSpeed) scanlines = new int[(int) height / 2][(int) width * 3];
		else scanlines = new int[0][]; // don't actually need this but java (or me) is stupid

		long totalRowTime = 0, totalColTime = 0;

		// ==== RENDERING CODE ====

 		long start = System.nanoTime();
		int row;
		for (row = 0; row < (halveSpeed ? height / 2 + 1 : height); ++row) {
//			System.out.print(new StringBuilder().append("\rCalculating... ").append(Math.round(row / height * 100)).append("%  ").toString());
			System.out.print(new StringBuilder().append("\rCalculating... ").append(row + (halveSpeed ? 0 : 1)).append("/").append((int) height / (halveSpeed ? 2 : 1)).append("  ").toString());
			long rowStart = System.nanoTime();
			final double imaginary_c = (row - height / 2.0) * 4.0 / width;
			int[] scanline = new int[(int) (width * 3)];
			for (int col = 0; col < width; ++col) {
				System.out.print(new StringBuilder().append("\b").append(spinner.next()).toString());
				long colStart = System.nanoTime();
				final double real_c = (col - width / 2.0) * 4.0 / width;

				double x = 0, y = 0;
				long iterations = 0;

				while (Math.pow(x, 2) + Math.pow(y, 2) < 4.0 && iterations < max_iterations) {
					double x_new = Math.pow(x, 2) - Math.pow(y, 2) + real_c;
					y = 2.0 * x * y + imaginary_c;
					x = x_new;
					++iterations;
				}

				if (iterations < max_iterations) {
//					double zn = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
//					double nsmooth = iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0);

//					Color c = Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1);
//					_image.setRGB(col, row, Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1).getRGB());

					Color color = Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1);

					scanline[col * 3] = color.getRed();
					scanline[col * 3 + 1] = color.getGreen();
					scanline[col * 3 + 2] = color.getBlue();

				} else {
					scanline[col * 3] = scanline[col * 3 + 1] = scanline[col * 3 + 2] = 0;
				}
				long colEnd = System.nanoTime();
				totalColTime += colEnd - colStart;
			}
			if (halveSpeed && row < height / 2) scanlines[row] = scanline;
			image.writeRow(new ImageLineInt(imageInfo, scanline));
			long rowEnd = System.nanoTime();
			totalRowTime += rowEnd - rowStart;
		}


		if (halveSpeed) {
			ArrayUtils.reverse(scanlines);
			for (int[] scanline : Arrays.copyOfRange(scanlines, 0, scanlines.length - 1)) {
				image.writeRow(new ImageLineInt(imageInfo, scanline));
			}
		}

		long end = System.nanoTime();

		System.out.println("\b \nFinished in " + Math.round((end - start) / 1000000d) / 1000d + "s");
		System.out.println("Average row time: " + Math.round((totalRowTime / height) / 1000d) / 1000d + "ms");
		System.out.println("Average pixel time: " + Math.round((totalColTime / (width * height))) / 1000d + "µs");


		image.close();

		System.out.println("\nSaved to " + filename);
	}

	private static void die(String[] args) {
		System.out.println("Invalid Config: " + String.join(" ", args));
		System.exit(1);
	}
}

