package diploma.matching;

import diploma.model.Finger;
import diploma.model.Fingerprint;

import java.io.*;
import java.util.*;

public class FingerprintsDatabase implements Serializable {

	public static FingerprintsDatabase loadExistent(String path) throws Exception {

		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path));
			FingerprintsDatabase fingerprintsDatabase = (FingerprintsDatabase) objectInputStream.readObject();
			objectInputStream.close();
			return fingerprintsDatabase;
		} catch (Exception e) {
			return new FingerprintsDatabase();
		}
	}

	private List<Fingerprint> fingerprints;
	private Map<Finger,List<Fingerprint>> fingerDb;

	public FingerprintsDatabase() {

		fingerprints = new ArrayList<>();
		fingerDb = new HashMap<>();
	}

//	public void extractFeatures() {
//
//		fingerprints = new ArrayList<>();
//
//		try {
//			File folder = new File(sourceImagesFolder);
//			File[] imageFiles = folder.listFiles();
//			for (File imageFile : imageFiles) {
//
//				Fingerprint fingerprint = Fingerprint.extractFeatures(imageFile.getName());
//				fingerprints.add(fingerprint);
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}

	public void addFinger(Finger finger) {
		fingerDb.put(finger, new LinkedList<Fingerprint>());
	}

	public void addFingerprintToFinger(Finger finger, Fingerprint fingerprint) {

		fingerDb.get(finger).add(fingerprint);
		fingerprints.add(fingerprint);
	}

	public void remove(Finger finger) {

		for (Fingerprint fingerprint : fingerDb.get(finger)) {
			fingerprints.remove(fingerprint);
		}
		fingerDb.remove(finger);
	}

	public void remove(Finger finger, Fingerprint fingerprint) {

		fingerprints.remove(fingerprint);
		fingerDb.get(finger).remove(fingerprint);
	}

	public List<Fingerprint> getFingerprints(Finger finger) {
		return fingerDb.get(finger);
	}

	public List<Fingerprint> getFingerprints() {
		return fingerprints;
	}

	public Map<Finger, List<Fingerprint>> getFingerDb() {
		return fingerDb;
	}

	public Finger getFinger(Fingerprint fingerprint) {

		for (Map.Entry<Finger,List<Fingerprint>> entry : fingerDb.entrySet()) {

			if (entry.getValue().contains(fingerprint)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void saveDB(String path) throws Exception {

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(path)));
		objectOutputStream.writeObject(this);
		objectOutputStream.close();
	}
}
