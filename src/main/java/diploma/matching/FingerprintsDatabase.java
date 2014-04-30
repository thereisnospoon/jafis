package diploma.matching;

import diploma.model.Fingerprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FingerprintsDatabase implements Serializable {

	public static FingerprintsDatabase loadExistent(String path) throws Exception {
		return  (FingerprintsDatabase) new ObjectInputStream(new FileInputStream(path)).readObject();
	}

	private List<Fingerprint> fingerprints;
	private transient String sourceImagesFolder;

	public FingerprintsDatabase(String sourceImagesFolder) {
		this.sourceImagesFolder = sourceImagesFolder;
	}

	public void extractFeatures() {

		fingerprints = new ArrayList<>();

		try {
			File folder = new File(sourceImagesFolder);
			File[] imageFiles = folder.listFiles();
			for (File imageFile : imageFiles) {

				Fingerprint fingerprint = Fingerprint.extractFeatures(imageFile.getName());
				fingerprints.add(fingerprint);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Fingerprint> getFingerprints() {
		return fingerprints;
	}
}
