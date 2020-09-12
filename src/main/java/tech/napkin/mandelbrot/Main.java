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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		System.out.println("Initializing...");

		String filename;
		final double width, height, max_iterations;

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

		BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);

		long totalRowTime = 0, totalColTime = 0;

		long start = System.nanoTime();
		for (int row = 0; row < height; ++row) {
			System.out.print("\rCalculating... " + Math.round(row / height * 100) + "%");
			long rowStart = System.nanoTime();
			final double imaginary_c = (row - height / 2.0) * 4.0 / width;
			for (int col = 0; col < width; ++col) {
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
					image.setRGB(col, row, Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * (iterations + 1.0 - Math.log10(Math.log10(Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))))) / Math.log10(2.0))), 0.6f, 1).getRGB());

				}
				long colEnd = System.nanoTime();
				totalColTime += colEnd - colStart;
			}
			long rowEnd = System.nanoTime();
			totalRowTime += rowEnd - rowStart;
		}
		long end = System.nanoTime();

		System.out.println("\nFinished in " + Math.round((end - start) / 1000000d) / 1000d + "s");
		System.out.println("Average row time: " + Math.round((totalRowTime / height) / 1000d) / 1000d + "ms");
		System.out.println("Average pixel time: " + Math.round((totalColTime / (width * height))) / 1000d + "µs");


		try {
			assert ImageIO.write(image, "PNG", new File(filename));
		} catch (IOException | AssertionError e) {
			System.out.println("\nWriting image failed! Sorry :(");
			if (!(e instanceof AssertionError)) e.printStackTrace();
			System.exit(1);
			return;
		}

		System.out.println("\nSaved to " + filename);
	}

	private static void die(String[] args) {
		System.out.println("Invalid Config: " + String.join(" ", args));
		System.exit(1);
	}
}
