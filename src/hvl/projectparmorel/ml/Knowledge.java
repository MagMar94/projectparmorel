package hvl.projectparmorel.ml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the algorithms knowledge.
 * 
 * @author Angela Barriga Rodriguez, Magnus Marthinsen
 */
public class Knowledge {
	private String knowledgeFilePath = "././knowledge.properties";
	private ExperienceMap experience;
	
	private List<Integer> preferences;
	
	public Knowledge(List<Integer> preferences) {
		this.preferences = preferences;
		experience = loadKnowledge();
	}
	
	/**
	 * Loads the knowledge
	 */
	private ExperienceMap loadKnowledge() {
		ExperienceMap newExperience = new ExperienceMap();
		ExperienceMap oldExperience = loadKnowledgeFromFile();
		if (oldExperience.getActionsDictionary().size() > 0) {
			// copy structure of qtable with values to 0
			newExperience.setqTable(normalizeQTable(oldExperience.getqTable()));
			// copy actions dictionary (actions + old rewards)
			newExperience.setActionsDictionary(oldExperience.getActionsDictionary());
			newExperience.influenceQTableFromActionTable(preferences);
			// if tags coincide, introduce in qtable rewards*coef 0,2
		}
		return newExperience;
	}

	/**
	 * Loads the knowledge from file if it exists.
	 * 
	 * @return Experience from file if it exists. Otherwise, it returns a new Experience.
	 */
	private ExperienceMap loadKnowledgeFromFile() {
		ExperienceMap oldExperience = new ExperienceMap();

		FileInputStream knowledgeFile = null;
		BufferedInputStream inputStream = null;
		ObjectInput objectInputStream = null;
		try {
			knowledgeFile = new FileInputStream(knowledgeFilePath);
			inputStream = new BufferedInputStream(knowledgeFile);
			objectInputStream = new ObjectInputStream(inputStream);

			oldExperience = ((ExperienceMap) objectInputStream.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectInputStream != null)
					objectInputStream.close();
				if (inputStream != null)
					inputStream.close();
				if (knowledgeFile != null)
					knowledgeFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return oldExperience;
	}

	/**
	 * Normalizes the Q-table, putting zero for all the values.
	 * 
	 * @param qTable
	 * @return QTable with all zero values.
	 */
	private Map<Integer, HashMap<Integer, HashMap<Integer, Double>>> normalizeQTable(
			Map<Integer, HashMap<Integer, HashMap<Integer, Double>>> qTable) {
		for (Integer key : qTable.keySet()) {
			for (Integer key2 : qTable.get(key).keySet()) {
				for (Integer key3 : qTable.get(key).get(key2).keySet()) {
					qTable.get(key).get(key2).put(key3, 0.0);
				}
			}
		}
		return qTable;
	}
	
	/**
	 * Saves the experience
	 */
	public void save() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(knowledgeFilePath));
			oos.writeObject(experience);
			oos.flush();
			oos.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the experience
	 * 
	 * @return the experience
	 */
	public ExperienceMap getExperience() {
		return experience;
	}
}
