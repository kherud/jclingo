package org.potassco.api;

import java.util.List;

import org.potassco.enums.SymbolType;
import org.potassco.jna.BaseClingo;

public class Symbol {
	/**
	 * Construct a symbol representing a number.
	 * 
	 * @param number
	 * @return the symbol
	 */
	public static Symbol createNumber(int number) {
		return new Symbol(BaseClingo.symbolCreateNumber(number));
	}

	/**
	 * Construct a symbol representing \#sup.
	 * 
	 * @return the symbol
	 */
	public static Symbol createSupremum() {
		return new Symbol(BaseClingo.symbolCreateSupremum());
	}

	/**
	 * Construct a symbol representing \#inf.
	 * 
	 * @return the symbol
	 */
	public static Symbol createInfimum() {
		return new Symbol(BaseClingo.symbolCreateInfimum());
	}

	/**
	 * Construct a symbol representing a string.
	 * 
	 * @return the symbol
	 */
	public static Symbol createString(String string) {
		return new Symbol(BaseClingo.symbolCreateString(string));
	}

	/**
	 * Construct a symbol representing an id.
	 * <p>
	 * 
	 * @note This is just a shortcut for clingo_symbol_create_function() with empty
	 *       arguments.
	 * @param name     the name
	 * @param positive whether the symbol has a classical negation sign
	 * @return the symbol
	 */
	public static Symbol createId(String name, boolean positive) {
		return new Symbol(BaseClingo.symbolCreateId(name, positive));
	}

	/**
	 * Construct a symbol representing a function or tuple.
	 * <p>
	 * 
	 * @note To create tuples, the empty string has to be used as name.
	 * @param name      the name of the function
	 * @param arguments the arguments of the function
	 * @param positive  whether the symbol has a classical negation sign
	 * @return the symbol
	 */
	public static Symbol symbolCreateFunction(String name, List<Long> arguments, boolean positive) {
		return new Symbol(BaseClingo.symbolCreateFunction(name, arguments, positive));
	}

	/**
	 * Parse a term in string form.
	 * <p>
	 * The result of this function is a symbol. The input term can contain
	 * unevaluated functions, which are evaluated during parsing.
	 * 
	 * @param string the string to parse
	 * @return the resulting symbol
	 */
	public static Symbol parseTerm(String string) {
		return new Symbol(BaseClingo.parseTerm(string));
	}

	private long reference;
	
	private Symbol() {}
	
	private Symbol(long reference) {
		this.reference = reference;
	}

	/**
	 * Get the number of a symbol.
	 * 
	 * @return the resulting number
	 */
	public int symbolNumber() {
		return BaseClingo.symbolNumber(this.reference);
	}

	/**
	 * Get the name of a symbol.
	 * 
	 * @return the resulting name
	 */
	public String symbolName() {
		return BaseClingo.symbolName(this.reference);
	}

	/**
	 * Get the string of a symbol.
	 * <p>
	 * 
	 * @note The string is internalized and valid for the duration of the process.
	 * @return the resulting string
	 */
	public String string() {
		return BaseClingo.symbolString(this.reference);
	}

	/**
	 * Check if a function is positive (does not have a sign).
	 * 
	 * @return true if positive
	 */
	public boolean isPositive() {
		return BaseClingo.symbolIsPositive(this.reference);
	}

	/**
	 * Check if a function is negative (has a sign).
	 * 
	 * @return true if negative
	 */
	public boolean isNegative() {
		return BaseClingo.symbolIsNegative(this.reference);
	}

	/**
	 * Get the arguments of a symbol.
	 * 
	 * @return the arguments as an array of symbols
	 */
	public Symbol[] arguments() {
		long[] symbolArguments = BaseClingo.symbolArguments(this.reference);
		Symbol[] result = new Symbol[symbolArguments.length];
		for (int i = 0; i < symbolArguments.length; i++) {
			result[i] = new Symbol(symbolArguments[i]);
		}
		return result;
	}

	/**
	 * Get the type of a symbol.
	 * 
	 * @return the type of the symbol
	 */
	public SymbolType symbolType() {
		return BaseClingo.symbolType(this.reference);
	}

	/**
	 * Get the string representation of a symbol.
	 * 
	 * @return the resulting string
	 */
	public String toString() {
		return BaseClingo.symbolToString(this.reference);
	}

	// Symbol Comparison Functions

	/**
	 * @param a first symbol
	 * @param b second symbol
	 * @return true if two symbols are equal.
	 */
	public boolean isEqualTo(Symbol a, Symbol b) {
		return BaseClingo.symbolIsEqualTo(a.reference, b.reference);
	}

	public boolean equals(Symbol other) {
		return BaseClingo.symbolIsEqualTo(this.reference, other.reference);
	}

	/**
	 * Check if a symbol is less than another symbol.
	 * <p>
	 * Symbols are first compared by type. If the types are equal, the values are
	 * compared (where strings are compared using strcmp). Functions are first
	 * compared by signature and then lexicographically by arguments.
	 * 
	 * @param a first symbol
	 * @param b second symbol
	 * @return
	 */
	public boolean isLessThan(Symbol a, Symbol b) {
		return BaseClingo.symbolIsLessThan(a.reference, b.reference);
	}

	/**
	 * Calculate a hash code of a symbol.
	 * 
	 * @return the hash code of the symbol
	 */
	public int symbolHash() {
		return BaseClingo.symbolHash(this.reference);
	}

}
