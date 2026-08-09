package org.potassco.clingo.ast.nodes;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.CommentType;
import org.potassco.clingo.ast.Location;

public class Comment extends Ast {

	public Comment(MemorySegment ast) {
		super(ast);
	}

	public Comment(Location location, String value, CommentType commentType) {
		super(create(location, value, commentType.getValue()));
	}

	public Location getLocation() {
		return getLocation(AstAttribute.LOCATION);
	}

	public String getValue() {
		return getString(AstAttribute.VALUE);
	}

	public CommentType getCommentType() {
		return CommentType.fromValue(getNumber(AstAttribute.COMMENT_TYPE));
	}

	public void setLocation(Location location) {
		setLocation(AstAttribute.LOCATION, location);
	}

	public void setValue(String value) {
		setString(AstAttribute.VALUE, value);
	}

	public void setCommentType(CommentType commentType) {
		setNumber(AstAttribute.COMMENT_TYPE, commentType.getValue());
	}

	private static MemorySegment create(Location location, String value, int commentType) {
		return build(AstType.COMMENT, location, value, commentType);
	}
}
