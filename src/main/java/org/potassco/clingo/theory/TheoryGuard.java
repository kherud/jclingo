package org.potassco.clingo.theory;

public class TheoryGuard {

    private final String connective;
    private final TheoryTerm theoryTerm;

    public TheoryGuard(String connective, TheoryTerm theoryTerm) {
        this.connective = connective;
        this.theoryTerm = theoryTerm;
    }

    public String getConnective() {
        return connective;
    }

    public TheoryTerm getTheoryTerm() {
        return theoryTerm;
    }
}
