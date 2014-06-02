package diploma;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class CommonUtils {

	public static ImagePlus resize(ImagePlus imagePlus, int width, int heigth) {

		ImageProcessor imageProcessor = imagePlus.getProcessor().resize(width,heigth);
		return new ImagePlus("new", imageProcessor);
	}

	public static float[][] toFloat(double[][] a) {

		float[][] b = new float[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				b[i][j] = (float) a[i][j];
			}
		}
		return b;
	}

	public static float[][] toFloat(int[][] a) {

		float[][] b = new float[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				b[i][j] = a[i][j];
			}
		}
		return b;
	}

	public static double[][] toDouble(float[][] a) {

		double[][] b = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				b[i][j] = a[i][j];
			}
		}
		return b;
	}

	public static void printSignalToFile(double[] signal, String fileName) {

		try {
			PrintWriter printWriter = new PrintWriter(fileName);
			for (double s: signal) {
				printWriter.format("%f ", s);
			}
			printWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void printSignalToFile(double[][] signal, String fileName) {

		try {
			PrintWriter printWriter = new PrintWriter(fileName);
			for (double[] row: signal) {
				for (double s: row) {
					printWriter.format("%.3f ", s);
				}
				printWriter.println();
			}
			printWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static double[][] transpose(double[][] a) {

		double[][] b = new double[a[0].length][a.length];
		for (int i = 0; i < a.length; i++) {
			arrayToColumn(b, a[i], i);
		}
		return b;
	}

	public static double mean(double[][] a) {

		double res = 0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				res += a[0][j];
			}
		}
		return res/a.length/a[0].length;
	}

	public static double[] columnToArray(double[][] x, int i) {

		double[] y = new double[x.length];
		for (int k = 0; k < x.length; k++) {
			y[k] = x[k][i];
		}
		return y;
	}

	public static void arrayToColumn(double[][] a, double[] x, int i) {

		for (int k = 0; k < x.length; k++) {
			a[k][i] = x[k];
		}
	}

	public static double[] toOneDim(double[][] a) {

		double[] b = new double[a.length*a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {

				b[i*a[0].length + j] = a[i][j];
			}
		}
		return b;
	}

	public static double variance(double[][] a) {

		double m = 0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				m += a[i][j];
			}
		}
		m /= a[0].length*a.length;

		double res = 0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				res += Math.pow(m - a[i][j], 2);
			}
		}
		return res/a[0].length/a.length;
	}

	public static void showImage(double[][] pixels) {

		ImagePlus image = new ImagePlus("image", new FloatProcessor(toFloat(pixels)));
		image.show();
	}

	public static void printToFile(String s, String fileName) {

		try {
			PrintWriter printWriter = new PrintWriter(fileName);
			printWriter.print(s);
			printWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static double[] toArray(List<Double> list) {

		return ArrayUtils.toPrimitive(list.toArray(new Double[0]));
	}

	public static Pair<Integer,Integer> getROIStart(Set<Pair<Integer,Integer>> roi) {

		int minRow = 0, minColumn = 0;
		for (Pair<Integer,Integer> cell: roi) {
			if (cell.getLeft() < minRow) {
				minRow = cell.getLeft();
			}
			if (cell.getRight() < minColumn) {
				minColumn = cell.getRight();
			}
		}
		return new ImmutablePair<>(minRow,minColumn);
	}

	public static String toString(double[][] a) {

		StringBuilder sb = new StringBuilder();
		for (double[] row : a) {

			for (double value: row) {
				sb.append(String.format("%3.2f ", value));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public static double[][] subArray(double[][] a, int startRow, int startColumn, int size) {

		double[][] b = new double[size][size];
		for (int i = startRow; i < startRow + size; i++) {
			for (int j = startColumn; j < startColumn + size; j++) {
				b[i - startRow][j - startColumn] = a[i][j];
			}
		}
		return b;
	}
}
