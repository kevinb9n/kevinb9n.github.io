package site.kevinb9n.tmsoloplacement

enum class PublishedMap(val map: MarsMap) {
  THARSIS("""

                            /       LSS   WSS    L    WC     W

                         /        L    VS     L     L     L    WCC

                      /       VC     L     L     L     L     L     L

                   /       VPT   LP    LP    LP    LPP   LP    LP    WPP

                /       VPP   LPP   NPP   WPP   WPP   WPP   LPP   LPP   LPP

             /             LP    LPP   LP    LP    LP    WP    WP    WP

          /                    L     L     L     L     L    LP     L

       /                         LSS    L    LC    LC     L    LT

    /                               LSS   LSS    L     L    WTT

  """),

  HELLAS("""

                            /       WPP   LPP   LPP   LPS   LP

                         /       WPP   LPP   LP    LPS   LP    LP

                      /       WP    LP    LS    LS     L    LPP   LPC

                   /       WP    LP    LS    LSS   LS    WP    WP    LP

                /       LC     L     L    LSS    L    WC    W3H    W    LP

             /             LT     L    LS     L     L     W    WS     L

          /                   WTT    L     L    LC     L     L    LT

       /                         LS    LC    LHH   LHH   LT    LT

    /                                L    LHH   LO6   LHH    L

  """),

  ELYSIUM("""

                            /        W    WT    WC    WS    LC

                         /       VT     L     L     W     W    LSS

                      /       VTT    L    LC     L    WP     W    V3C

                   /       LP    LP    LP    WPP   LP    WP    WP    LSP

                /       LPP   LPP   LPP   WPP   LPP   L3P   LPP   LPP   VPT

             /             LS    LP    LP    LP    LP    LP    LP     L

          /                   LT    LS     L     L    LS     L     L

       /                         LSS    L     L     L    LSS    L

    /                               LS     L    LC    LC    LSS

  """);

  constructor(text: String) : this(MarsMap(text))
}
