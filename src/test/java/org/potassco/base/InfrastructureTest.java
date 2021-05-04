package org.potassco.base;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.StatisticsType;
import org.potassco.enums.SymbolType;

import com.sun.jna.Pointer;

public class InfrastructureTest {

	@Test
	public void testSignature() {
		BaseClingo clingo = new BaseClingo();
		try {
			String name = "test";
			int arity = 2;
			boolean positive = true;
			Pointer signature = clingo.signatureCreate(name, arity, positive);
			assertEquals(name, clingo.signatureName(signature));
			assertEquals(arity, clingo.signatureArity(signature));
			assertEquals(positive, clingo.signatureIsPositive(signature));
			assertEquals(!positive, clingo.signatureIsNegative(signature));
			assertTrue(clingo.signatureIsEqualTo(signature, clingo.signatureCreate("test", 2, true)));
			assertTrue(clingo.signatureIsLessThan(signature, clingo.signatureCreate("test", 3, true)));
			int hash = clingo.signatureHash(signature);
			assertEquals(hash , clingo.signatureHash(signature)); // returns the same hash
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSymbolHandling() {
		BaseClingo clingo = new BaseClingo();
		int number = 42;
		long num = clingo.symbolCreateNumber(number);
		assertEquals(number, clingo.symbolNumber(num));
		// TODO: Is this correct?
		assertEquals(false, clingo.symbolIsPositive(num));
		// TODO: Is this correct?
		assertEquals(false, clingo.symbolIsNegative(num));
		
		String c = "clingo";
		assertEquals(c, clingo.symbolString(clingo.symbolCreateString(c)));
		long sup = clingo.symbolCreateSupremum();
		assertEquals(sup, clingo.symbolCreateSupremum());
		long inf = clingo.symbolCreateInfimum();
		assertEquals(inf, clingo.symbolCreateInfimum());
		
		String p = "potassco";
		long ps = clingo.symbolCreateId(p, true);
		assertEquals(null, clingo.symbolString(ps));
		assertEquals(p, clingo.symbolName(ps));
		assertEquals(true, clingo.symbolIsPositive(ps));
		assertEquals(false, clingo.symbolIsNegative(ps));
	}

	@Test
	public void testSymbolCreateFunction() {
		BaseClingo clingo = new BaseClingo();
		int number = 42;
		long num = clingo.symbolCreateNumber(number);
		String c = "clingo";
		long s = clingo.symbolCreateString(c);
		String p = "potassco";
		List<Long> args = new ArrayList<Long>();
		args.add(num);
		args.add(s);
		long f = clingo.symbolCreateFunction(p, args, true);
		assertEquals(p, clingo.symbolName(f));
		assertEquals(true, clingo.symbolIsPositive(f));
//		clingo.symbolArguments(f, null, null); TODO: infuctional
		assertEquals(SymbolType.FUNCTION, clingo.symbolType(f));
		assertEquals(20, clingo.symbolToStringSize(f));
//	TODO:	assertEquals(p, clingo.symbolToString(f, new Size(2)));
		assertFalse(clingo.symbolIsEqualTo(s, f));
		assertTrue(clingo.symbolIsEqualTo(num, clingo.symbolCreateNumber(number)));
		assertTrue(clingo.symbolIsLessThan(s, f));
		int hash = clingo.symbolHash(f);
		assertEquals(hash, clingo.symbolHash(f));
		long[] res = clingo.symbolArguments(f);
		for (int i = 0; i < res.length; i++) {
			assertFalse(clingo.symbolIsEqualTo(args.get(i), res[i]));
		}
	}

	@Test
	public void testSymbolType() {
		BaseClingo clingo = new BaseClingo();
		String c = "clingo";
		assertEquals(c, clingo.addString(c));
		String t = "f(a,42)";
		long symbol = clingo.parseTerm(t);
		assertEquals(SymbolType.FUNCTION, clingo.symbolType(symbol));
	}

	@Test
	public void testConfiguration1() {
		String name = "base";
		String program = "a. b.";
		Clingo clingo = new Clingo();
		Pointer control = clingo.control(null);
		clingo.controlAdd(control, name, null, program);
//		clingo.controlGround(control, name); - not used here!
		Pointer conf = clingo.controlConfiguration(control);
		int root = clingo.configurationRoot(conf);
		assertEquals(root, clingo.configurationRoot(conf));
		assertEquals(ConfigurationType.MAP, clingo.configurationType(conf, root));
		assertEquals("Options", clingo.configurationDescription(conf, root));
	}

	/**
	 * {@link https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_conf.py}
	 */
	@Test
	public void testStatistics() {
		String name = "base";
//		String program = "a. b.";
		Clingo clingo = new Clingo();
		Pointer control = clingo.control(null);
		clingo.controlAdd(control, name, null, name);
//		clingo.ground(name); - not used here!
		Pointer stats = clingo.controlStatistics(control);
		long root = clingo.statisticsRoot(stats);
		assertEquals(StatisticsType.EMPTY, clingo.statisticsType(stats, root));
		assertEquals(0L, clingo.clingoStatisticsArraySize(stats, root));
	}


//    public int statisticsArrayAt(Pointer statistics, long key, long offset) {
//    public int statisticsArrayPush(Pointer statistics, long key, int type) {
//    public long statisticsMapSize(Pointer statistics, long key) {
//    public byte statisticsMapHas_subkey(Pointer statistics, long key, String name) {
//    public String statisticsMapSubkey_name(Pointer statistics, long key, long offset) {
//    public int statisticsMapAt(Pointer statistics, long key, String name) {
//    public int statisticsMapAddSubkey(Pointer statistics, long key, String name, int type) {
//    public double statisticsValueGet(Pointer statistics, long key) {
//    public void statisticsValueSet(Pointer statistics, long key, double value) {
}
