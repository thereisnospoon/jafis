package diploma;

import diploma.matching.FingerprintsDatabase;
import diploma.matching.Matcher;
import diploma.model.Finger;
import diploma.model.Fingerprint;
import diploma.ui.MainFrame;

import java.io.File;
import java.util.*;

public class Tester {

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

		FingerprintsDatabase db = formDB("C:\\Users\\nagrizolich\\Desktop\\trainingSample");
		db.saveDB("db");
//		List<Fingerprint> fingerprints = extractFeatures("C:\\Users\\nagrizolich\\Desktop\\test");
//
//		double threshold = 1;
//		for (int i = 0; i < 9; i++) {
//			System.out.println(performTest(db, fingerprints, threshold));
//			threshold -= 0.2;
//		}
	}
}
