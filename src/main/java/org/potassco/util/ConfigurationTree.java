package org.potassco.util;

import org.potassco.jna.BaseClingo;
import org.potassco.jna.SizeT;

import com.sun.jna.Pointer;

/**
 * Typically used after solving.
 * @author Josef Schneeberger
 *
 */
public class ConfigurationTree extends PropertyTree {

	public ConfigurationTree() {
		super("ClingoConfiguration");
	}

	public ConfigurationTree(Pointer conf, int rootKey) {
		this();
		checkConfiguration(conf, rootKey, 0);
	}

	private void checkConfiguration(Pointer conf, int key, int depth) {
		int type = BaseClingo.configurationType(conf, key);
		switch (type) {
		case 1: {
			String value = BaseClingo.configurationValueGet(conf, key);
			this.addValue(value, depth);
			break;
		}
		case 2: {
			SizeT size = BaseClingo.configurationArraySize(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
				long subkey = BaseClingo.configurationArrayAt(conf, key, new SizeT(j));
				String desc = BaseClingo.configurationDescription(conf, key);
				this.addIndex(j, desc, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1);
			}
			break;
		}
		case 4: {
			SizeT size = BaseClingo.configurationMapSize(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.configurationMapSubkeyName(conf, key, new SizeT(j));
				long subkey = BaseClingo.configurationMapAt(conf, key, name);
				String desc = BaseClingo.configurationDescription(conf, key);
				this.addNode(name, desc, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1);
			}
			break;
		}
		case 5: { // like 1
			String value = BaseClingo.configurationValueGet(conf, key);
			this.addValue(value, depth);
			break;
		}
		case 6: {
//			SizeT as = BaseClingo.configurationArraySize(conf, key);
			SizeT size = BaseClingo.configurationMapSize(conf, key);
			for (int j = 0; j < size.intValue(); j++) {
		        // get and print map name (with prefix for readability)
				String name = BaseClingo.configurationMapSubkeyName(conf, key, new SizeT(j));
				int arraySubkey = BaseClingo.configurationArrayAt(conf, key, new SizeT(j));
				long subkey = BaseClingo.configurationMapAt(conf, key, name);
				String desc = BaseClingo.configurationDescription(conf, key);
				String value = BaseClingo.configurationValueGet(conf, Math.toIntExact(subkey));
				
				int arraySkCt = BaseClingo.configurationType(conf, arraySubkey);
//				checkConfiguration(conf, arraySubkey, depth + 1, tree);
				
				int mapSkCt = BaseClingo.configurationType(conf, Math.toIntExact(subkey));

				this.addNode(name, desc, depth);
		        // recursively print subentry
				checkConfiguration(conf, Math.toIntExact(subkey), depth + 1);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}

}
