package org.potassco.dto;

import java.util.HashSet;
import java.util.Set;

public class Solution {

	private long size;
	private Set<String> symbols = new HashSet<>();

	public void setSize(long size) {
		this.size = size;
	}

	public void addSymbol(String symbol) {
		this.symbols.add(symbol);
	}

	public long getSize() {
		return size;
	}

	public Set<String> getSymbols() {
		return symbols;
	}

}