package diploma;


import diploma.preprocessing.FrequencyFiled;
import diploma.preprocessing.GaborFilter;
import diploma.preprocessing.OrientationField;
import diploma.preprocessing.SegmentedImage;
import diploma.wavelets.Wavelet;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static diploma.CommonUtils.*;

public class Example {



	private static double[] generateSignal(int n) {

		Random r = new Random(System.nanoTime());
		double[] signal = new double[n];
		double h = 2*Math.PI/n;
		for (int i = 0; i < n; i++) {
			signal[i] = Math.sin(i*h) + 0.2*r.nextDouble();
		}
		return signal;
	}

	public static void main(String[] args) {

		ImagePlus imagePlus = new ImagePlus("C:\\Users\\nagrizolich\\Desktop\\DB2_B\\108_2.tif");
		OrientationField orientationField = new OrientationField(imagePlus);
		double[][] smoothedField = orientationField.calculate(17).smoothField(1).getLeft();
		Pair<Double,Double> corePoint = OrientationField.getCorePoint(smoothedField);
		System.out.println((2 + corePoint.getLeft())*17 + " " + (2 + corePoint.getRight())*17);
	}
}
