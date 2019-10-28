package hvl.projectparmorel.modelrepair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * @author Magnus Marthinsen
 * @author Angela Barriga Rodriguez, abar@hvl.no 
 * 2019 
 * Western Norway University of Applied Sciences Bergen - Norway
 */
public class Solution implements Comparable<Solution> {
	private int id;
	private List<AppliedAction> sequence;
	private double weight;
	private File model;
	private File original;
	private ResourceSet resourceSet;

	public Solution() {
		super();
		sequence = new ArrayList<AppliedAction>();
		weight = 0.0;
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	}

	public Solution(int id, List<AppliedAction> seq, double weight, File model) {
		super();
		this.id = id;
		this.sequence = seq;
		this.weight = weight;
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		this.model = model;
	}
	
	/**
	 * Discards the sequence and deletes the associated file;
	 */
	public void discard() {
		model.delete();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AppliedAction> getSequence() {
		return sequence;
	}

	public void setSequence(List<AppliedAction> sequence) {
		this.sequence = sequence;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Sequence [id=" + id + ", seq=" + sequence + ", weight=" + weight + "]" + System.getProperty("line.separator")
				+ System.getProperty("line.separator");
	}

	public void setModel(File model) {
		this.model = model;
	}

	public File getModel() {
		return model;
	}

	@Override
	public int compareTo(Solution solution) {
		return Double.compare(weight, solution.getWeight());
	}

	/**
	 * Set the original model
	 * 
	 * @param originalModel
	 */
	public void setOriginal(File originalModel) {
		original = originalModel;
	}
	
	/**
	 * Gets the original file
	 * @return
	 */
	public File getOriginal() {
		return original;
	}
}