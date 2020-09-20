package tech.napkin.mandelbrot;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngWriter;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BigDecimalConverter;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


class Config {

	public final Spinner<Character> spinner = new Spinner<>('|', '/', '\u2014', '\\');

	public final int width, height, max_iterations;
	public final BigDecimal zoom, real, imaginary;
	public final boolean usePercent, optimized;
	public final String filename;
	public final ImageInfo info;
	public final PngWriter writer;

	Config (String[] args) {
		if (args.length == 0) args = new String[]{"--help"};
		JCommander jct = JCommander.newBuilder().addObject(new Args()).build();
		jct.parse(args);

		if (Args.showVersion) {
			System.out.println("Mandelbrot Generator version @VERSION@. Created by SirNapkin1334. Licensed under GNU " +
					"GPLv3.\nThe source code is available at https://github.com/SirNapkin1334/Mandelbrot");
			System.exit(0);
		}

		if (Args.help) {
			StringBuilder sb = new StringBuilder();
			jct.getUsageFormatter().usage(sb); // todo: use custom IUsageFormatter instead of this nonsense
			System.out.println(sb.toString().replaceAll("\n +Default: false\n", "\n").replaceAll("( {6}.*)\n +(Default: .+)\n", "$1 $2\n"));
			System.exit(0);
		}

		if (Args.imageParams.size() != 3) {
			throw new ParameterException("Number of image parameters must be 3: width, height, iterations!");
		}

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

		@Parameter(names = {"-h", "--help"}, help = true, description = "Shows the help menu and exits.")
		private static boolean help; // todo: figure out why it doesn't print help menu

		@Parameter(names = {"-v", "--version"}, description = "Prints program info and version and exists.")
		private static boolean showVersion;

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
