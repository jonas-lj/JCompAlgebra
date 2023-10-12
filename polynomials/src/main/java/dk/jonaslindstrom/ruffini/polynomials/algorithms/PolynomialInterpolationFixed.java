package dk.jonaslindstrom.ruffini.polynomials.algorithms;

import dk.jonaslindstrom.ruffini.common.abstractions.Field;
import dk.jonaslindstrom.ruffini.common.util.ArrayUtils;
import dk.jonaslindstrom.ruffini.polynomials.elements.Polynomial;
import dk.jonaslindstrom.ruffini.polynomials.structures.PolynomialRing;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PolynomialInterpolationFixed<E> implements Function<List<E>, Polynomial<E>> {

    private final PolynomialRing<E> polynomialRing;
    private final BinaryTree<Polynomial<E>> tree;
    private final List<E> x;

    public PolynomialInterpolationFixed(PolynomialRing<E> polynomialRing, List<E> x) {
        this.polynomialRing = polynomialRing;
        this.x = x;
        this.tree = new BinaryTree<>(ArrayUtils.populate(x.size(), i -> Polynomial.of(
                polynomialRing.getRing().negate(x.get(i)),
                polynomialRing.getRing().identity())), polynomialRing::multiply);
    }

    @Override
    public Polynomial<E> apply(List<E> y) {
        if (y.size() != this.x.size()) {
            throw new IllegalArgumentException("x and y must have the same size");
        }
        Field<E> field = this.polynomialRing.getBaseField();
        Polynomial<E> ľ = interpolationTree(Collections.nCopies(x.size(), field.identity()));
        List<E> evaluations = this.tree.evaluate(ľ, (a, b) -> polynomialRing.divisionWithRemainder(a, b).getSecond()).stream().map(Polynomial::getConstant).toList();
        List<E> l = ArrayUtils.populate(x.size(), i -> field.divide(y.get(i), evaluations.get(i)));
        return interpolationTree(l);
    }

    private Polynomial<E> interpolationTree(List<E> y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("x and y must have the same size");
        }
        return this.tree.evaluateFromLeafs(y.stream().map(Polynomial::constant).toList(),
                (a, b) -> polynomialRing.add(
                            polynomialRing.multiply(b.first, a.second),
                            polynomialRing.multiply(a.first, b.second)));
    }
}
