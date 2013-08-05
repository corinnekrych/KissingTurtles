package dsl

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer

public class GameCustomizer extends CompilationCustomizer {
    def step = 0

    public GameCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(final SourceUnit source,
                     final GeneratorContext context,
                     final ClassNode classNode)
    throws CompilationFailedException {
        def methodCalls = []
        int i = 0;
        def ast = source.getAST();
        BlockStatement gameScript
        ast.classes.each {
            it.methods.each {
                if (it.code instanceof BlockStatement
                        && it.name == "run") {
                    gameScript = it.code
                }
            }
        }
        def methodCallVisitor = new MethodCallVisitor()
        methodCallVisitor.visitBlockStatement(gameScript)

        if (methodCallVisitor.i > 3) {
            throw new Throwable("Limit of allowed statements exceeded!")
        }
    }


}

class MethodCallVisitor extends ClassCodeVisitorSupport {
    int i = 0;
    @Override
    protected SourceUnit getSourceUnit() {
        return null
}

    @Override
    public void visitMethodCallExpression(MethodCallExpression expression) {
        if(expression.getMethodAsString() in ["move", "ask"] ) {
           i++
        }
        super.visitMethodCallExpression(expression)
    }

}