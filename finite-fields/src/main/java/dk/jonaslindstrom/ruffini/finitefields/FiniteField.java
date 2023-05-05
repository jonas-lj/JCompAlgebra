package dk.jonaslindstrom.ruffini.finitefields;

import dk.jonaslindstrom.ruffini.common.abstractions.Field;
import dk.jonaslindstrom.ruffini.common.algorithms.EuclideanAlgorithm;
import dk.jonaslindstrom.ruffini.common.structures.QuotientRing;
import dk.jonaslindstrom.ruffini.polynomials.elements.Polynomial;
import dk.jonaslindstrom.ruffini.polynomials.structures.PolynomialRing;

public class FiniteField extends QuotientRing<Polynomial<Integer>>
        implements Field<Polynomial<Integer>> {

    private final String stringRepresentation;
    private final int p;
    private final PrimeField baseField;

    /**
     * Create a new finite field of order <i>p<sup>n</sup></i> using default representation.
     */
    public FiniteField(int p, int n) {
        this(new PrimeField(p), getIrreduciblePolynomial(p, n));
    }

    /**
     * Create a finite field as a field of prime order module an irreducible polynomial.
     */
    public FiniteField(PrimeField baseField, Polynomial<Integer> mod) {
        super(new PolynomialRing<>(baseField), mod);

        this.p = baseField.getModulus();
        this.stringRepresentation = String.format("GF(%s^{%s})", baseField.getModulus(),
                mod.degree());
        this.baseField = baseField;
    }

    private static Polynomial<Integer> getIrreduciblePolynomial(int p, int degree) {

        switch (p) {
            case 2:

                switch (degree) {
                    case 1:
                        return Polynomial.of(0, 1);

                    case 2:
                        return Polynomial.of(1, 1, 1);

                    case 3:
                        return Polynomial.of(1, 0, 1, 1);

                    case 4:
                        return Polynomial.of(1, 1, 0, 0, 1);

                    case 5:
                        return Polynomial.of(1, 0, 1, 0, 0, 1);

                    case 6:
                        return Polynomial.of(1, 1, 0, 0, 0, 0, 1);

                    case 7:
                        return Polynomial.of(1, 1, 0, 0, 0, 0, 0, 1);

                    case 8:
                        return Polynomial.of(1, 1, 0, 1, 1, 0, 0, 0, 1);

                    default:
                        return null;
                }

            case 3:
                switch (degree) {
                    case 1:
                        return Polynomial.of(0, 1);

                    case 2:
                        return Polynomial.of(2, 2, 1);

                    case 3:
                        return Polynomial.of(1, 2, 0, 1);

                    case 4:
                        return Polynomial.of(2, 1, 0, 0, 1);

                    default:
                        return null;
                }

            default:
                return null;

        }

    }

    public Polynomial<Integer> element(Integer... coefficients) {
        return Polynomial.of(coefficients);
    }

    @Override
    public Polynomial<Integer> invert(Polynomial<Integer> a) {
        return new EuclideanAlgorithm<>((PolynomialRing<Integer>) super.ring)
                .applyExtended(a, mod).x();
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    public int getPrime() {
        return p;
    }

    public int getExponent() {
        return this.mod.degree();
    }

    public Polynomial<Integer> createElement(Integer... c) {
        return Polynomial.of(c);
    }

}
