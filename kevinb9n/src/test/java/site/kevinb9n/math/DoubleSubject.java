package site.kevinb9n.math;

import static java.lang.Math.abs;

import com.google.common.base.Preconditions;
import com.google.common.truth.ComparableSubject;
import com.google.common.truth.Fact;
import com.google.common.truth.FailureMetadata;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class DoubleSubject extends ComparableSubject<Double> {
  private static final long NEG_ZERO_BITS = Double.doubleToLongBits(-0.0D);
  private final Double actual;

  final Fact butWas() {
    return Fact.fact("but was", actualCustomStringRepresentation());
  }

  DoubleSubject(FailureMetadata metadata, @NullableDecl Double actual) {
    super(metadata, actual);
    this.actual = actual;
  }

  public DoubleSubject.TolerantDoubleComparison isWithin(final double tolerance) {
    return new DoubleSubject.TolerantDoubleComparison() {
      public void of(double expected) {
        Double actual = DoubleSubject.this.actual;
        Preconditions.checkNotNull(actual, "actual value cannot be null. tolerance=%s expected=%s", tolerance, expected);
        DoubleSubject.checkTolerance(tolerance);
        if (!(abs(actual - expected) <= abs(tolerance))) {
          DoubleSubject.this.failWithoutActual(
            Fact.fact("expected", String.valueOf(expected)),
            DoubleSubject.this.butWas(), Fact.fact("outside tolerance", String.valueOf(tolerance)));
        }
      }
    };
  }

  public DoubleSubject.TolerantDoubleComparison isNotWithin(final double tolerance) {
    return new DoubleSubject.TolerantDoubleComparison() {
      public void of(double expected) {
        Double actual = DoubleSubject.this.actual;
        Preconditions.checkNotNull(actual, "actual value cannot be null. tolerance=%s expected=%s", tolerance, expected);
        DoubleSubject.checkTolerance(tolerance);
        if (!(abs(actual - expected) > abs(tolerance))) {
          DoubleSubject.this.failWithoutActual(Fact.fact("expected not to be",
            String.valueOf(expected)),
            DoubleSubject.this.butWas(), Fact.fact("within tolerance", String.valueOf(tolerance)));
        }

      }
    };
  }

  static void checkTolerance(double tolerance) {
    Preconditions.checkArgument(!Double.isNaN(tolerance), "tolerance cannot be NaN");
    Preconditions.checkArgument(tolerance >= 0.0D, "tolerance (%s) cannot be negative", tolerance);
    Preconditions.checkArgument(Double.doubleToLongBits(tolerance) != NEG_ZERO_BITS, "tolerance (%s) cannot be negative", tolerance);
    Preconditions.checkArgument(tolerance != 1.0D / 0.0, "tolerance cannot be POSITIVE_INFINITY");
  }

  public abstract static class TolerantDoubleComparison {
    public abstract void of(double var1);

    /** @deprecated */
    @Deprecated
    public boolean equals(@NullableDecl Object o) {
      throw new UnsupportedOperationException("If you meant to compare doubles, use .of(double) instead.");
    }

    /** @deprecated */
    @Deprecated
    public int hashCode() {
      throw new UnsupportedOperationException("Subject.hashCode() is not supported.");
    }
  }
}
