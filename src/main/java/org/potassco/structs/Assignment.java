package org.potassco.structs;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Structure;

/**
 * Represents a (partial) assignment of a particular solver.
 *
 * An assignment assigns truth values to a set of literals.
 * A literal is assigned to either @link clingo_assignment_truth_value() true or false, or is unassigned@endlink.
 * Furthermore, each assigned literal is associated with a @link clingo_assignment_level() decision level@endlink.
 * There is exactly one @link clingo_assignment_decision() decision literal@endlink for each decision level greater than zero.
 * Assignments to all other literals on the same level are consequences implied by the current and possibly previous decisions.
 * Assignments on level zero are immediate consequences of the current program.
 * Decision levels are consecutive numbers starting with zero up to and including the @link clingo_assignment_decision_level() current decision level@endlink.

 * @author Josef Schneeberger
 * {@link clingo_h#clingo_assignment_t}
 */
public class Assignment extends Structure {

}
