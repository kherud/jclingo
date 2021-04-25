package org.potassco;

import java.util.HashSet;
import java.util.Set;

public class SolveHandle {

	private int size;
	private Set<String> symbols = new HashSet<>();

	public void setSize(int size) {
		this.size = size;
	}

	public void addSymbol(String symbol) {
		this.symbols.add(symbol);
	}

	public int getSize() {
		return size;
	}

	public Set<String> getSymbols() {
		return symbols;
	}

}
