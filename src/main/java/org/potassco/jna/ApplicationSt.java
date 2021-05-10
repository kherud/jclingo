package org.potassco.jna;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * This struct contains a set of functions to customize the clingo application.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_application_t}
 */
public class ApplicationSt extends Structure {
	public String name;
	public String version;
	public int message_limit;
//	public MainFunctionCallback main; // typedef bool (*clingo_main_function_t) (clingo_control_t *control, char const *const * files, size_t size, void *data);
//	public Pointer logger;
//	public Pointer printer;
//    char const *(*program_name) (void *data);                        //!< callback to obtain program name
//    char const *(*version) (void *data);                             //!< callback to obtain version information
//    unsigned (*message_limit) (void *data);                          //!< callback to obtain message limit
//    clingo_main_function_t main;                                     //!< callback to override clingo's main function
//    clingo_logger_t logger;                                          //!< callback to override default logger
//    clingo_model_printer_t printer;                                  //!< callback to override default model printing
//    bool (*register_options)(clingo_options_t *options, void *data); //!< callback to register options
//    bool (*validate_options)(void *data);                            //!< callback validate options

}
