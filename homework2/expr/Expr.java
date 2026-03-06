package expr;

public sealed interface Expr
        permits Constant, Negate, Addition, Multiplication, Exponent {

    double evaluate();
}
