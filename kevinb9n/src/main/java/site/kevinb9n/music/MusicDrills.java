package site.kevinb9n.music;

import com.google.common.math.IntMath;
import java.io.Console;
import java.util.*;
import com.google.common.collect.*;

import static site.kevinb9n.MusicDrills.IntervalType.*;
import static site.kevinb9n.music.MusicDrills.IntervalType.AUGMENTED;
import static site.kevinb9n.music.MusicDrills.IntervalType.DIMINISHED;
import static site.kevinb9n.music.MusicDrills.IntervalType.MAJOR;
import static site.kevinb9n.music.MusicDrills.IntervalType.MINOR;
import static site.kevinb9n.music.MusicDrills.IntervalType.PERFECT;


class MusicDrills {

  /**
   * A note with no accidentals (C D E F G A or B).
   */
  enum BaseNote {
    C(0), D(2), E(4), F(5), G(7), A(9), B(11);

    static BaseNote fromString(String s) {
      for (BaseNote baseNote : values()) {
        if (baseNote.toString().equals(s)) {
          return baseNote;
        }
      }
      throw new IllegalArgumentException();
    }

    final int semitonesAboveC;

    private BaseNote(int semitonesAboveC) {
      this.semitonesAboveC = semitonesAboveC;
    }
  }

  enum Accidental {
    DOUBLE_FLAT(-2, "𝄫", "bb"),
    FLAT(-1, "♭", "b"),
    NATURAL(0, "", ""), // ♮
    SHARP(1, "♯", "#"),
    DOUBLE_SHARP(2, "𝄪", "##");

    final int semitoneDelta;
    final String notation;
    final String asciified;

    Accidental(int semitoneDelta, String notation, String asciified) {
      this.semitoneDelta = semitoneDelta;
      this.notation = notation;
      this.asciified = asciified;
    }

    static Accidental fromString(String s) {
      for (Accidental accidental : values()) {
        if (accidental.notation.equals(s)) {
          return accidental;
        }
      }
      throw new IllegalArgumentException();
    }
  }

  enum BaseInterval {
    FIRST(0), SECOND(1, 2), THIRD(3, 4), FOURTH(5), FIFTH(7), SIXTH(8, 9), SEVENTH(10, 11);

    static BaseInterval forOrdinalName(int ordinalName) {
      return values()[ordinalName - 1 % 7];
    }

    boolean isPerfect;
    int semitonesMinor;
    int semitonesMajor;

    BaseInterval(int semitonesPerfect) {
      this.isPerfect = true;
      this.semitonesMinor = semitonesPerfect;
      this.semitonesMajor = semitonesPerfect;
    }

    BaseInterval(int semitonesMinor, int semitonesMajor) {
      this.isPerfect = false;
      this.semitonesMinor = semitonesMinor;
      this.semitonesMajor = semitonesMajor;
    }
  }

  enum IntervalType {DIMINISHED, MINOR, PERFECT, MAJOR, AUGMENTED}

  static class Interval {

    private final int ordinalName;
    private final IntervalType type;

    Interval(int ordinalName, IntervalType type) {
      BaseInterval baseInterval = BaseInterval.forOrdinalName(ordinalName);
      if (baseInterval.isPerfect) {
        switch (type) {
          case MINOR:
          case MAJOR:
            throw new IllegalArgumentException();
        }
      } else {
        switch (type) {
          case PERFECT:
            throw new IllegalArgumentException();
        }
      }
      this.ordinalName = ordinalName;
      this.type = type;
    }

    Note resolveInKey(Note key) {
      int n = IntMath.mod(key.baseNote.ordinal() + ordinalName - 1, 7);
      BaseNote baseNote = BaseNote.values()[n];
      throw new UnsupportedOperationException();
    }
  }

  private static final ImmutableList<Interval> COMMON_INTERVALS = ImmutableList.of(
    new Interval(2, MINOR),
    new Interval(2, MAJOR),
    new Interval(3, MINOR),
    new Interval(3, MAJOR),
    new Interval(4, PERFECT),
    new Interval(5, DIMINISHED),
    new Interval(5, PERFECT),
    new Interval(6, MINOR),
    new Interval(6, MAJOR),
    new Interval(7, MINOR),
    new Interval(7, MAJOR));

  private static final ChordOrScale MAJOR_TRIAD = new ChordOrScale(
    new Interval(3, IntervalType.MAJOR),
    new Interval(5, IntervalType.PERFECT));

  private static final ChordOrScale MINOR_TRIAD = new ChordOrScale(
    new Interval(3, IntervalType.MINOR),
    new Interval(5, IntervalType.PERFECT));

  private static final ChordOrScale AUGMENTED_TRIAD = new ChordOrScale(
    new Interval(3, IntervalType.MAJOR),
    new Interval(5, IntervalType.AUGMENTED));

  private static final ChordOrScale DIMINISHED_TRIAD = new ChordOrScale(
    new Interval(3, IntervalType.MINOR),
    new Interval(5, IntervalType.DIMINISHED));

  private static final ChordOrScale SUSPENDED2_TRIAD = new ChordOrScale(
    new Interval(2, IntervalType.MAJOR),
    new Interval(5, IntervalType.PERFECT));

  private static final ChordOrScale SUSPENDED4_TRIAD = new ChordOrScale(
    new Interval(4, IntervalType.PERFECT),
    new Interval(5, IntervalType.PERFECT));

  private static final ChordOrScale DOMINANT_SEVENTH =
    new ChordOrScale(MAJOR_TRIAD, new Interval(7, MINOR));
  private static final ChordOrScale MAJOR_SEVENTH =
    new ChordOrScale(MAJOR_TRIAD, new Interval(7, MAJOR));
  private static final ChordOrScale MINOR_SEVENTH =
    new ChordOrScale(MINOR_TRIAD, new Interval(7, MINOR));
  private static final ChordOrScale MINOR_MAJOR_SEVENTH =
    new ChordOrScale(MINOR_TRIAD, new Interval(7, MAJOR));
  private static final ChordOrScale NINTH =
    new ChordOrScale(DOMINANT_SEVENTH, new Interval(9, MAJOR));
  private static final ChordOrScale MINOR_NINTH =
    new ChordOrScale(MINOR_SEVENTH, new Interval(9, MAJOR));

  private static final ChordOrScale LYDIAN;
  private static final ChordOrScale IONIAN;
  private static final ChordOrScale MIXOLYDIAN;
  private static final ChordOrScale DORIAN;
  private static final ChordOrScale AEOLIAN;
  private static final ChordOrScale PHRYGIAN;
  private static final ChordOrScale LOCRIAN;

  static {
    // Build the LIMDAPL modes progressively
    List<IntervalType> modifiers = Arrays.asList(
      null,
      PERFECT,
      MAJOR,
      MAJOR,
      AUGMENTED,
      PERFECT,
      MAJOR,
      MAJOR);
    LYDIAN = create7NoteScale(modifiers);

    modifiers.set(4, PERFECT);
    IONIAN = create7NoteScale(modifiers);

    modifiers.set(7, MINOR);
    MIXOLYDIAN = create7NoteScale(modifiers);

    modifiers.set(3, MINOR);
    DORIAN = create7NoteScale(modifiers);

    modifiers.set(6, MINOR);
    AEOLIAN = create7NoteScale(modifiers);

    modifiers.set(2, MINOR);
    PHRYGIAN = create7NoteScale(modifiers);

    modifiers.set(5, DIMINISHED);
    LOCRIAN = create7NoteScale(modifiers);
  }

  private static final ChordOrScale MAJOR_SCALE = IONIAN;
  private static final ChordOrScale MINOR_SCALE = AEOLIAN;

  private static ChordOrScale create7NoteScale(List<IntervalType> modifiers) {
    List<Interval> intervals = new ArrayList<>();
    for (int ordinalName = 2; ordinalName < 8; ordinalName++) {
      intervals.add(new Interval(ordinalName, modifiers.get(ordinalName)));
    }
    return new ChordOrScale(intervals);
  }

  static class ChordOrScale {

    private final ImmutableList<Interval> intervals;

    ChordOrScale(Interval... intervals) {
      this(Arrays.asList(intervals));
    }

    ChordOrScale(ChordOrScale base, Interval... intervals) {
      this(Iterables.concat(base.intervals, Arrays.asList(intervals)));
    }

    ChordOrScale(Iterable<Interval> intervals) {
      this.intervals = ImmutableList.copyOf(intervals);
    }
  }

  // @AutoValue
  static class Note {

    private final BaseNote baseNote;
    private final Accidental accidental;

    Note(BaseNote baseNote, Accidental accidental) {
      this.baseNote = baseNote;
      this.accidental = accidental;
    }

    Note(BaseNote baseNote) {
      this(baseNote, Accidental.NATURAL);
    }

    static Note fromString(String string) {
      String baseNote = string.substring(0, 1);
      String accidental = string.substring(1);
      return new Note(BaseNote.fromString(baseNote), Accidental.fromString(accidental));
    }

    int semitonesAboveC() {
      return IntMath.mod(baseNote.semitonesAboveC + accidental.semitoneDelta, 12);
    }

    boolean isEnharmonic(Note that) {
      return this.semitonesAboveC() == that.semitonesAboveC();
    }

    Note canonical(boolean preferSharp) {
      for (BaseNote baseNote : BaseNote.values()) {
        Note note = new Note(baseNote, Accidental.NATURAL);
        if (isEnharmonic(note)) {
          return note;
        }
      }
      for (BaseNote baseNote : BaseNote.values()) {
        Accidental preferred = preferSharp ? Accidental.SHARP : Accidental.FLAT;
        Note note = new Note(baseNote, preferred);
        if (isEnharmonic(note)) {
          return note;
        }
      }
      throw new AssertionError();
    }
  }

  static ImmutableList<Note> COMMON_KEYS = ImmutableList.of(
    new Note(BaseNote.C),
    new Note(BaseNote.G),
    new Note(BaseNote.D),
    new Note(BaseNote.A),
    new Note(BaseNote.E),
    new Note(BaseNote.B),
    new Note(BaseNote.F),
    new Note(BaseNote.B, Accidental.FLAT),
    new Note(BaseNote.E, Accidental.FLAT),
    new Note(BaseNote.A, Accidental.FLAT),
    new Note(BaseNote.D, Accidental.FLAT),
    new Note(BaseNote.G, Accidental.FLAT));

  public static void main(String[] args) {
    Console c = System.console();
    Random r = new Random();
    while (true) {
      System.out.println("Game one: name the interval");
      BaseNote baseNote = BaseNote.values()[r.nextInt(7)];
      Note key = COMMON_KEYS.get(r.nextInt(COMMON_KEYS.size()));
      System.out.println("Key: " + key);

      Interval interval = COMMON_INTERVALS.get(r.nextInt(COMMON_INTERVALS.size()));
      System.out.println("Interval: " + interval);

      Note resolved = interval.resolveInKey(key);
    }
  }
}
