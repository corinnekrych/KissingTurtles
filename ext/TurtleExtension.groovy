import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.SourceUnit

class MyVisitor extends ClassCodeVisitorSupport {
    def var
    def ignore = false
    MyVisitor(var) {
        this.var = var
    }
    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }

    // Assign to a an array seen as
    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        if (var.name == expression.text) {
            ignore = true
        }
    }

}

unresolvedVariable { var ->
    if (var.name in ['left', 'right', 'up', 'down', 'to', 'kiss']) {
        storeType(var, classNodeFor(dsl.Direction))
        handled = true
    }
    def myVisitor = new MyVisitor(var)
    context.source.AST.statementBlock.visit(myVisitor)
    if (myVisitor.ignore) {
        storeType(var, STRING_TYPE)
        handled = true
    }
}

methodNotFound { receiver, name, argList, argTypes, call ->
    if (name in ['by', 'move', 'meet']) {
       return newMethod(name, classNodeFor(dsl.Turtle))
    }
    if (name in ['ask', 'assign']) {
        return newMethod(name, LIST_TYPE)
    }
}

// return value not type

// return more error Type check string in method call

//setup {
//    context.pushErrorCollector()
//}
//
//finish {
//    def ec = context.popErrorCollector()
//    def oc = context.errorCollector
//    ec.errors.each {
//        oc.addError(new SimpleMessage('Boum', null)      )
//    }
//}



//@groovy.transform.Field def var = "toto"
//4.times {
//
//    ask toto assign to name
//    when (name == "Franklin) {
//}
//ask "what's your birthday?" assign to date
//}


//use future in AST transform
//

// ext module

// a la scala home() -> home

// delegate + @DelegateTo -> See guillaume

// tests for DSL