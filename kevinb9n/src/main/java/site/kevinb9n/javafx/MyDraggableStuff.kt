package site.kevinb9n.javafx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import site.kevinb9n.plane.Point

fun main() = Application.launch(MyDraggableStuff::class.java)

class MyDraggableStuff : Application() {
  override fun start(stage: Stage) {
    val rootPane = Pane(
      DragToTranslate(
        Rectangle(300.0, 300.0).apply {
          stroke = Color.DARKBLUE
          fill = Color.LIGHTBLUE
          strokeWidth = 10.0
        }),
      DragToTranslate(
        Circle(0.0, 0.0, 100.0).apply {
          fill = Color.MEDIUMPURPLE
          translateX = 400.0
          translateY = 225.0
        }),
      DragToTranslate(
        pointsToPolygon(Point(200, 100), Point(300, 300), Point(100, 300)).apply {
          fill = Color.PINK
        }),
    )
    for (node in rootPane.children) {
      node.addEventFilter(MouseEvent.MOUSE_CLICKED) {
        println("click")
        // TODO: stop this from happening after drag
        moveToBack(rootPane.children, node) }
    }

    with(stage) {
      scene = Scene(rootPane, 800.0, 450.0)
      title = "Widgets Shapes"
      show()

    }
  }
}
