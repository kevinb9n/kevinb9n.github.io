package site.kevinb9n.javafx

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Pane

/**
 * Has a "zoom" property, which works like "scaling" except:
 *
 * * there is just one zoom for both x and y
 * * strokeWidths of all shapes do *not* scale
 * * each child added can choose whether it scales with the zoom or not. If not, the point the child
 *   considers to be (0,0) is the point that will stay in place.
 * *
 * *
 *
 */
class ZoomPane : Pane() {
  val zoomProperty = SimpleDoubleProperty()
  init {
    scaleXProperty().bind(zoomProperty)
    scaleYProperty().bind(zoomProperty)

  }
}
