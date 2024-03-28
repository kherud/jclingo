package org.potassco.clingo.ast.nodes;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.internal.Clingo;

public class Comment extends Ast {

	public Comment(Pointer ast) {
		super(ast);
	}

	public Comment(Location location, String value, int commentType) {
		super(create(location, value, commentType));
	}

	public Location getLocation() {
		Location.ByReference locationByReference = new Location.ByReference();
		Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, AstAttribute.LOCATION.ordinal(), locationByReference));
		return locationByReference;
	}

	public String getValue() {
		String[] stringByRef = new String[1];
		Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_string(ast, AstAttribute.VALUE.ordinal(), stringByRef));
		return stringByRef[0];
	}

	public int getCommentType() {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, AstAttribute.COMMENT_TYPE.ordinal(), intByReference));
		return intByReference.getValue();
	}

	public void setLocation(Location location) {
		Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AstAttribute.LOCATION.ordinal(), location));
	}

	public void setValue(String value) {
		Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_string(ast, AstAttribute.VALUE.ordinal(), value));
	}

	public void setCommentType(int commentType) {
		Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, AstAttribute.COMMENT_TYPE.ordinal(), commentType));
	}

	private static Pointer create(Location location, String value, int commentType) {
		PointerByReference pointerByReference = new PointerByReference();
		Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.COMMENT.ordinal(), pointerByReference, location, value, commentType));
		return pointerByReference.getValue();
	}
}
