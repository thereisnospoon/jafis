package diploma.matching;

import diploma.model.Fingerprint;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.ml.distance.CanberraDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matcher {

	public static final EuclideanDistance EUCLIDEAN_DISTANCE = new EuclideanDistance();
	public static final CanberraDistance CANBERRA_DISTANCE = new CanberraDistance();

	public static Map<Fingerprint,Double> getDistances(List<Fingerprint> fpBase, Fingerprint fingerprint, DistanceMeasure distanceMeasure) {

		Map<Fingerprint,Double> distances = new HashMap<>();
		for (Fingerprint fpFromBase : fpBase) {
			distances.put(fpFromBase, distanceMeasure.compute(fpFromBase.getFeatureValues(), fingerprint.getFeatureValues()));
		}
		return distances;
	}

	public static Fingerprint getNearestFingerprint(List<Fingerprint> fpBase, Fingerprint fingerprint) {

		Map<Fingerprint,Double> distances = getDistances(fpBase, fingerprint, CANBERRA_DISTANCE);

		Fingerprint nearest = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Fingerprint fpFromBase : fpBase) {

			if (distances.get(fpFromBase) < minDistance) {
				minDistance = distances.get(fpFromBase);
				nearest = fpFromBase;
			}
		}
		return nearest;
	}
}
