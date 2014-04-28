package diploma.model;

import diploma.CommonUtils;
import diploma.preprocessing.OrientationField;
import ij.ImagePlus;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class Fingerprint {

	public static final int PROCESSING_BLOCK_SIZE = 17;
	public static final int SMOOTHING_BORDER_OFFSET = 1;
	public static final int SIZE_OF_ROI = 8;

	private Map<Feature,Double> featureVector;
	private double[][] pixels;
	private String imagePath;
	private OrientationField orientationField;
	private Pair<Integer,Integer> startROIBlock;

	private Fingerprint() {}

	public static Fingerprint extractFeatures(String imagePath) {

		Fingerprint fingerprint = new Fingerprint();
		fingerprint.imagePath = imagePath;

		ImagePlus imagePlus = new ImagePlus(imagePath);
		fingerprint.pixels = CommonUtils.transpose(CommonUtils.toDouble(imagePlus.getProcessor().getFloatArray()));

		OrientationField orientationField = new OrientationField(fingerprint.pixels);
		fingerprint.orientationField = orientationField;
		orientationField.calculate(PROCESSING_BLOCK_SIZE);
		Pair<double[][],double[][]> smoothedField = orientationField.smoothField(SMOOTHING_BORDER_OFFSET);
		Pair<Double,Double> corePoint = OrientationField.getCorePoint(smoothedField.getLeft());
		fingerprint.startROIBlock = getStartROIBlock(corePoint).getLeft();

		return fingerprint;
	}

	/**
	 * Calculates coordinates of left upper border block of ROI
	 * @param corePoint	estimated core point (in blocks)
	 * @return	coordinates of left upper border block of ROI (first pair) and actual estimated core point (in pixels)
	 */
	public static Pair<ImmutablePair<Integer,Integer>,ImmutablePair<Integer,Integer>> getStartROIBlock(Pair<Double,Double> corePoint) {

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
}
