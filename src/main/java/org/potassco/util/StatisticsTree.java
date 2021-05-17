package org.potassco.util;

import org.potassco.enums.StatisticsType;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.SizeT;

import com.sun.jna.Pointer;

/**
 * Typically used after solving.
 * @author Josef Schneeberger
 *
 */
public class StatisticsTree extends PropertyTree {

	public StatisticsTree() {
		super("ClingoStatistics");
	}

	public StatisticsTree(Pointer stats, long statsKey) {
		this();
		construct(stats, statsKey, 0);
	}

	private void construct(Pointer stats, long key, int depth) {
		StatisticsType type = BaseClingo.statisticsType(stats, key);
		switch (type) {
		case VALUE: {
			double value = BaseClingo.statisticsValueGet(stats, key);
			this.addValue(value, depth);
			break;
		}
		case ARRAY: {
			SizeT size = BaseClingo.statisticsArraySize(stats, key);
			for (int j = 0; j < size.intValue(); j++) {
				long subkey = BaseClingo.statisticsArrayAt(stats, key, new SizeT(j));
				this.addIndex(j, null, depth);
		        // recursively print subentry
				construct(stats, subkey, depth + 1);
			}
			break;
		}
		case MAP: {
			SizeT size = BaseClingo.statisticsMapSize(stats, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.statisticsMapSubkeyName(stats, key, new SizeT(j));
				long subkey = BaseClingo.statisticsMapAt(stats, key, name);
				this.addNode(name, null, depth);
		        // recursively print subentry
				construct(stats, subkey, depth + 1);
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

}
