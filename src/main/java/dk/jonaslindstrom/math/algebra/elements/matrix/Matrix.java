package dk.jonaslindstrom.math.algebra.elements.matrix;

import dk.jonaslindstrom.math.algebra.abstractions.Ring;
import dk.jonaslindstrom.math.algebra.elements.vector.Vector;
import dk.jonaslindstrom.math.functional.IntBinaryFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Matrix<E> extends BiFunction<Vector<E>, Ring<E>, Vector<E>> {

  Vector<E> getColumn(int j);

  Vector<E> getRow(int i);

  E get(int i, int j);

  int getHeight();

  int getWidth();

  Matrix<E> minor(int i, int j);

  Matrix<E> transpose();

  <F> Matrix<F> forEach(Function<E, F> f);

  Iterable<Vector<E>> rows();
  
  boolean equals(Matrix<E> other, BiPredicate<E, E> equality);

  boolean isSquare();

  /**
   * Returns a new larger matrix of size <i>m x n</i> which has this matrix in the top left corner
   * and pads the rest using the given padding value.
   * 
   * @param m
   * @param n
   * @param padding
   * @return
   */
  Matrix<E> extend(int m, int n, E padding);

  /**
   * Returns a new matrix with the given rows and columns from this matrix. The given arrays are
   * assumed to be sorted.
   * 
   * @param rows
   * @param columns
   * @return
   */
  Matrix<E> submatrix(int[] rows, int[] columns);

  /**
   * Returns a new matrix of size <i>(r1-r0) x (c1-c0)</i> with rows <i>r0, ..., r1-1</i> and
   * columns <i>c0, ..., c1-1</i> from this matrix.
   * 
   * @param r0
   * @param r1
   * @param c0
   * @param c1
   * @return
   */
  Matrix<E> submatrix(int r0, int r1, int c0, int c1);

  /**
   * Returns a view of the given matrix. This does not store any values but instead maps operations
   * to this matrix.
   * 
   * @return
   */
  Matrix<E> view();
  
  /**
   * Returns a mutable copy of this matrix.
   * 
   * @return
   */
  MutableMatrix<E> mutable();

  public static <E> Matrix<E> lazy(int m, int n, IntBinaryFunction<E> populator) {
    return new ConstructiveMatrix<>(m, n, populator);
  }

  /**
   * Create copy of this matrix.
   * 
   * @param matrix
   * @return
   */
  public static <E> Matrix<E> copy(Matrix<E> matrix) {
    return new ConcreteMatrix<>(matrix.getHeight(), matrix.getWidth(), matrix::get);
  }

  /**
   * Create a new matrix with the given height, width and entries.
   * 
   * @param m
   * @param n
   * @param populator
   * @return
   */
  public static <E> Matrix<E> of(int m, int n, IntBinaryFunction<E> populator) {
    return new ConcreteMatrix<>(m, n, populator);
  }

  public static <E> Matrix<E> of(int m, IntFunction<ArrayList<E>> rowPopulator) {
    return new ConcreteMatrix<>(
        IntStream.range(0, m).mapToObj(rowPopulator).collect(Collectors.toCollection(ArrayList::new)));
  }

  @SuppressWarnings("unchecked")
  public static <E> Matrix<E> of(E[]... rows) {
    return new ConcreteMatrix<>(rows.length, i -> new ArrayList<>(Arrays.asList(rows[i])));
  }

  @SafeVarargs
  public static <E> Matrix<E> of(ArrayList<E>... rows) {
    return new ConcreteMatrix<>(
        Arrays.stream(rows).collect(Collectors.toCollection(ArrayList::new)));
  }

  /**
   * Convenience function to quickly define (small) matrices. The first parameter is the number of
   * rows and the remaining are the entries which should be divisible by the number of rows.
   * 
   * @param m
   * @param entries
   * @return
   */
  @SafeVarargs
  public static <E> Matrix<E> of(int m, E... entries) {
    assert (entries.length % m == 0);
    int n = entries.length / m;
    return of(m, n, (i, j) -> entries[i * n + j]);
  }

  public static <E> Matrix<E> of(int m, int n, E defaultValue) {
    return new ConcreteMatrix<>(m, n, (x, y) -> defaultValue);
  }

  public static <E> Matrix<E> fromBlocks(Matrix<Matrix<E>> blocks) {
    Matrix<E> tl = blocks.get(0, 0);
    int m = blocks.getHeight() * tl.getHeight();
    int n = blocks.getWidth() * tl.getWidth();

    ArrayList<ArrayList<E>> rows = new ArrayList<>(m);

    for (Vector<Matrix<E>> row : blocks.rows()) {
      for (int i = 0; i < tl.getHeight(); i++) {
        ArrayList<E> entries = new ArrayList<>(n);
        for (Matrix<E> b : row) {
          for (E e : b.getRow(i)) {
            entries.add(e);
          }
        }
        rows.add(entries);
      }
    }
    return new ConcreteMatrix<>(rows);
  }

}
