package hvl.projectparmorel.modelrepair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;

public class ErrorExtractor {

	public static List<Integer> unsuportedErrorCodes = new ArrayList<>(Arrays.asList(4, 6));
	private static Logger logger = Logger.getGlobal();
	
	/**
	 * Extracts the errors from the provided model.
	 * 
	 * @param model
	 * @return a list of errors found in the model
	 */
	public static List<Error> extractErrorsFrom(Resource model) {
		List<Error> errors = new ArrayList<Error>();

		Diagnostic diagnostic = validateMode(model);
		if (diagnostic.getSeverity() != Diagnostic.OK) {
			for (Diagnostic child : diagnostic.getChildren()) {
				Error error = getErrorFor(child);
				if (error != null) {
					if(unsuportedErrorCodes.contains(error.getCode())){
						logger.warning("Encounteded unsupported error " + error.getCode() + ". Message: " + error.getMessage() + ". Skipping error.");
					} else {
						errors.add(error);
					}
				}
			}
		}
		return errors;
	}

	/**
	 * Validates the model
	 * 
	 * @param model
	 * @return the diagnostic for the model
	 */
	private static Diagnostic validateMode(Resource model) {
		EObject object = model.getContents().get(0);
		return Diagnostician.INSTANCE.validate(object);
	}

	/**
	 * Gets the error for the specified diagnostic
	 * 
	 * @param diagnostic
	 * @param sons
	 * @return the error for the specified diagnostic
	 */
	private static Error getErrorFor(Diagnostic diagnostic) {
		if (diagnostic.getCode() != 1) { // we don't remember what error code 1 is. Could it be an error at package
											// level?
			if (isPackageOrTwoFeatures(diagnostic)) {
				return new Error(diagnostic.getCode(), diagnostic.getMessage(), diagnostic.getData());
			} else {
				return getErrorFromErrorCode(diagnostic);
			}
		}
		return null;
	}

	/**
	 * Checks whether the diagnostic is a package or two features, or if it is not.
	 * 
	 * @param diagnostic
	 * @return true if the diagnostic is a package or has two features, false
	 *         otherwise.
	 */
	private static boolean isPackageOrTwoFeatures(Diagnostic diagnostic) {
		return diagnostic.getData().get(0).getClass() == EPackageImpl.class
				|| diagnostic.getMessage().contains("two features");
	}

	private static Error getErrorFromErrorCode(Diagnostic diagnostic) {
		switch (diagnostic.getCode()) {
		case 40: // The typed element must have a type
			return handleError40(diagnostic);
		case 44: // The name X is not well formed
			return handleError44(diagnostic);
		default:
			Error error = handleEReferenceImplError(diagnostic);
			if (error == null) {
				error = new Error(diagnostic.getCode(), diagnostic.getMessage(), diagnostic.getData());
			}
			return error;
		}
	}

	/**
	 * Gets an error that fits if the error code is 40 (The typed element must have a type). 
	 * 
	 * It sets the error code of the return to 401 if it contains a EReferenceImpl, and 40 otherwise.
	 * 
	 * @param diagnostic
	 * @throws IllegalArgumentException if the code of the diagnostic is not 40
	 * @return an error object with code 40 or 401
	 */
	private static Error handleError40(Diagnostic diagnostic) {
		if(diagnostic.getCode() != 40)
			throw new IllegalArgumentException("This method should only be called when diagnostic code is 40.");
		String code = String.valueOf(diagnostic.getCode());
		if (diagnostic.getData().get(0).getClass().toString().contains("EReferenceImpl")) {
			code = code + "1";
			return new Error(Integer.parseInt(code), diagnostic.getMessage(), diagnostic.getData());
		} else {
			return new Error(diagnostic.getCode(), diagnostic.getMessage(), diagnostic.getData());
		}
	}
	
	/**
	 * Gets an error that fits if the error code is 44 (The name X is not well formed).
	 * 
	 *  The error code is 44 if the class is of EReferenceImpl.class.
	 *  The error code is 441 if the class contains EClassImpl
	 *  The error code is 442 if the class contains EOperation
	 *  The error code is 443 if the class contains EAttribute
	 *  The error code is 444 if the class contains ETypeParameterImpl
	 *  The error code is 445 if the class contains EEnum
	 *  
	 * @param diagnostic
	 * @throws IllegalArgumentException if the code of the diagnostic is not 44
	 * @return an error with specified code
	 */
	private static Error handleError44(Diagnostic diagnostic) {
		if(diagnostic.getCode() != 44)
			throw new IllegalArgumentException("This method should only be called when diagnostic code is 44.");
		String s = String.valueOf(44);
		if (diagnostic.getData().get(0).getClass().toString().contains("EClassImpl")) {
			s = s + "1";
		}
		if (diagnostic.getData().get(0).getClass().toString().contains("EOperation")) {
			s = s + "2";
		}
		if (diagnostic.getData().get(0).getClass().toString().contains("EAttribute")) {
			s = s + "3";
		}
		if (diagnostic.getData().get(0).getClass().toString().contains("ETypeParameterImpl")) {
			s = s + "4";
			// if name null
		}
		if (diagnostic.getData().get(0).getClass().toString().contains("EEnum")) {
			s = s + "5";
			// if name null
		}
		Error error = handleEReferenceImplError(diagnostic);
		if (error == null) {
			error = new Error(Integer.parseInt(s), diagnostic.getMessage(), diagnostic.getData());
		}
		return error;
	}

	/**
	 * Handles errors where the class is of type EReferenceImpl.class
	 * 
	 * @param diagnostic
	 * @return an Error for EReferenceImpl.class, null otherwise
	 */
	private static Error handleEReferenceImplError(Diagnostic diagnostic) {
		if (diagnostic.getData().get(0).getClass() == EReferenceImpl.class) {
			try {
				Error e = new Error(diagnostic.getCode(), diagnostic.getMessage(), diagnostic.getData());
				EReferenceImpl era = (EReferenceImpl) EReference.class.getMethod("getEOpposite")
						.invoke(diagnostic.getData().get(0));
				if (era != null) {
					List<Object> contexts = new ArrayList<Object>(diagnostic.getData());
					contexts.add(0, era);
					e.setContexts(contexts);
				}
				return e;
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
}