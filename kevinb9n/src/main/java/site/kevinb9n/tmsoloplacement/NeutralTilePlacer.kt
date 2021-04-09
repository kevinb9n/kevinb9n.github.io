package site.kevinb9n.tmsoloplacement

// This is the part not implemented yet - the different strategies

enum class ZeroPolicy { REDRAW, TREAT_AS_ONE, COUNT_FROM_ZERO, WRAP }
enum class OverflowPolicy { REDRAW, TREAT_AS_MAX, WRAP }
enum class SkipPolicy { SKIP_ALL_UNAVAIL, ADVANCE_IF_NEEDED }

class NeutralTilePlacer(
  val zeroPolicy: ZeroPolicy,
  val overflowPolicy: OverflowPolicy,
  val skipPolicy: SkipPolicy) {


}