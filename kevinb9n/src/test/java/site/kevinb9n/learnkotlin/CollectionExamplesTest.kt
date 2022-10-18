package site.kevinb9n.learnkotlin

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class CollectionExamplesTest {
  @Suppress("USELESS_CAST")
  @Test fun test() {
    val c: Collection<Int> = listOf(1, 2, 3)

    assertThat(c.all { it > 0 }).isTrue();
    assertThat(c.all { it > 1 }).isFalse();

    assertThat(c.any()).isTrue()
    assertThat(listOf<Int>().any()).isFalse()

    assertThat(c.any { it <= 1 }).isTrue();
    assertThat(c.any { it <= 0 }).isFalse();

    c.asIterable() as Iterable<Int>
    c.asSequence() as Sequence<Int>

    // key expr "to" val expr
    c.associate(transform = { "foo $it" to it + 0.1 }) as Map<String, Double>
    c.associateBy(keySelector = { "foo $it" }, valueTransform = { it + 0.1 }) as Map<String, Double>
    // key expr to actual value, last wins
    c.associateBy(keySelector = { "foo $it" }) as Map<String, Int>
    // actual key to value expr
    c.associateWith(valueSelector = { it + 0.1 }) as Map<Int, Double>

    // those again but writing into a mutable map
    c.associateTo(destination = mutableMapOf(), transform = { "foo $it" to it + 0.1 }) as MutableMap<String, Double>
    c.associateByTo(destination = mutableMapOf(), keySelector = { "foo $it" }, valueTransform = { it + 0.1 }) as MutableMap<String, Double>
    c.associateByTo(destination = mutableMapOf(), keySelector = { "foo $it" }) as MutableMap<String, Int>
    c.associateWithTo(destination = mutableMapOf(), valueSelector = { it + 0.1 }) as MutableMap<Int, Double>

    c.average() as Double // numeric types only
    c.chunked(size = 5) as List<List<Int>>
    c.chunked(size = 5, transform = { it.average() }) as List<Double>
    c.contains(element = 2) as Boolean
    c.containsAll(elements = listOf(1, 3, 5)) as Boolean
    c.count() as Int
    c.count(predicate = { it > 0 }) as Int
    c.distinct() as List<Int>
    c.distinctBy(selector = { it / 2 }) as List<Int>
    c.drop(n = 5) as List<Int>
    c.dropWhile(predicate = { it > 0 }) as List<Int>
    c.elementAt(index = 2) as Int
    c.elementAtOrElse(index = 5, defaultValue = { it + 0.1 }) as Number
    c.elementAtOrNull(index = 5) as Int?
    c.equals(listOf(1, 2, 4)) as Boolean
    c.filter(predicate = { it > 0 }) as List<Int>

    c.groupBy(keySelector = { "foo $it" }) as Map<String, List<Int>>
    c.groupingBy(keySelector = { "foo $it" }) as Grouping<Int, String>

    c.ifEmpty(defaultValue = { listOf(42).asIterable() }) as Iterable<Int>

    c.indices as IntRange
    c.isNotEmpty() as Boolean // same as any()
    c.plus(5)
    c.plusElement(5)


  }
}
