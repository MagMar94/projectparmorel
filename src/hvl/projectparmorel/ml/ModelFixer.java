package hvl.projectparmorel.ml;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public interface ModelFixer {
	
	/**
	 * Sets the user preferences used in the algorithm.
	 * 
	 * @param preferences
	 */
	public void setPreferences(List<Integer> preferences);

	/**
	 * Fixes the model provided as attribute, and stores the repaired model in the uri-location.
	 * @param model
	 * @param ur
	 * @return the optimal sequence of actions
	 */
	public Sequence fixModel(Resource model, URI uri);

	/**
	 * Gets the model specified by the uri
	 * 
	 * @param uri
	 * @return the coresponding model
	 */
	public Resource getModel(URI uri);

	
}