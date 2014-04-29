package diploma;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import org.apache.commons.lang3.ArrayUtils;

import java.io.PrintWriter;
import java.util.List;

public class CommonUtils {

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
}
