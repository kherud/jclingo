package org.potassco.clingo.control;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.control.MainFunctionCallback;
import org.potassco.clingo.solving.ModelPrinterCallback;

import java.util.Arrays;
import java.util.List;

/**
 * This struct contains a set of functions to customize the clingo application.
 */
public class Application extends Structure {

	/**
	 * JNA DECLARATIONS IGNORE
	 */

	// callback to obtain program name
	public interface ProgramNameCallback extends Callback {
		default String callback(Pointer data) {
			return call();
		}

		String call();
	}

	// callback to obtain version information
	public interface VersionCallback extends Callback {
		String callback(Pointer data);
	}

	// callback to obtain message limit
	public interface MessageLimitCallback extends Callback {
		int callback(Pointer data);
	}

	// callback to register options
	public interface RegisterOptionsCallback extends Callback {
		boolean callback(Pointer options, Pointer data);
	}

	// callback validate options
	public interface ValidateOptionsCallback extends Callback {
		boolean callback(Pointer data);
	}

	public ProgramNameCallback programName; // callback to obtain program name
	public VersionCallback version; // callback to obtain version information
	public MessageLimitCallback messageLimit; // callback to obtain message limit
	public MainFunctionCallback main; // callback to override clingo's main function
	public LoggerCallback logger; // callback to override default logger
	public ModelPrinterCallback printer; // callback to override default model printing
	public RegisterOptionsCallback registerOptions; // callback to register options
	public ValidateOptionsCallback validateOptions; // callback validate options

	protected List<String> getFieldOrder() {
		return Arrays.asList("programName", "version", "messageLimit", "main", "logger", "printer", "registerOptions", "validateOptions");
	}

}
