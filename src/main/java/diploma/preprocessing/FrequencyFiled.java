package diploma.preprocessing;

import com.google.common.collect.Range;
import diploma.CommonUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.*;

import static java.lang.Math.*;

public class FrequencyFiled {

	private static double distance(double i,double j, double theta) {

		if (abs(PI/2 - theta) < 1e-4) {
			return i;
		}
		double norm = sqrt(1 + pow(tan(theta),2));
		return (i*tan(theta) - j)/norm;
	}

	public static Map<Integer,Pair<Integer,Integer>> getPixelAxis(double theta, int n) {

		if (n % 2 == 0) {
			throw new IllegalArgumentException("n should be odd");
		}
		int halfSize = (n - 1)/2;

		double[][] distances = new double[n][n];
		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances.length; j++) {
				double dist = distance(i - halfSize,j - halfSize,theta);
				distances[j][i] = (abs(dist) > 0.5 ? 0 : 1);
			}
		}
		List<Pair<Integer,Integer>> line = new ArrayList<>();
		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances.length; j++) {
				if (distances[j][i] > 0) {
					line.add(new ImmutablePair<>(i - halfSize, j - halfSize));
				}
			}
		}
		Collections.sort(line, new Comparator<Pair<Integer, Integer>>() {

			@Override
			public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {

				return o1.getLeft().compareTo(o2.getLeft());
			}
		});
		int centerIndex = line.indexOf(new ImmutablePair<>(0,0));
		Map<Integer,Pair<Integer,Integer>> result = new HashMap<>();
		result.put(0, line.get(centerIndex));
		for (int i = 1; i <= halfSize; i++) {
			result.put(i, line.get(centerIndex + i));
			result.put(-i, line.get(centerIndex - i));
		}
		return result;
	}

	public static List<Double> getSignature(double[][] image, int row, int column, int xSize, int ySize, double theta) {

		Map<Integer,Pair<Integer,Integer>> xAxis = getPixelAxis(theta, xSize);
		Map<Integer,Pair<Integer,Integer>> yAxis = getPixelAxis(theta + PI/2, ySize);
		List<Double> signature = new ArrayList<>();
		Range<Integer> rowsRange = Range.closed(0, image.length - 1);
		Range<Integer> columnsRange = Range.closed(0, image[0].length - 1);

		for (int i = -(xSize -1)/2; i <= (xSize - 1)/2; i++) {

			double value = 0;
			Pair<Integer,Integer> currentCenter = new ImmutablePair<>(xAxis.get(i).getLeft() + row,
					xAxis.get(i).getRight() + column);

			for (Pair<Integer,Integer> pair : yAxis.values()) {
				int rowCor = currentCenter.getLeft() + pair.getLeft();
				int colCor = currentCenter.getRight() + pair.getRight();
				if (!(rowsRange.contains(rowCor) && columnsRange.contains(colCor))) {
					value = 0;
					break;
				} else {
					value += image[rowCor][colCor];
				}
			}
			if (abs(value) > 1e-4) {
				signature.add(value);
			}
		}
		return signature;
	}

	public static double getPeriod(double[][] image, int row, int column, int xSize, int ySize, double theta) {

		List<Double> signature = getSignature(image, row, column, xSize, ySize, theta);
		double[] x = new double[xSize];
		for (int i = 0; i < x.length; i++) {
			x[i] = i + 1;
		}
		PolynomialSplineFunction function = new SplineInterpolator().interpolate(x, CommonUtils.toArray(signature));
		List<Double> interpolatedValues = new ArrayList<>();

		double value = 1;
		while (value <= x.length) {
			interpolatedValues.add(function.value(value));
			value += 0.1;
		}
		return getAvgPeakDistance(findPeaks(CommonUtils.toArray(interpolatedValues)))/10;
	}


	private static double getAvgPeakDistance(List<Pair<Double,Integer>> peaks) {

		List<Double> p = new LinkedList<>();
		for (Pair<Double,Integer> pair : peaks) {
			p.add(pair.getLeft());
		}

		double mean = (Collections.max(p) + Collections.min(p))/2;
		List<Pair<Double,Integer>> truePeaks = new ArrayList<>();
		for (Pair<Double,Integer> pair : peaks) {
			if (pair.getLeft() > mean) {
				truePeaks.add(pair);
			}
		}

		double avgDist = 0;
		for (int i = 0; i < truePeaks.size() - 1; i++) {
			avgDist += truePeaks.get(i + 1).getRight() - truePeaks.get(i).getRight();
		}
		return avgDist/(truePeaks.size() - 1);
	}

	private static List<Pair<Double,Integer>> findPeaks(double[] array) {

		List<Pair<Double,Integer>> peaks = new ArrayList<>();
		for (int i = 1; i < array.length - 2; i++) {
			if (array[i] > array[i - 1] && array[i] > array[i + 1]) {
				peaks.add(new ImmutablePair<>(array[i],i));
			}
		}
		return peaks;
	}
}