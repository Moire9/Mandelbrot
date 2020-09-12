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
				}
			}
			width = conf[0];
			height = conf[1];
			max_iterations = conf[2];
		}

		BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);

		for (int row = 0; row < height; ++row) {
			System.out.print("\rCalculating... " + Math.round(row / height * 100) + "%");
			final double imaginary_c = (row - height / 2.0) * 4.0 / width;
			for (int col = 0; col < width; ++col) {
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
					double zn = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
					double nsmooth = iterations + 1.0 - Math.log10(Math.log10(Math.abs(zn))) / Math.log10(2.0);

					Color c = Color.getHSBColor((float) Math.toRadians(0.95 + 10.0 * nsmooth), 0.6f, 1);
					image.setRGB(col, row, c.getRGB());

				}
			}
		}

		System.out.println("\nComplete! Saved to " + filename);

		File f = new File(filename);

		try {
			ImageIO.write(image, "PNG", f);
		} catch (IOException e) {
			System.out.println("Writing image failed! Sorry :(");
		}
	}

	private static void die(String[] args) {
		throw new RuntimeException("Invalid Config: " + String.join(" ", args));
	}
}
