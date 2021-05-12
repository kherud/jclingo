package org.potassco.jna;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.StatisticsType;
import org.potassco.util.StatisticsTree;

import com.sun.jna.Pointer;

public class StatisticsTest {

	/**
	 * https://potassco.org/clingo/c-api/5.5/statistics_8c-example.html
	 */
	@Test
	public void test() {
		Set<String> expected = new HashSet<>();
		expected.add("a");
		expected.add("b");
		int expectedModels = 2;

		String name = "base";
//		String[] arguments = {"0"};
		String[] arguments = { "-n", "0" };
//		String[] arguments = null;
		String program = "a :- not b. b :- not a.";
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		Pointer config = BaseClingo.controlConfiguration(control);
		int rootKey = BaseClingo.configurationRoot(config);

// configure to enumerate all models
//		int subKey = BaseClingo.configurationMapAt(conf, rootKey, "solve.models");
//		BaseClingo.configurationValueSet(conf, subKey, "0");

		int confSub = BaseClingo.configurationMapAt(config, rootKey, "stats");
		BaseClingo.configurationValueSet(config, confSub, "1");
		BaseClingo.controlAdd(control, name, null, program);
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		SolveEventCallback eventHandler = null;
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), eventHandler, null);
		boolean modelExists = true;
		int m = 0;
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				// print_model
//				System.out.println("ModelSt:");
				long[] atoms = BaseClingo.modelSymbols(model, ShowType.SHOWN);
				for (int i = 0; i < atoms.length; i++) {
					String str = BaseClingo.symbolToString(atoms[i]);
//					System.out.println(str);
					assertTrue(expected.contains(str));
				}
				m++;
			} else {
				modelExists = false;
			}
		}
		assertEquals(expectedModels, m);
	}

	/**
	 * https://potassco.org/clingo/c-api/5.5/statistics_8c-example.html
	 */
	@Test
	public void testStatistics() {
		String name = "base";
		String[] arguments = {"0"};
		Pointer control = BaseClingo.control(arguments, null, null, 0);
		Pointer config = BaseClingo.controlConfiguration(control);
		int configRoot = BaseClingo.configurationRoot(config);
		// and set the statistics level to one to get more statistics
		int configSub = BaseClingo.configurationMapAt(config, configRoot, "stats");
		BaseClingo.configurationValueSet(config, configSub, "1");
		BaseClingo.controlAdd(control, name, null, "a :- not b. b :- not a.");
		PartSt[] parts = new PartSt[1];
		parts[0] = new PartSt(name, null, 0L);
		BaseClingo.controlGround(control, parts, null, null);
		solve(control);
		// get the statistics object, get the root key, then print the statistics recursively
		Pointer stats = BaseClingo.controlStatistics(control);
		long statsKey = BaseClingo.statisticsRoot(stats);
		StatisticsTree tree = new StatisticsTree();
		checkStatistics(stats, statsKey, 0, tree);
//		tree.showXml();
		assertEquals(2.0, tree.queryXpath("//lp/atoms/text()"), 0.0001);
		assertEquals(2.0, tree.queryXpath("//lp/bodies/text()"), 0.0001);
		assertEquals(0.0, tree.queryXpath("//lp/atoms_aux/text()"), 0.0001);
		assertEquals(3.0, tree.queryXpath("//lp/eqs/text()"), 0.0001);
		assertEquals(2.0, tree.queryXpath("//solving//models/text()"), 0.0001);
		assertEquals(2.0, tree.queryXpath("//summary//enumerated/text()"), 0.0001);
	}
	
/*
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<ClingoStatistics>
    <problem>
        <lp>
            <atoms>2.0</atoms>
            <atoms_aux>0.0</atoms_aux>
            <disjunctions>0.0</disjunctions>
            <disjunctions_non_hcf>0.0</disjunctions_non_hcf>
            <bodies>2.0</bodies>
            <bodies_tr>2.0</bodies_tr>
            <sum_bodies>0.0</sum_bodies>
            <sum_bodies_tr>0.0</sum_bodies_tr>
            <count_bodies>0.0</count_bodies>
            <count_bodies_tr>0.0</count_bodies_tr>
            <sccs>0.0</sccs>
            <sccs_non_hcf>0.0</sccs_non_hcf>
            <gammas>0.0</gammas>
            <ufs_nodes>0.0</ufs_nodes>
            <rules>2.0</rules>
            <rules_normal>2.0</rules_normal>
            <rules_choice>0.0</rules_choice>
            <rules_minimize>0.0</rules_minimize>
            <rules_acyc>0.0</rules_acyc>
            <rules_heuristic>0.0</rules_heuristic>
            <rules_tr>2.0</rules_tr>
            <rules_tr_normal>2.0</rules_tr_normal>
            <rules_tr_choice>0.0</rules_tr_choice>
            <rules_tr_minimize>0.0</rules_tr_minimize>
            <rules_tr_acyc>0.0</rules_tr_acyc>
            <rules_tr_heuristic>0.0</rules_tr_heuristic>
            <eqs>3.0</eqs>
            <eqs_atom>0.0</eqs_atom>
            <eqs_body>0.0</eqs_body>
            <eqs_other>3.0</eqs_other>
        </lp>
        <lpStep>
            <atoms>2.0</atoms>
            <atoms_aux>0.0</atoms_aux>
            <disjunctions>0.0</disjunctions>
            <disjunctions_non_hcf>0.0</disjunctions_non_hcf>
            <bodies>2.0</bodies>
            <bodies_tr>2.0</bodies_tr>
            <sum_bodies>0.0</sum_bodies>
            <sum_bodies_tr>0.0</sum_bodies_tr>
            <count_bodies>0.0</count_bodies>
            <count_bodies_tr>0.0</count_bodies_tr>
            <sccs>0.0</sccs>
            <sccs_non_hcf>0.0</sccs_non_hcf>
            <gammas>0.0</gammas>
            <ufs_nodes>0.0</ufs_nodes>
            <rules>2.0</rules>
            <rules_normal>2.0</rules_normal>
            <rules_choice>0.0</rules_choice>
            <rules_minimize>0.0</rules_minimize>
            <rules_acyc>0.0</rules_acyc>
            <rules_heuristic>0.0</rules_heuristic>
            <rules_tr>2.0</rules_tr>
            <rules_tr_normal>2.0</rules_tr_normal>
            <rules_tr_choice>0.0</rules_tr_choice>
            <rules_tr_minimize>0.0</rules_tr_minimize>
            <rules_tr_acyc>0.0</rules_tr_acyc>
            <rules_tr_heuristic>0.0</rules_tr_heuristic>
            <eqs>3.0</eqs>
            <eqs_atom>0.0</eqs_atom>
            <eqs_body>0.0</eqs_body>
            <eqs_other>3.0</eqs_other>
        </lpStep>
        <generator>
            <vars>1.0</vars>
            <vars_eliminated>0.0</vars_eliminated>
            <vars_frozen>1.0</vars_frozen>
            <constraints>0.0</constraints>
            <constraints_binary>0.0</constraints_binary>
            <constraints_ternary>0.0</constraints_ternary>
            <acyc_edges>0.0</acyc_edges>
            <complexity>0.0</complexity>
        </generator>
    </problem>
    <solving>
        <solvers>
            <choices>1.0</choices>
            <conflicts>0.0</conflicts>
            <conflicts_analyzed>0.0</conflicts_analyzed>
            <restarts>0.0</restarts>
            <restarts_last>0.0</restarts_last>
            <extra>
                <domain_choices>0.0</domain_choices>
                <models>2.0</models>
                <models_level>3.0</models_level>
                <hcc_tests>0.0</hcc_tests>
                <hcc_partial>0.0</hcc_partial>
                <lemmas_deleted>0.0</lemmas_deleted>
                <distributed>0.0</distributed>
                <distributed_sum_lbd>0.0</distributed_sum_lbd>
                <integrated>0.0</integrated>
                <lemmas>0.0</lemmas>
                <lits_learnt>0.0</lits_learnt>
                <lemmas_binary>0.0</lemmas_binary>
                <lemmas_ternary>0.0</lemmas_ternary>
                <cpu_time>0.0</cpu_time>
                <integrated_imps>0.0</integrated_imps>
                <integrated_jumps>0.0</integrated_jumps>
                <guiding_paths_lits>1.0</guiding_paths_lits>
                <guiding_paths>1.0</guiding_paths>
                <splits>0.0</splits>
                <lemmas_conflict>0.0</lemmas_conflict>
                <lemmas_loop>0.0</lemmas_loop>
                <lemmas_other>0.0</lemmas_other>
                <lits_conflict>0.0</lits_conflict>
                <lits_loop>0.0</lits_loop>
                <lits_other>0.0</lits_other>
                <jumps>
                    <jumps>0.0</jumps>
                    <jumps_bounded>0.0</jumps_bounded>
                    <levels>0.0</levels>
                    <levels_bounded>0.0</levels_bounded>
                    <max>0.0</max>
                    <max_executed>0.0</max_executed>
                    <max_bounded>0.0</max_bounded>
                </jumps>
            </extra>
        </solvers>
    </solving>
    <summary>
        <call>0.0</call>
        <result>1.0</result>
        <signal>0.0</signal>
        <exhausted>1.0</exhausted>
        <costs/>
        <lower/>
        <concurrency>1.0</concurrency>
        <winner>0.0</winner>
        <times>
            <total>0.008991241455078125</total>
            <cpu>0.0625</cpu>
            <solve>9.975433349609375E-4</solve>
            <unsat>0.0</unsat>
            <sat>0.0</sat>
        </times>
        <models>
            <enumerated>2.0</enumerated>
            <optimal>0.0</optimal>
        </models>
    </summary>
    <accu>
        <times>
            <total>0.008991241455078125</total>
            <cpu>0.0625</cpu>
            <solve>9.975433349609375E-4</solve>
            <unsat>0.0</unsat>
            <sat>0.0</sat>
        </times>
        <models>
            <enumerated>2.0</enumerated>
            <optimal>0.0</optimal>
        </models>
        <solving>
            <solvers>
                <choices>1.0</choices>
                <conflicts>0.0</conflicts>
                <conflicts_analyzed>0.0</conflicts_analyzed>
                <restarts>0.0</restarts>
                <restarts_last>0.0</restarts_last>
                <extra>
                    <domain_choices>0.0</domain_choices>
                    <models>2.0</models>
                    <models_level>3.0</models_level>
                    <hcc_tests>0.0</hcc_tests>
                    <hcc_partial>0.0</hcc_partial>
                    <lemmas_deleted>0.0</lemmas_deleted>
                    <distributed>0.0</distributed>
                    <distributed_sum_lbd>0.0</distributed_sum_lbd>
                    <integrated>0.0</integrated>
                    <lemmas>0.0</lemmas>
                    <lits_learnt>0.0</lits_learnt>
                    <lemmas_binary>0.0</lemmas_binary>
                    <lemmas_ternary>0.0</lemmas_ternary>
                    <cpu_time>0.0</cpu_time>
                    <integrated_imps>0.0</integrated_imps>
                    <integrated_jumps>0.0</integrated_jumps>
                    <guiding_paths_lits>1.0</guiding_paths_lits>
                    <guiding_paths>1.0</guiding_paths>
                    <splits>0.0</splits>
                    <lemmas_conflict>0.0</lemmas_conflict>
                    <lemmas_loop>0.0</lemmas_loop>
                    <lemmas_other>0.0</lemmas_other>
                    <lits_conflict>0.0</lits_conflict>
                    <lits_loop>0.0</lits_loop>
                    <lits_other>0.0</lits_other>
                    <jumps>
                        <jumps>0.0</jumps>
                        <jumps_bounded>0.0</jumps_bounded>
                        <levels>0.0</levels>
                        <levels_bounded>0.0</levels_bounded>
                        <max>0.0</max>
                        <max_executed>0.0</max_executed>
                        <max_bounded>0.0</max_bounded>
                    </jumps>
                </extra>
            </solvers>
        </solving>
    </accu>
    <user_step/>
    <user_accu>
        <values>
            <index id="0">7.18678654E8</index>
            <index id="1">8.1877894E8</index>
            <index id="2">1.44802289E8</index>
            <index id="3">2.90638998E8</index>
            <index id="4">8.02713623E8</index>
            <index id="5">4.83506197E8</index>
            <index id="6">1.70008382E8</index>
            <index id="7">3.34162461E8</index>
            <index id="8">5.50005631E8</index>
            <index id="9">3.90993342E8</index>
        </values>
        <summary>
            <sum>4.09321221E8</sum>
            <avg>4.09321221E7</avg>
        </summary>
    </user_accu>
</ClingoStatistics>
 */
	
	private void checkStatistics(Pointer stats, long key, int depth, StatisticsTree tree) {
		StatisticsType type = BaseClingo.statisticsType(stats, key);
		switch (type) {
		case VALUE: {
			double value = BaseClingo.statisticsValueGet(stats, key);
			tree.addValue(value, depth);
			break;
		}
		case ARRAY: {
			SizeT size = BaseClingo.statisticsArraySize(stats, key);
			for (int j = 0; j < size.intValue(); j++) {
				long subkey = BaseClingo.statisticsArrayAt(stats, key, new SizeT(j));
				tree.addIndex(j, depth);
		        // recursively print subentry
				checkStatistics(stats, subkey, depth + 1, tree);
			}
			break;
		}
		case MAP: {
			SizeT size = BaseClingo.statisticsMapSize(stats, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.statisticsMapSubkeyName(stats, key, new SizeT(j));
				long subkey = BaseClingo.statisticsMapAt(stats, key, name);
				tree.addNode(name, depth);
		        // recursively print subentry
				checkStatistics(stats, subkey, depth + 1, tree);
			}
			break;
		}
		case EMPTY: {
			// this case won't occur if the statistics are traversed like this
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}

	private int solve(Pointer control) {
		SolveEventCallback eventHandler = new SolveEventCallback() {
			@Override
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				int sum = 0;
				long value;
				switch (SolveEventType.fromValue(type)) {
				case STATISTICS:
					// obtain a pointer to the accumulated statistics
					Pointer stats = event.getPointerArray(0)[1];
					// get the root key which referring to a special modifiable entry
					long root = BaseClingo.statisticsRoot(stats);
					// set some pseudo-random values in an array
					long values = BaseClingo.statisticsMapAddSubkey(stats, root, "values", StatisticsType.ARRAY);
					for (int i = 0; i < 10; ++i) {
						int random = ThreadLocalRandom.current().nextInt(1, 999999999 + 1);
						value = BaseClingo.statisticsArrayPush(stats, values, StatisticsType.VALUE);
						BaseClingo.statisticsValueSet(stats, value, random);
				        sum += random;
					}
					// add the sum and average of the values in a map under key summary
					long summary = BaseClingo.statisticsMapAddSubkey(stats, root, "summary", StatisticsType.MAP);
					value = BaseClingo.statisticsMapAddSubkey(stats, summary, "sum", StatisticsType.VALUE);
					BaseClingo.statisticsValueSet(stats, value, sum);
					value = BaseClingo.statisticsMapAddSubkey(stats, summary, "avg", StatisticsType.VALUE);
					BaseClingo.statisticsValueSet(stats, value, (double)sum/10);
					break;

				case MODEL:
					break;
					
				default:
					break;
				}
				return true;
			}
		};
		// get a solve handle
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.YIELD, null, new SizeT(), eventHandler, null);
		boolean modelExists = true;
		// loop over all models
		while (modelExists) {
			BaseClingo.solveHandleResume(handle);
			Pointer model = BaseClingo.solveHandleModel(handle);
			if (model != null) {
				checkModel(model);
			} else {
				modelExists = false;
			}
		}
		return BaseClingo.solveHandleGet(handle);
	}

	private void checkModel(Pointer model) {
		// TODO Auto-generated method stub
		
	}

}
