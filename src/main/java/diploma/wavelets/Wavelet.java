package diploma.wavelets;

import diploma.CommonUtils;
import diploma.preprocessing.Convolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Wavelet {

	Haar(new double[]{0.707106781186548, 0.707106781186548}, new double[]{-0.707106781186548, 0.707106781186548}),
	db2(new double[]{-0.129409522550921, 0.224143868041857, 0.836516303737469, 0.482962913144690},
			new double[]{-0.482962913144690, 0.836516303737469, -0.224143868041857, -0.129409522550921}),
	db10(
			new double[]{-1.32642030023549e-5, 9.35886700010899e-5, -0.000116466854994386, -0.000685856695004683, 
				0.00199240529499085, 0.00139535174699408, -0.0107331754829796, 0.00360655356698839, 0.0332126740589332
				,-0.0294575368219457, -0.0713941471658608, 0.0930573646038066, 0.127369340335743, -0.195946274376597, 
				-0.249846424326489, 0.281172343660427, 0.688459039452592, 0.527201188930920, 0.188176800077621, 0.0266700579009508},
			
			new double[]{-0.0266700579009508, 0.188176800077621, -0.527201188930920, 0.688459039452592, 
					-0.281172343660427, -0.249846424326489, 0.195946274376597, 0.127369340335743, -0.0930573646038066, 
					-0.0713941471658608, 0.0294575368219457, 0.0332126740589332, -0.00360655356698839, -0.0107331754829796,
					-0.00139535174699408, 0.00199240529499085, 0.000685856695004683, -0.000116466854994386, 
					-9.35886700010899e-5, -1.32642030023549e-5}
			);

	private double[] hiFilter, loFilter;

	Wavelet(double[] loFilter, double[] hiFilter) {
		this.loFilter = loFilter;
		this.hiFilter = hiFilter;
	}

	public double[][] transform(double[] signal) {

		if (signal.length % 2 != 0) {
			throw new IllegalArgumentException("Signal length must be even");
		}

		double[][] decomposition = new double[2][signal.length/2];
		decomposition[0] = downSample(Convolution.convolve(signal, loFilter));
		decomposition[1] = downSample(Convolution.convolve(signal, hiFilter));
		return decomposition;
	}

	public List<double[][]> transform(double[][] signal) {

		if (signal.length % 2 != 0 || signal[0].length % 2 != 0) {
			throw new IllegalArgumentException("Signal length must be even");
		}

		List<double[][]> transformedRows = rowsTransform(signal);
		List<double[][]> twoDimTransformRes = new ArrayList<>(4);
		twoDimTransformRes.addAll(columnsTransform(transformedRows.get(0)));
		twoDimTransformRes.addAll(columnsTransform(transformedRows.get(1)));
		return twoDimTransformRes;
	}

	private List<double[][]> rowsTransform(double[][] signal) {

		double[][] lo = new double[signal.length][];
		double[][] hi = new double[signal.length][];

		for (int i = 0; i < signal.length; i++) {
			double[][] y = transform(signal[i]);
			lo[i] = y[0];
			hi[i] = y[1];
		}
		return Arrays.asList(lo, hi);
	}

	private List<double[][]> columnsTransform(double[][] x) {

		double[][] lo = new double[x.length/2][x[0].length];
		double[][] hi = new double[x.length/2][x[0].length];

		for (int i = 0; i < x[0].length; i++) {
			double[][] y = transform(CommonUtils.columnToArray(x, i));
			CommonUtils.arrayToColumn(lo, y[0], i);
			CommonUtils.arrayToColumn(hi, y[1], i);
		}
		return Arrays.asList(lo, hi);
	}

	private double[] downSample(double[] x) {

		double[] y = new double[x.length/2];
		for (int i = 0; i < x.length; i+=2) {
			y[i/2] = x[i];
		}
		return y;
	}
}