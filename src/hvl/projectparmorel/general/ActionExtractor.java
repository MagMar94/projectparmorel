package hvl.projectparmorel.general;

import java.util.List;

import hvl.projectparmorel.knowledge.Action;
import hvl.projectparmorel.modelrepair.Error;

public interface ActionExtractor {
	
	/**
	 * Extract all the actions that has the potential to solve the specified errors.
	 * 
	 * @param errors
	 * @return
	 */
	public List<Action> extractActionsFor(List<Error> errors);
}
