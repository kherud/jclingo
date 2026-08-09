package org.potassco.clingo.internal;

import java.lang.foreign.Linker;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * An exception escaping an upcall terminates the JVM, so every stub clingo calls has to be guarded. The guard lives
 * in the method handle {@link Upcalls} composes, which is only a guarantee as long as nobody creates a stub elsewhere.
 */
public class UpcallFactoryArchTest {

    private static final JavaClasses CLASSES = new ClassFileImporter().importPackages("org.potassco.clingo");

    @Test
    public void testOnlyTheUpcallFactoryCreatesUpcallStubs() {
        ArchRule rule = noClasses()
                .that().doNotHaveFullyQualifiedName(Upcalls.class.getName())
                .should(createUpcallStubs());
        rule.check(CLASSES);
    }

    private static ArchCondition<JavaClass> createUpcallStubs() {
        return new ArchCondition<>("create upcall stubs outside of " + Upcalls.class.getSimpleName()) {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                for (JavaMethodCall call : item.getMethodCallsFromSelf()) {
                    if (createsUpcallStub(call)) {
                        events.add(SimpleConditionEvent.violated(call, call.getDescription()));
                    }
                }
            }
        };
    }

    private static boolean createsUpcallStub(JavaMethodCall call) {
        String owner = call.getTargetOwner().getFullName();
        if (owner.equals(Linker.class.getName())) {
            return call.getName().equals("upcallStub");
        }
        // the generated callback classes offer a convenience allocate(Function, Arena) that skips the guard
        return owner.startsWith("org.potassco.clingo.internal.ffi.")
               && call.getName().equals("allocate")
               && call.getTarget().getRawParameterTypes().stream()
                       .anyMatch(parameter -> parameter.getFullName().endsWith("$Function"));
    }
}
