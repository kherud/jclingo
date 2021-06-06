package org.potassco.jna;

import static org.junit.Assert.*;

import org.junit.Test;
import org.potassco.jna.ClingoLibrary.AstCallback;
import org.potassco.util.AstConstructorsTree;
import org.potassco.util.PropertyTree;
import org.potassco.ast.enums.Type;
import org.potassco.cpp.clingo_h;
import org.potassco.enums.SolveMode;

import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class AstConstructorsTest extends CheckModels {

	/** {@link clingo_h#g_clingo_ast_constructors} */
	@Test
	public void testAstTypes() {
		NativeLibrary lib = NativeLibrary.getInstance("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll");
		Pointer paddr = lib.getGlobalVariableAddress("g_clingo_ast_constructors");
		AstConstructors astContstructors = new AstConstructors(paddr);
		AstConstructor ac1 = new AstConstructor(astContstructors.constructors);
		AstConstructor[] constructors = (AstConstructor[]) ac1.toArray(astContstructors.size.intValue());
		PropertyTree tree = new AstConstructorsTree(constructors);
		for (AstConstructor ac : constructors) {
//			System.out.println(ac.name);
			AstArgument aa = new AstArgument(ac.arguments);
			AstArgument[] aaArray = (AstArgument[]) aa.toArray(ac.size);
			for (AstArgument aarg : aaArray) {
//				System.out.println("  "
//						+ aarg.attribute + "="
//						+ org.potassco.ast.enums.Attribute.fromOrdinal(aarg.attribute)
//						+ " " + aarg.type + "="
//						+ org.potassco.ast.enums.AttributeType.fromValue(aarg.type));
			}
		}
		tree.showXml();
		assertEquals("2", tree.queryXpathAsString("/AstConstructors/id/@size"));
		assertEquals("2", tree.queryXpathAsString("/AstConstructors[40]/name()"));
	}
/*
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<AstConstructors>
    <id size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
    </id>
    <variable size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
    </variable>
    <symbolic_term size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <SYMBOL ordinal="40" type="SYMBOL" typeValue="1"/>
    </symbolic_term>
    <unary_operation size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <OPERATOR_TYPE ordinal="30" type="NUMBER" typeValue="0"/>
        <ARGUMENT ordinal="0" type="AST" typeValue="4"/>
    </unary_operation>
    <binary_operation size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <OPERATOR_TYPE ordinal="30" type="NUMBER" typeValue="0"/>
        <LEFT ordinal="21" type="AST" typeValue="4"/>
        <RIGHT ordinal="35" type="AST" typeValue="4"/>
    </binary_operation>
    <interval size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <LEFT ordinal="21" type="AST" typeValue="4"/>
        <RIGHT ordinal="35" type="AST" typeValue="4"/>
    </interval>
    <function size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <ARGUMENTS ordinal="1" type="AST_ARRAY" typeValue="7"/>
        <EXTERNAL ordinal="14" type="NUMBER" typeValue="0"/>
    </function>
    <pool size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ARGUMENTS ordinal="1" type="AST_ARRAY" typeValue="7"/>
    </pool>
    <csp_product size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <COEFFICIENT ordinal="9" type="AST" typeValue="4"/>
        <VARIABLE ordinal="44" type="AST" typeValue="4"/>
    </csp_product>
    <csp_sum size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <COEFFICIENT ordinal="9" type="AST" typeValue="4"/>
        <VARIABLE ordinal="44" type="AST" typeValue="4"/>
    </csp_sum>
    <csp_guard size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <COMPARISON ordinal="10" type="NUMBER" typeValue="0"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
    </csp_guard>
    <boolean_constant size="1">
        <VALUE ordinal="43" type="NUMBER" typeValue="0"/>
    </boolean_constant>
    <symbolic_atom size="1">
        <SYMBOL ordinal="40" type="AST" typeValue="4"/>
    </symbolic_atom>
    <comparison size="3">
        <COMPARISON ordinal="10" type="NUMBER" typeValue="0"/>
        <LEFT ordinal="21" type="AST" typeValue="4"/>
        <RIGHT ordinal="35" type="AST" typeValue="4"/>
    </comparison>
    <csp_literal size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
        <GUARDS ordinal="18" type="AST_ARRAY" typeValue="7"/>
    </csp_literal>
    <aggregate_guard size="2">
        <COMPARISON ordinal="10" type="NUMBER" typeValue="0"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
    </aggregate_guard>
    <conditional_literal size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <LITERAL ordinal="23" type="AST" typeValue="4"/>
        <CONDITION ordinal="11" type="AST_ARRAY" typeValue="7"/>
    </conditional_literal>
    <aggregate size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <LEFT_GUARD ordinal="22" type="OPTIONAL_AST" typeValue="5"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
        <RIGHT_GUARD ordinal="36" type="OPTIONAL_AST" typeValue="5"/>
    </aggregate>
    <body_aggregate_element size="2">
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
        <CONDITION ordinal="11" type="AST_ARRAY" typeValue="7"/>
    </body_aggregate_element>
    <body_aggregate size="5">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <LEFT_GUARD ordinal="22" type="OPTIONAL_AST" typeValue="5"/>
        <FUNCTION ordinal="16" type="NUMBER" typeValue="0"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
        <RIGHT_GUARD ordinal="36" type="OPTIONAL_AST" typeValue="5"/>
    </body_aggregate>
    <head_aggregate_element size="2">
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
        <CONDITION ordinal="11" type="AST" typeValue="4"/>
    </head_aggregate_element>
    <head_aggregate size="5">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <LEFT_GUARD ordinal="22" type="OPTIONAL_AST" typeValue="5"/>
        <FUNCTION ordinal="16" type="NUMBER" typeValue="0"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
        <RIGHT_GUARD ordinal="36" type="OPTIONAL_AST" typeValue="5"/>
    </head_aggregate>
    <disjunction size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
    </disjunction>
    <disjoint_element size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
        <CONDITION ordinal="11" type="AST_ARRAY" typeValue="7"/>
    </disjoint_element>
    <disjoint size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
    </disjoint>
    <theory_sequence size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <SEQUENCE_TYPE ordinal="38" type="NUMBER" typeValue="0"/>
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
    </theory_sequence>
    <theory_function size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <ARGUMENTS ordinal="1" type="AST_ARRAY" typeValue="7"/>
    </theory_function>
    <theory_unparsed_term_element size="2">
        <OPERATORS ordinal="31" type="STRING_ARRAY" typeValue="6"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
    </theory_unparsed_term_element>
    <theory_unparsed_term size="2">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
    </theory_unparsed_term>
    <theory_guard size="2">
        <OPERATOR_NAME ordinal="29" type="STRING" typeValue="3"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
    </theory_guard>
    <theory_atom_element size="2">
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
        <CONDITION ordinal="11" type="AST_ARRAY" typeValue="7"/>
    </theory_atom_element>
    <theory_atom size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
        <ELEMENTS ordinal="13" type="AST_ARRAY" typeValue="7"/>
        <GUARD ordinal="17" type="OPTIONAL_AST" typeValue="5"/>
    </theory_atom>
    <literal size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <SIGN ordinal="39" type="NUMBER" typeValue="0"/>
        <ATOM ordinal="3" type="AST" typeValue="4"/>
    </literal>
    <theory_operator_definition size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <PRIORITY ordinal="34" type="NUMBER" typeValue="0"/>
        <OPERATOR_TYPE ordinal="30" type="NUMBER" typeValue="0"/>
    </theory_operator_definition>
    <theory_term_definition size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <OPERATORS ordinal="31" type="AST_ARRAY" typeValue="7"/>
    </theory_term_definition>
    <theory_guard_definition size="2">
        <OPERATORS ordinal="31" type="STRING_ARRAY" typeValue="6"/>
        <TERM ordinal="41" type="STRING" typeValue="3"/>
    </theory_guard_definition>
    <theory_atom_definition size="6">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ATOM_TYPE ordinal="5" type="NUMBER" typeValue="0"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <ARITY ordinal="2" type="NUMBER" typeValue="0"/>
        <TERM ordinal="41" type="STRING" typeValue="3"/>
        <GUARD ordinal="17" type="OPTIONAL_AST" typeValue="5"/>
    </theory_atom_definition>
    <rule size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <HEAD ordinal="19" type="AST" typeValue="4"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
    </rule>
    <definition size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <VALUE ordinal="43" type="AST" typeValue="4"/>
        <IS_DEFAULT ordinal="20" type="NUMBER" typeValue="0"/>
    </definition>
    <show_signature size="5">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <ARITY ordinal="2" type="NUMBER" typeValue="0"/>
        <POSITIVE ordinal="33" type="NUMBER" typeValue="0"/>
        <CSP ordinal="12" type="NUMBER" typeValue="0"/>
    </show_signature>
    <show_term size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <TERM ordinal="41" type="AST" typeValue="4"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
        <CSP ordinal="12" type="NUMBER" typeValue="0"/>
    </show_term>
    <minimize size="5">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <WEIGHT ordinal="45" type="AST" typeValue="4"/>
        <PRIORITY ordinal="34" type="AST" typeValue="4"/>
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
    </minimize>
    <script size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <SCRIPT_TYPE ordinal="37" type="NUMBER" typeValue="0"/>
        <CODE ordinal="8" type="STRING" typeValue="3"/>
    </script>
    <program size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <PARAMETERS ordinal="32" type="AST_ARRAY" typeValue="7"/>
    </program>
    <external size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ATOM ordinal="3" type="AST" typeValue="4"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
        <EXTERNAL_TYPE ordinal="15" type="AST" typeValue="4"/>
    </external>
    <edge size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NODE_U ordinal="27" type="AST" typeValue="4"/>
        <NODE_V ordinal="28" type="AST" typeValue="4"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
    </edge>
    <heuristic size="6">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ATOM ordinal="3" type="AST" typeValue="4"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
        <BIAS ordinal="6" type="AST" typeValue="4"/>
        <PRIORITY ordinal="34" type="AST" typeValue="4"/>
        <MODIFIER ordinal="25" type="AST" typeValue="4"/>
    </heuristic>
    <project_atom size="3">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <ATOM ordinal="3" type="AST" typeValue="4"/>
        <BODY ordinal="7" type="AST_ARRAY" typeValue="7"/>
    </project_atom>
    <project_signature size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <ARITY ordinal="2" type="NUMBER" typeValue="0"/>
        <POSITIVE ordinal="33" type="NUMBER" typeValue="0"/>
    </project_signature>
    <defined size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <ARITY ordinal="2" type="NUMBER" typeValue="0"/>
        <POSITIVE ordinal="33" type="NUMBER" typeValue="0"/>
    </defined>
    <theory_definition size="4">
        <LOCATION ordinal="24" type="LOCATION" typeValue="2"/>
        <NAME ordinal="26" type="STRING" typeValue="3"/>
        <TERMS ordinal="42" type="AST_ARRAY" typeValue="7"/>
        <ATOMS ordinal="4" type="AST_ARRAY" typeValue="7"/>
    </theory_definition>
</AstConstructors>


 */
}
