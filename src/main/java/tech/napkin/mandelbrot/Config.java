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

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngWriter;
import com.beust.jcommander.*;
import com.beust.jcommander.converters.BigDecimalConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


class Config {

	public final Spinner<Character> spinner = new Spinner<>('|', '/', '\u2014', '\\');

	public final int width, height, max_iterations, verbosity;
	public final BigDecimal zoom, real, imaginary;
	public final boolean usePercent, optimized;
	public final String filename;
	public final ImageInfo info;
	public final PngWriter writer;

	Config (String[] args) {
		if (args.length == 0) args = new String[]{"--help"};
		JCommander jct = new JCommander();
		jct.addObject(new Args());
		jct.parse(args);

		if (Args.showHelp) {
			StringBuilder sb = new StringBuilder();
			new UnixStyleUsageFormatter(jct).usage(sb);
			System.out.println(sb.toString());
			System.exit(0);
		}

		if (Args.showVersion) {
			System.out.println("Mandelbrot Generator version @VERSION@. " + // replaced by gradle on compilation
					"Created by SirNapkin1334.\nThe source code is available at " +
					"https://github.com/SirNapkin1334/Mandelbrot");
			System.exit(0);
		}

		if (Args.showCopyright) {
			System.out.println(new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream("/COPYRIGHT"))
			).lines().collect(Collectors.joining("\n")));
			System.exit(0);
		}


		if (Args.imageParams.size() != 3) {
			throw new ParameterException("Number of image parameters must be 3: width, height, iterations!");
		}


		this.verbosity = Args.verbose3 ? 3 : Args.verbose2 ? 2 : Args.verbose1 ? 1 : 0;

		this.width = Args.imageParams.get(0);
		this.height = Args.imageParams.get(1);
		this.max_iterations = Args.imageParams.get(2);
		this.zoom = Args.zoom;
		this.real = Args.real;
		this.imaginary = Args.imaginary;
		this.usePercent = Args.usePercent;
		this.optimized = !Args.notSpeedOptimized;
		this.filename = Args.filename.endsWith(".png") ?
				Args.filename : new StringBuilder(Args.filename).append(".png").toString();

		this.info = new ImageInfo(this.width, this.height, 8, false);
		this.writer = new PngWriter(new File(this.filename), this.info, true);

	}

	@SuppressWarnings("FieldMayBeFinal")
	private static class Args {

		@Parameter(names = "--help", help = true, description = "Shows the help menu and exits.")
		private static boolean showHelp;

		@Parameter(names = "--version", description = "Prints program info and version and exists.")
		private static boolean showVersion;

		@Parameter(names = "--copyright", description = "Prints copyright/licensing info and exits.")
		private static boolean showCopyright;


		@Parameter(names = "-v", description = "Set verbosity level. The more th is flag is included, the more verbose.")
		private static boolean verbose1;

		@Parameter(names = "-vv", hidden = true)
		private static boolean verbose2;

		@Parameter(names = "-vvv", hidden = true)
		private static boolean verbose3;


		@Parameter
		private static List<Integer> imageParams = new ArrayList<>();

		@Parameter(names = {"-f", "--filename"})
		private static String filename = "image.png";

		@Parameter(names = {"-O", "--no-optimize"}, description = "Disables speed-optimized mode. Changing the imaginary coordinate will invoke this.")
		private static boolean notSpeedOptimized;

		@Parameter(names = {"-p", "--percent"}, description = "If used, the completeness will be shown in percent of lines, rather than the amount of lines completed.")
		private static boolean usePercent = false;

		@Parameter(names = {"-z", "--zoom"}, description = "If included, this will be the applied zooming amount.", converter = BigDecimalConverter.class)
		private static BigDecimal zoom = new BigDecimal("1.0");

		@Parameter(names = {"-r", "--real-coordinate"}, description = "The real coordinate that will be focused.", converter = BigDecimalConverter.class)
		private static BigDecimal real = new BigDecimal("0.0");

		@Parameter(names = {"-i", "--imaginary-coordinate"}, description = "The imaginary coordinate that will be focused.", converter = BigDecimalConverter.class, validateWith = CheckImaginaryCoordinateIsZero.class)
		private static BigDecimal imaginary = new BigDecimal("0.0");


		public static class CheckImaginaryCoordinateIsZero implements IParameterValidator {

			private final static Pattern pattern = Pattern.compile("^0(?:\\.0+)$");

			@Override
			public void validate(String name, String value) throws ParameterException {
				if (!pattern.matcher(value).matches()) Args.notSpeedOptimized = true;
			}
		}

	}

}
