package expr;

public record Exponent(Expr base, double power) implements Expr {

    @Override
    public double evaluate() {
        return Math.pow(base.evaluate(), power);
    }

    @Override
    public String toString() {
        return "(" + base + "^" + power + ")";
    }
}
