package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.potassco.api.ClingoException;
import org.potassco.enums.ConfigurationType;
import org.potassco.enums.StatisticsType;
import org.potassco.enums.SymbolType;
import org.potassco.jna.BaseClingo;

import com.sun.jna.Pointer;

public class BaseInfrastructureTest {

	@Test
	public void testSignature() {
		try {
			String name = "test";
			int arity = 2;
			boolean positive = true;
			Pointer signature = BaseClingo.signatureCreate(name, arity, positive);
			assertEquals(name, BaseClingo.signatureName(signature));
			assertEquals(arity, BaseClingo.signatureArity(signature));
			assertEquals(positive, BaseClingo.signatureIsPositive(signature));
			assertEquals(!positive, BaseClingo.signatureIsNegative(signature));
			assertTrue(BaseClingo.signatureIsEqualTo(signature, BaseClingo.signatureCreate("test", 2, true)));
			assertTrue(BaseClingo.signatureIsLessThan(signature, BaseClingo.signatureCreate("test", 3, true)));
			int hash = BaseClingo.signatureHash(signature);
			assertEquals(hash , BaseClingo.signatureHash(signature)); // returns the same hash
		} catch (ClingoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSymbolHandling() {
		int number = 42;
		long num = BaseClingo.symbolCreateNumber(number);
		assertEquals(number, BaseClingo.symbolNumber(num));
		// TODO: Is this correct?
		assertEquals(false, BaseClingo.symbolIsPositive(num));
		// TODO: Is this correct?
		assertEquals(false, BaseClingo.symbolIsNegative(num));
		
		String c = "clingo";
		assertEquals(c, BaseClingo.symbolString(BaseClingo.symbolCreateString(c)));
		long sup = BaseClingo.symbolCreateSupremum();
		assertEquals(sup, BaseClingo.symbolCreateSupremum());
		long inf = BaseClingo.symbolCreateInfimum();
		assertEquals(inf, BaseClingo.symbolCreateInfimum());
		
		String p = "potassco";
		long ps = BaseClingo.symbolCreateId(p, true);
		assertEquals(null, BaseClingo.symbolString(ps));
		assertEquals(p, BaseClingo.symbolName(ps));
		assertEquals(true, BaseClingo.symbolIsPositive(ps));
		assertEquals(false, BaseClingo.symbolIsNegative(ps));
	}

	@Test
	public void testSymbolCreateFunction() {
		int number = 42;
		long num = BaseClingo.symbolCreateNumber(number);
		String c = "clingo";
		long s = BaseClingo.symbolCreateString(c);
		String p = "potassco";
		List<Long> args = new ArrayList<Long>();
		args.add(num);
		args.add(s);
		long f = BaseClingo.symbolCreateFunction(p, args, true);
		assertEquals(p, BaseClingo.symbolName(f));
		assertEquals(true, BaseClingo.symbolIsPositive(f));
//		BaseClingo.symbolArguments(f, null, null); TODO: infuctional
		assertEquals(SymbolType.FUNCTION, BaseClingo.symbolType(f));
//	TODO:	assertEquals(p, BaseClingo.symbolToString(f, new Size(2)));
		assertFalse(BaseClingo.symbolIsEqualTo(s, f));
		assertTrue(BaseClingo.symbolIsEqualTo(num, BaseClingo.symbolCreateNumber(number)));
		assertTrue(BaseClingo.symbolIsLessThan(s, f));
		int hash = BaseClingo.symbolHash(f);
		assertEquals(hash, BaseClingo.symbolHash(f));
		long[] res = BaseClingo.symbolArguments(f);
		for (int i = 0; i < res.length; i++) {
			assertFalse(BaseClingo.symbolIsEqualTo(args.get(i), res[i]));
		}
	}

	@Test
	public void testSymbolType() {
		String c = "clingo";
		assertEquals(c, BaseClingo.addString(c));
		String t = "f(a,42)";
		long symbol = BaseClingo.parseTerm(t);
		assertEquals(SymbolType.FUNCTION, BaseClingo.symbolType(symbol));
	}

	@Test
	public void testConfiguration1() {
		String name = "base";
		String program = "a. b.";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, program);
//		BaseClingo.controlGround(control, name); - not used here!
		Pointer conf = BaseClingo.controlConfiguration(control);
		int root = BaseClingo.configurationRoot(conf);
		assertEquals(root, BaseClingo.configurationRoot(conf));
		assertEquals(ConfigurationType.MAP, BaseClingo.configurationType(conf, root));
		assertEquals("Options", BaseClingo.configurationDescription(conf, root));
	}

	/**
	 * {@link https://github.com/potassco/clingo/blob/master/libpyclingo/clingo/tests/test_conf.py}
	 */
	@Test
	public void testStatistics() {
		String name = "base";
//		String program = "a. b.";
		Pointer control = BaseClingo.control(null, null, null, 0);
		BaseClingo.controlAdd(control, name, null, name);
//		BaseClingo.ground(name); - not used here!
		Pointer stats = BaseClingo.controlStatistics(control);
		long root = BaseClingo.statisticsRoot(stats);
		assertEquals(StatisticsType.EMPTY, BaseClingo.statisticsType(stats, root));
		assertEquals(0L, BaseClingo.clingoStatisticsArraySize(stats, root));
	}

// TODO
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
