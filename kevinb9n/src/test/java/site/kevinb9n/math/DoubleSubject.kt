package site.kevinb9n.math

import com.google.common.base.Preconditions
import com.google.common.truth.ComparableSubject
import com.google.common.truth.Fact
import com.google.common.truth.FailureMetadata
import org.checkerframework.checker.nullness.compatqual.NullableDecl

class DoubleSubject(metadata: FailureMetadata, val actual: Double?) :
  ComparableSubject<Double>(metadata, actual) {
  fun isWithin(tolerance: Double): TolerantDoubleComparison {
    return object : TolerantDoubleComparison() {
      override fun of(expected: Double) {
        val actual = this@DoubleSubject.actual!!
        checkTolerance(tolerance)
        if (Math.abs(actual - expected) > Math.abs(tolerance)) {
          this@DoubleSubject.failWithoutActual(
            Fact.fact("expected", expected.toString()),
            butWas(), Fact.fact("outside tolerance", tolerance.toString()))
        }
      }
    }
  }

  fun isNotWithin(tolerance: Double): TolerantDoubleComparison {
    return object : TolerantDoubleComparison() {
      override fun of(expected: Double) {
        val actual = this@DoubleSubject.actual!!
        checkTolerance(tolerance)
        if (Math.abs(actual - expected) <= Math.abs(tolerance)) {
          this@DoubleSubject.failWithoutActual(Fact.fact("expected not to be", expected.toString()),
            butWas(), Fact.fact("within tolerance", tolerance.toString()))
        }
      }
    }
  }

  abstract class TolerantDoubleComparison {
    abstract fun of(expected: Double)
    override fun equals(other: Any?): Boolean {
      throw UnsupportedOperationException("If you meant to compare doubles, use .of(double) instead.")
    }

    override fun hashCode(): Int {
      throw UnsupportedOperationException("Subject.hashCode() is not supported.")
    }
  }

  fun butWas(): Fact {
    return Fact.fact("but was", actualCustomStringRepresentation())
  }

  companion object {
    private val NEG_ZERO_BITS = java.lang.Double.doubleToLongBits(-0.0)
    fun checkTolerance(tolerance: Double) {
      Preconditions.checkArgument(!java.lang.Double.isNaN(tolerance), "tolerance cannot be NaN")
      Preconditions.checkArgument(tolerance >= 0.0, "tolerance (%s) cannot be negative", tolerance)
      Preconditions.checkArgument(java.lang.Double.doubleToLongBits(tolerance) != NEG_ZERO_BITS, "tolerance (%s) cannot be negative", tolerance)
      @Suppress("DIVISION_BY_ZERO")
      Preconditions.checkArgument(tolerance != 1.0 / 0.0, "tolerance cannot be POSITIVE_INFINITY")
    }
  }
}
