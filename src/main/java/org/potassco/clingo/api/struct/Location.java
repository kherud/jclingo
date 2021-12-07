package org.potassco.clingo.api.struct;

import com.sun.jna.Structure;
import org.potassco.clingo.api.types.NativeSize;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a source code location marking its beginnig and end.
 *
 * Not all locations refer to physical files.
 * By convention, such locations use a name put in angular brackets as filename.
 * The string members of a location object are internalized and valid for the duration of the process.
 *
 * @author Josef Schneeberger
 */
public class Location extends Structure {
	public String begin_file; // the file where the location begins
	public String end_file; // the file where the location ends
	public NativeSize begin_line; // the line where the location begins
	public NativeSize end_line; // the line where the location ends
	public NativeSize begin_column; // the column where the location begins
	public NativeSize end_column; // the column where the location ends

	public Location(String begin_file, String end_file, NativeSize begin_line, NativeSize end_line, NativeSize begin_column, NativeSize end_column) {
		super();
		this.begin_file = begin_file;
		this.end_file = end_file;
		this.begin_line = begin_line;
		this.end_line = end_line;
		this.begin_column = begin_column;
		this.end_column = end_column;
	}

	public Location(String begin_file, String end_file, int begin_line, int end_line, int begin_column, int end_column) {
		this.begin_file = begin_file;
		this.end_file = end_file;
		this.begin_line = new NativeSize(begin_line);
		this.end_line = new NativeSize(end_line);
		this.begin_column = new NativeSize(begin_column);
		this.end_column = new NativeSize(end_column);
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("begin_file", "end_file", "begin_line", "end_line", "begin_column", "end_column");
	}

}