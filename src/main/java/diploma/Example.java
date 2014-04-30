package diploma;


import diploma.matching.Matcher;
import diploma.model.Feature;
import diploma.model.Fingerprint;
import diploma.preprocessing.Convolution;
import diploma.preprocessing.FrequencyFiled;
import diploma.preprocessing.OrientationField;
import ij.ImagePlus;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

		Fingerprint fp1 = Fingerprint.extractFeatures("C:\\Users\\dmde0313\\Google Drive\\Diploma docs\\DB2_B\\103_6.tif");
		Fingerprint fp2 = Fingerprint.extractFeatures("C:\\Users\\dmde0313\\Google Drive\\Diploma docs\\DB2_B\\102_7.tif");
		Fingerprint fp3 = Fingerprint.extractFeatures("C:\\Users\\dmde0313\\Google Drive\\Diploma docs\\DB2_B\\102_1.tif");
		Fingerprint fp = Fingerprint.extractFeatures("C:\\Users\\dmde0313\\Google Drive\\Diploma docs\\DB2_B\\103_2.tif");

		System.out.println(Matcher.getNearestFingerprint(Arrays.asList(fp1, fp2, fp3), fp).getImagePath());
	}
}
