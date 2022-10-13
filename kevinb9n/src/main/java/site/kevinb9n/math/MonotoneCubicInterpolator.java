package site.kevinb9n.math;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

/**
 * Monotone cubic spline interpolator. Unlike other Hermite interpolators (such as Catmull-Rom),
 * this interpolator sacrifices continuity of the second derivative for the absence of
 * overshooting (wherein a spline segment overshoots a control point and reverses direction to
 * intersect the point). This implementation is adapted from the JavaScript implementation in
 * Wikipedia (https://en.wikipedia.org/wiki/Monotone_cubic_interpolation#Example_implementation).
 * The algorithm is due to Fritsch & Carlson, (1980) ("Monotone Piecewise Cubic Interpolation".
 * SIAM Journal on Numerical Analysis. SIAM. 17 (2): 238–246). It is the algorithm used by
 * MATLAB's PCHIP interpolator.
 *
 * @author Josh Bloch
 */
public class MonotoneCubicInterpolator {
  /**
   * Returns a monotone cubic spline interpolator for the x-y points represented by the given
   * parallel arrays. The returned function maps its input to an interpolated output value.
   * When applied to any of the given x-values, it will return the corresponding y-value.
   * @param xVals The x-values of the control points for the spline (must be in ascending order)
   * @param yVals The y-values of the control points for the spline
   * @return the interpolator
   * @throws IllegalArgumentException if the lengths of xVals and yVals differ
   * @throws IllegalArgumentException if the lengths of xVals and yVals are less than 4
   * @throws NullPointerException if xVals or yVals are null
   */
  public static DoubleUnaryOperator of(double[] xVals, double[] yVals) {
    if (xVals.length != yVals.length)
      throw new IllegalArgumentException("Array len " + xVals.length + " != " + yVals.length);
    if (xVals.length < 3)
      throw new IllegalArgumentException("Array lengths < 4: " + xVals.length);

    // Add one artificial control point before and after real ones
    double[] x = xVals; // extendControlPoints(xVals);
    double[] y = yVals; // extendControlPoints(yVals);
    int n = x.length;

    // Compute distances between successive x vals, and secants (slopes)
    double[] dy = new double[n - 1]; // Distances between successive y values
    double[] dx = new double[n - 1]; // Distances between successive x values
    double[] m  = new double[n - 1]; // Slopes from one sample point to the next
    for (int i = 0; i < n - 1; i++) {
      dy[i] = y[i + 1] - y[i];
      dx[i] = x[i + 1] - x[i];
      m[i]  = dy[i] / dx[i];
    }

    // Compute degree-1 coefficients
    double[] c1 = new double[n];
    c1[0] = m[0];
    for (int i = 0; i < dx.length - 1; i++) {
      if (m[i] * m[i + 1] > 0) { // i.e., slopes are both positive or both negative
        double common = dx[i] + dx[i + 1];
        c1[i + 1] = 3 * common / ((common + dx[i + 1]) / m[i] + (common + dx[i]) / m[i + 1]);
      }
    }
    c1[c1.length - 1] = m[m.length - 1];

    // Compute degree-2 and degree-3 coefficients
    double[] c2 = new double[n - 1];
    double[] c3 = new double[n - 1];
    for (int i = 0; i < c1.length - 1; i++) {
      double invDx = 1 / dx[i];
      double common = c1[i] + c1[i + 1] - 2 * m[i];
      c2[i] = (m[i] - c1[i] - common) * invDx;
      c3[i] = common * invDx * invDx;
    }

    // This works fine, but does not go out of its way to throw exceptions if x is out of range,
    // which could be done by removing leading and trailing values from x, y, c1, c2, & c3.
    return xVal -> {
      int i = Arrays.binarySearch(x, xVal);
      if (i >= 0) // Exact match to control point
        return y[i];
      i = ~i - 1; // Compute segment index (in [0, n-2]) from insertion point
      try {
        double diff = xVal - x[i];
        double diff2 = diff * diff;
        return y[i] + c1[i] * diff + c2[i] * diff2 + c3[i] * diff * diff2;
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new IllegalArgumentException("x out of bounds: " + xVal);
      }
    };
  }

  /**
   * Adds an artificial control point before and after the given array of control points (whose
   * length must be at least two). The added control point is the same distance away from the
   * first (or last) control point as it is from its successor (or predecessor).
   */
  private static double[] extendControlPoints(double[] p) {
    double[] result = new double[p.length + 2];
    result[0] = 2 * p[0] - p[1];
    System.arraycopy(p, 0, result, 1, p.length);
    result[p.length] = 2 * p[p.length - 1] - p[p.length - 2];
    return result;
  }
}
