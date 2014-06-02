package diploma;

import diploma.matching.FingerprintsDatabase;
import diploma.matching.Matcher;
import diploma.model.Feature;
import diploma.model.Finger;
import diploma.model.Fingerprint;

import java.io.File;
import java.util.*;

public class DBCreator {

	private static FingerprintsDatabase formDB(String trainingSamplePath) {

		FingerprintsDatabase fingerprintsDatabase = new FingerprintsDatabase();
		Set<Finger> fingers = new HashSet<>();
		for (File file : new File(trainingSamplePath).listFiles()) {

			System.out.println("Working with " + file.getName());

			String fileName = file.getName();
			String[] parts = fileName.split("\\D+");
			Finger finger = new Finger(parts[0]);
			if (!fingers.contains(finger)) {
				fingerprintsDatabase.addFinger(finger);
				fingers.add(finger);
			}
			fingerprintsDatabase.addFingerprintToFinger(finger, Fingerprint.extractFeatures(file.getAbsolutePath()));
		}
		return fingerprintsDatabase;
	}

	private static List<Fingerprint> extractFeatures(String testSamplePath) {

		File[] testFiles = new File(testSamplePath).listFiles();
		List<Fingerprint> fingerprints = new LinkedList<>();

		for (File file : testFiles) {

			System.out.println("Working with " + file.getName());

			fingerprints.add(Fingerprint.extractFeatures(file.getAbsolutePath()));
		}
		return fingerprints;
	}

	private static String performTest(FingerprintsDatabase fingerprintsDatabase, List<Fingerprint> testSample,
									double threshold) {

		int matched = 0, unmatched = 0, wrongMatched = 0;
		for (Fingerprint fingerprint : testSample) {

//			System.out.println(Arrays.toString(fingerprint.getFeatureValues()));
			Fingerprint matchedFingerprint = Matcher.match(fingerprintsDatabase, fingerprint, threshold);

			if (matchedFingerprint == null) {
				unmatched++;
			} else if (matchedFingerprint.getId() == fingerprint.getId()) {
				matched++;
			} else {
				wrongMatched++;
			}
		}
		return threshold + " " + matched + " " + unmatched + " " + wrongMatched;
	}

	public static void main(String[] args) throws Exception {

		formDB("C:\\Users\\nagrizolich\\Desktop\\trainingSample").saveDB("db");
//		formDB("C:\\Users\\nagrizolich\\Desktop\\testSample").saveDB("db3test");

//		FingerprintsDatabase trainDB = FingerprintsDatabase.loadExistent("db3train");
//		FingerprintsDatabase testDB = FingerprintsDatabase.loadExistent("db3test");

//		double t = 0.01;
//		while (t <= 0.2) {
//			System.out.println(performTest(trainDB, testDB.getFingerprints(), t));
//			t += 0.01;
//		}
	}
}
