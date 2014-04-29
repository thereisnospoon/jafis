package diploma;


import diploma.model.Fingerprint;
import diploma.preprocessing.Convolution;
import diploma.preprocessing.FrequencyFiled;
import diploma.preprocessing.OrientationField;
import ij.ImagePlus;

import java.util.Random;

import static diploma.CommonUtils.printSignalToFile;
import static diploma.CommonUtils.toDouble;
import static diploma.CommonUtils.transpose;

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

//		Fingerprint.extractFeatures("C:\\Users\\nagrizolich\\Desktop\\DB2_B\\107_2.tif");
	}
}
