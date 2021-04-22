package org.potassco.dto;

import java.util.HashSet;
import java.util.Set;

public class Solution {

	/**
	 * Number of symbols in the solution model??
	 */
	private int size;
	private Set<String> symbols = new HashSet<String>();

	public void setSize(int size) {
		this.size = size;
	}

	public void addSymbol(String symbol) {
		symbols.add(symbol);
	}

	public Set<String> getSymbols() {
		return symbols;
	}

	public void setSymbols(Set<String> symbols) {
		this.symbols = symbols;
	}

	public int getSize() {
		return size;
	}

}
