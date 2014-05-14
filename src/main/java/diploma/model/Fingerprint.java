package diploma.model;

import diploma.CommonUtils;
import diploma.preprocessing.*;
import diploma.wavelets.Wavelet;
import diploma.wavelets.Wavelet.Subband;
import ij.ImagePlus;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Fingerprint implements Serializable {

	public static final int PROCESSING_BLOCK_SIZE = 17;
	public static final int SMOOTHING_BORDER_OFFSET = 1;
	public static final int SIZE_OF_ROI = 8;

	private Map<Feature,Double> featureVector;
	private transient double[][] pixels;
	private String imagePath;
	private OrientationField orientationField;
	private Pair<Integer,Integer> startROIBlock;

	private Fingerprint() {}

	public static Fingerprint extractFeatures(String imagePath) {

		Fingerprint fingerprint = new Fingerprint();
		fingerprint.imagePath = imagePath;

		ImagePlus imagePlus = new ImagePlus(imagePath);
		fingerprint.pixels = CommonUtils.transpose(CommonUtils.toDouble(imagePlus.getProcessor().getFloatArray()));

		//pre-processing
		OrientationField orientationField = new OrientationField(fingerprint.pixels);
		fingerprint.orientationField = orientationField;

		System.out.println("Orientation field calculation and smoothing");

		orientationField.calculate(PROCESSING_BLOCK_SIZE);
		Pair<double[][],double[][]> smoothedField = orientationField.smoothField(SMOOTHING_BORDER_OFFSET);

		System.out.println("Core point detection");

		Pair<Double,Double> corePoint = OrientationField.getCorePoint(smoothedField.getLeft());
		fingerprint.startROIBlock = getStartROIBlock(corePoint).getLeft();
		double[][] smoothedOrientationField = mergeFields(orientationField.getOrientationField(), smoothedField.getLeft());

		System.out.println("Local frequency calculation");

		Map<Pair<Integer,Integer>,Double> roiFrequencies = calculateROIFrequencies(fingerprint.pixels, smoothedOrientationField, fingerprint.startROIBlock);

		System.out.println("Filtering");

		double[][] filteredROI = getGaborFilteredROI(fingerprint.pixels, smoothedOrientationField, roiFrequencies, fingerprint.startROIBlock);

		System.out.println("Wavelet transformation and feature extraction");

		Wavelet wavelet = Wavelet.Haar;
		Map<Subband,double[][]> transformedROI = wavelet.transform(filteredROI);
		fillFeatureArray(fingerprint, transformedROI);

		return fingerprint;
	}

	private static void fillFeatureArray(Fingerprint fingerprint, Map<Subband,double[][]> waveletTransformResult) {

		fingerprint.featureVector = new HashMap<>();
		fingerprint.featureVector.put(Feature.LH_Variance, new Variance().evaluate(CommonUtils.toOneDim(waveletTransformResult.get(Subband.LH))));
		fingerprint.featureVector.put(Feature.HL_Variance, new Variance().evaluate(CommonUtils.toOneDim(waveletTransformResult.get(Subband.HL))));
		fingerprint.featureVector.put(Feature.HH_Variance, new Variance().evaluate(CommonUtils.toOneDim(waveletTransformResult.get(Subband.HH))));
	}

	private static double[][] getGaborFilteredROI(double[][] pixels, double[][] orientationField,
												  Map<Pair<Integer,Integer>,Double> frequencies, Pair<Integer,Integer> cornerBlock) {

		int startBlockRow = cornerBlock.getLeft();
		int startBlockColumn = cornerBlock.getRight();
		double[][] filteredROI = new double[PROCESSING_BLOCK_SIZE*SIZE_OF_ROI][PROCESSING_BLOCK_SIZE*SIZE_OF_ROI];

		SegmentedImage segmentedImage = new SegmentedImage(PROCESSING_BLOCK_SIZE, pixels);
		for (Pair<Integer,Integer> block : frequencies.keySet()) {

			int blockRow = block.getLeft();
			int blockColumn = block.getRight();
			GaborFilter gaborFilter = new GaborFilter(frequencies.get(block), orientationField[blockRow][blockColumn]
					+ Math.PI/2, 0, 0.5, 2);

			double[][] filteredBlockPixels = Convolution.convolve(segmentedImage.getSegment(blockRow,blockColumn).getPixels(),
					gaborFilter.getKernel());

			for (int i = 0; i < PROCESSING_BLOCK_SIZE; i++) {
				for (int j = 0; j < PROCESSING_BLOCK_SIZE; j++) {

					int roiRow = PROCESSING_BLOCK_SIZE*(blockRow - startBlockRow) + i;
					int roiColumn = PROCESSING_BLOCK_SIZE*(blockColumn - startBlockColumn) + j;
					filteredROI[roiRow][roiColumn] = filteredBlockPixels[i][j];
					// without filtering
//					filteredROI[roiRow][roiColumn] = segmentedImage.getSegment(blockRow,blockColumn).getPixels()[i][j];
				}
			}
		}

		return filteredROI;
	}

	/**
	 * Returns orientation field with the same size as source field but with values of smoothed field (if it's not
	 * source field borders)
	 */
	private static double[][] mergeFields(double[][] sourceOrientationField, double[][] smoothedField) {

		int n = sourceOrientationField.length;
		int m = sourceOrientationField[0].length;
		double[][] mergedField = new double[n][m];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				if (i < SMOOTHING_BORDER_OFFSET || i >= n - SMOOTHING_BORDER_OFFSET || j < SMOOTHING_BORDER_OFFSET
						|| j >= m - SMOOTHING_BORDER_OFFSET) {

					mergedField[i][j] = sourceOrientationField[i][j];
				} else {
					mergedField[i][j] = smoothedField[i - SMOOTHING_BORDER_OFFSET][j - SMOOTHING_BORDER_OFFSET];
				}
			}
		}
		return mergedField;
	}

	/**
	 * Calculates coordinates of left upper border block of ROI
	 * @param corePoint	estimated core point which was calculated from some smoothed orientation field (in blocks)
	 * @return	coordinates of left upper border block of ROI (first pair) and actual estimated core point (in pixels)
	 */
	private static Pair<ImmutablePair<Integer,Integer>,ImmutablePair<Integer,Integer>> getStartROIBlock(Pair<Double,Double> corePoint) {

		int row = SMOOTHING_BORDER_OFFSET + (int) corePoint.getLeft().doubleValue();
		int column = SMOOTHING_BORDER_OFFSET + (int) corePoint.getRight().doubleValue();

		int startRowOfROI = row - SIZE_OF_ROI/2 + SMOOTHING_BORDER_OFFSET;
		int startColumnOfROI = column - SIZE_OF_ROI/2 + SMOOTHING_BORDER_OFFSET;

		int corePointRow = (startRowOfROI + SIZE_OF_ROI/2)*PROCESSING_BLOCK_SIZE;
		int corePointColumn = (startColumnOfROI + SIZE_OF_ROI/2)*PROCESSING_BLOCK_SIZE;

		if (startRowOfROI < 0 || startColumnOfROI < 0) {
			throw new IllegalStateException("Cannot crop sufficient area as ROI");
		}

		return new ImmutablePair<>(new ImmutablePair<>(startRowOfROI,startColumnOfROI),
				new ImmutablePair<>(corePointRow,corePointColumn));
	}

	/**
	 * Calculates local ridge frequency for each block in ROI
	 * @param pixels			pixels of image
	 * @param orientationField	evidently
	 * @param roiCorner			coordinates of left upper ROI block
	 * @return					returns Maps with ROI blocks coordinates and correspondent local frequency
	 */
	private static Map<Pair<Integer,Integer>,Double> calculateROIFrequencies(double[][] pixels,
																						   double[][] orientationField,
																						   Pair<Integer,Integer> roiCorner) {

		Map<Pair<Integer,Integer>,Double> frequencies = new HashMap<>();
		for (int i = roiCorner.getLeft(); i < roiCorner.getLeft() + SIZE_OF_ROI; i++) {
			for (int j = roiCorner.getRight(); j < roiCorner.getRight() + SIZE_OF_ROI; j++) {

				int blockCenterRow = (int) (PROCESSING_BLOCK_SIZE*(i + 0.5));
				int blockCenterColumn = (int) (PROCESSING_BLOCK_SIZE*(j + 0.5));

				double frequency;
				if (i == 0 || j == 0 || i == orientationField.length - 1 || j == orientationField[0].length - 1) {

					frequency = FrequencyFiled.getPeriod(pixels, blockCenterRow, blockCenterColumn, 15,15,
							orientationField[i][j]);
				} else {
					frequency = FrequencyFiled.getPeriod(pixels, blockCenterRow, blockCenterColumn, 31,31,
							orientationField[i][j]);
				}
				frequencies.put(new ImmutablePair<>(i,j), frequency);
			}
		}
		return frequencies;
	}

	public double[] getFeatureValues() {
		return Feature.getFeatureValues(featureVector);
	}

	public String getImagePath() {
		return imagePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Fingerprint that = (Fingerprint) o;

		if (!imagePath.equals(that.imagePath)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return imagePath.hashCode();
	}

	@Override
	public String toString() {

		String[] split = imagePath.split("\\W");
		return split[split.length - 2] + "." + split[split.length - 1];
	}
}
