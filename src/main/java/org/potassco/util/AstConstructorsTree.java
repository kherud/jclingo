package org.potassco.util;

import org.potassco.ast.enums.Attribute;
import org.potassco.ast.enums.AttributeType;
import org.potassco.jna.AstArgument;
import org.potassco.jna.AstConstructor;

public class AstConstructorsTree extends PropertyTree {

	public AstConstructorsTree() {
		super("AstConstructors");
	}

	public AstConstructorsTree(AstConstructor[] constructors) {
		this();
		construct(constructors);
	}

	private void construct(AstConstructor[] constructors) {
		for (AstConstructor ac : constructors) {
			this.addNode(ac.name, null, 0);
			this.addAttribute("size", Integer.toString(ac.size));
			AstArgument aa = new AstArgument(ac.arguments);
			AstArgument[] aaArray = (AstArgument[]) aa.toArray(ac.size);
			construct(aaArray);
		}
	}

	private void construct(AstArgument[] aaArray) {
		for (AstArgument aarg : aaArray) {
			Attribute attributeName = org.potassco.ast.enums.Attribute.fromOrdinal(aarg.attribute);
			AttributeType attributeType = org.potassco.ast.enums.AttributeType.fromValue(aarg.type);
			this.addNode(attributeName.name(), null, 1);
			this.addAttribute("ordinal", Integer.toString(aarg.attribute));
			this.addAttribute("type", attributeType.name());
			this.addAttribute("typeValue", Integer.toString(aarg.type));
		}
	}

}
