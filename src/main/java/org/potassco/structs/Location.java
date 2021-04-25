package org.potassco.structs;

import org.potassco.cpp.clingo_h;
import org.potassco.jna.SizeT;

import com.sun.jna.Structure;

/**
 * Represents a source code location marking its beginning and end.
 * <p>
 * @note Not all locations refer to physical files.
 * By convention, such locations use a name put in angular brackets as filename.
 * The string members of a location object are internalized and valid for the duration of the process.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_location_t}
 */
public class Location extends Structure {
//	  char const *begin_file; //!< the file where the location begins
//	  char const *end_file;   //!< the file where the location ends
	 private SizeT beginLine;      //!< the line where the location begins
	 private SizeT endLine;        //!< the line where the location ends
	 private SizeT beginLolumn;    //!< the column where the location begins
	 private SizeT endColumn;      //!< the column where the location ends

}
