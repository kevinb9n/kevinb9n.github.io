package site.kevinb9n.javafx

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.stage.Stage

fun main() = Application.launch(MyDraggableStuff::class.java)

class MyDraggableStuff : Application() {
  override fun start(stage: Stage) {
    val panelsPane = Pane()
    panelsPane.children += listOf()
    val sceneRoot = BorderPane()
      BorderPane.setAlignment(panelsPane, Pos.TOP_LEFT)
    sceneRoot.center = panelsPane
    val dragModeCheckbox = CheckBox("TranslatingDrag mode")
      BorderPane.setMargin(dragModeCheckbox, Insets(6.0))
    sceneRoot.bottom = dragModeCheckbox
    val scene = Scene(sceneRoot, 400.0, 300.0)
    stage.scene = scene
    stage.title = "Draggable Panels Example"
    stage.show()
  }

  class DragToTranslate(val node: Node) : Group(node) {
    var drag = TranslatingDrag(0.0, 0.0)
    init {
      addEventFilter(MouseEvent.ANY) {
        it.consume()
      }
      addEventFilter(MouseEvent.MOUSE_PRESSED) {
        drag = TranslatingDrag(it, node)
      }
      addEventFilter(MouseEvent.MOUSE_DRAGGED) {
        val point = drag.adjust(it)
        node.translateX = point.x
        node.translateY = point.y
      }
    }
  }

  data class TranslatingDrag(val offsetX: Double, val offsetY: Double) {
    constructor(event: MouseEvent, node: Node)
      : this(node.translateX - event.x, node.translateY - event.y)
    fun adjust(event: MouseEvent): Point {
      return Point(event.x + offsetX, event.y + offsetY)
    }
  }
}
