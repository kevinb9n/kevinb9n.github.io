package site.kevinb9n.javafx

import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import site.kevinb9n.math.Complex

fun main() = Application.launch(ComplexViz::class.java)

val STROKE_WIDTH = 3.0

class ComplexViz : Application() {
  override fun start(stage: Stage) {

    val aProp = SimpleObjectProperty<Complex>()
    val bProp = SimpleObjectProperty<Complex>()

    val root = Pane(
      Line(100.0, 400.0, 700.0, 400.0).also {
        it.stroke = Color.DARKGRAY
        it.strokeWidth = STROKE_WIDTH
      },
      Line(400.0, 100.0, 400.0, 700.0).also {
        it.stroke = Color.DARKGRAY
        it.strokeWidth = STROKE_WIDTH
      },
      Group(
        Circle(0.0, 0.0, 9.0).also {
          it.fill = Color.DARKBLUE
        },
        Text("+").also {
          it.stroke = Color.WHITE
          it.fill = Color.WHITE
          it.textAlignment = TextAlignment.CENTER
          it.translateX = -4.0
          it.translateY = 3.0
        }
      ).also {
        it.translateXProperty().bindDouble(aProp, bProp) {
          complexToCoord(aProp.value + bProp.value).first
        }
        it.translateYProperty().bindDouble(aProp, bProp) {
          complexToCoord(aProp.value + bProp.value).second
        }
      },
      Group(
        Circle(0.0, 0.0, 9.0).also {
          it.fill = Color.DARKORANGE
        },
        Text("X").also {
          it.stroke = Color.WHITE
          it.fill = Color.WHITE
          it.textAlignment = TextAlignment.CENTER
          it.translateX = -4.0
          it.translateY = 3.0
        }
      ).also {
        it.translateXProperty().bindDouble(aProp, bProp) {
          complexToCoord(aProp.value * bProp.value).first
        }
        it.translateYProperty().bindDouble(aProp, bProp) {
          complexToCoord(aProp.value * bProp.value).second
        }
      },
      Group(
        Circle(0.0, 0.0, 9.0).also {
          it.fill = Color.PURPLE
        },
        Text("/").also {
          it.stroke = Color.WHITE
          it.fill = Color.WHITE
          it.textAlignment = TextAlignment.CENTER
          it.translateX = -4.0
          it.translateY = 3.0
        }
      ).also {
        it.translateXProperty().bindDouble(aProp, bProp) {
          val b = if (bProp.value?.abs() ?: 0.0 < 1e-7) 1e-7 else bProp.value.abs()
          complexToCoord(aProp.value / b).first
        }
        it.translateYProperty().bindDouble(aProp, bProp) {
          val b = if (bProp.value?.abs() ?: 0.0 < 1e-7) 1e-7 else bProp.value.abs()
          complexToCoord(aProp.value / b).second
        }
      },
      DragToTranslate(
        Group(
          Circle(0.0, 0.0, 9.0).also {
            it.fill = Color.DARKRED
          },
          Text("A").also {
            it.stroke = Color.WHITE
            it.fill = Color.WHITE
            it.textAlignment = TextAlignment.CENTER
            it.translateX = -4.0
            it.translateY = 3.0
          }
        ).also {
          it.translateX = 500.0
          it.translateY = 300.0
          aProp.bindObject(it.translateXProperty(), it.translateYProperty()) {
            coordToComplex(it.translateX, it.translateY)
          }
        }
      ),
      DragToTranslate(
        Group(
          Circle(0.0, 0.0, 9.0).also {
            it.fill = Color.DARKGREEN
          },
          Text("B").also {
            it.stroke = Color.WHITE
            it.fill = Color.WHITE
            it.textAlignment = TextAlignment.CENTER
            it.translateX = -4.0
            it.translateY = 3.0
          }
        ).also {
          it.translateX = 550.0
          it.translateY = 350.0
          bProp.bindObject(it.translateXProperty(), it.translateYProperty()) {
            coordToComplex(it.translateX, it.translateY)
          }
        }
      ),
    )

    with(stage) {
      scene = Scene(root, 800.0, 800.0)
      title = "Complex visualizer"
      show()
    }
  }

  fun coordToComplex(x: Double, y: Double): Complex {
    val re = (x - 400.0) / 30.0
    val im = (400.0 - y) / 30.0
    val c = Complex(re, im)
//    println("x $x y $y --> c $c")
    return c
  }

  fun complexToCoord(c: Complex): Pair<Double, Double> {
    val x = 400.0 + c.re * 30.0
    val y = 400.0 - c.im * 30.0
//    println("c $c --> x $x y $y")
    return x to y
  }
}
