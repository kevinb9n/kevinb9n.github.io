package site.kevinb9n.javafx

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun main() = Application.launch(Draggable::class.java)

class Draggable : Application() {
  val dragOn = SimpleBooleanProperty(this, "dragModeActive", true)

  override fun start(stage: Stage) {
    val loginPanel = Draggable(createLoginPanel(), dragOn)
    val confirmationPanel = Draggable(createConfirmationPanel(), dragOn)
    val progressPanel = Draggable(createProgressPanel(), dragOn)
    loginPanel.relocate(0.0, 0.0)
    confirmationPanel.relocate(0.0, 67.0)
    progressPanel.relocate(0.0, 106.0)
    val panelsPane = Pane()
    panelsPane.children += listOf(loginPanel, confirmationPanel, progressPanel)
    val sceneRoot = BorderPane()
    BorderPane.setAlignment(panelsPane, Pos.TOP_LEFT)
    sceneRoot.center = panelsPane
    val dragModeCheckbox = CheckBox("TranslatingDrag mode")
    BorderPane.setMargin(dragModeCheckbox, Insets(6.0))
    sceneRoot.bottom = dragModeCheckbox
    dragOn.bind(dragModeCheckbox.selectedProperty())
    val scene = Scene(sceneRoot, 400.0, 300.0)
    stage.scene = scene
    stage.title = "Draggable Panels Example"
    stage.show()
  }

  class Draggable(val node: Node, val dragOn: SimpleBooleanProperty) : Group(node) {
    var drag = Drag(0.0, 0.0)
    init {
      addEventFilter(MouseEvent.ANY) {
        if (dragOn.get()) it.consume()
      }
      addEventFilter(MouseEvent.MOUSE_PRESSED) {
        if (dragOn.get()) drag = Drag(it, node)
      }
      addEventFilter(MouseEvent.MOUSE_DRAGGED) {
        if (dragOn.get()) {
          val point = drag.adjust(it)
          node.translateX = point.x
          node.translateY = point.y
        }
      }
    }
  }

  data class Drag(val offsetX: Double, val offsetY: Double) {
    constructor(event: MouseEvent, node: Node)
      : this(node.translateX - event.x, node.translateY - event.y)
    fun adjust(event: MouseEvent): Point {
      return Point(event.x + offsetX, event.y + offsetY)
    }
  }

  fun createLoginPanel(): Node {
    val toggleGroup = ToggleGroup()
    val textField = TextField()
    textField.prefColumnCount = 10
    textField.promptText = "Your name"
    val passwordField = PasswordField()
    passwordField.prefColumnCount = 10
    passwordField.promptText = "Your password"

    val choiceBox = ChoiceBox(
      observableArrayList("English", "\u0420\u0443\u0441\u0441\u043a\u0438\u0439", "Fran\u00E7ais"))
    choiceBox.tooltip = Tooltip("Your language")
    choiceBox.selectionModel.select(0)

    val panel = createHBox(6.0,
      createVBox(2.0,
        createRadioButton("High", toggleGroup, true),
        createRadioButton("Medium", toggleGroup, false),
        createRadioButton("Low", toggleGroup, false)),
      createVBox(2.0,
        textField,
        passwordField),
      choiceBox)
    panel.alignment = Pos.BOTTOM_LEFT
    configureBorder(panel)
    return panel
  }

  private fun createConfirmationPanel(): Node {
    val acceptanceLabel = Label("Not Available")
    val acceptButton = Button("Accept")
    acceptButton.onAction = EventHandler { acceptanceLabel.text = "Accepted" }
    val declineButton = Button("Decline")
    declineButton.onAction = EventHandler { acceptanceLabel.text = "Declined" }
    val panel = createHBox(6.0, acceptButton, declineButton, acceptanceLabel)
    panel.alignment = Pos.CENTER_LEFT
    configureBorder(panel)
    return panel
  }

  private fun createProgressPanel(): Node {
    val slider = Slider()
    val progressIndicator = ProgressIndicator(0.0)
    progressIndicator.progressProperty().bind(
      Bindings.divide(slider.valueProperty(), slider.maxProperty()))
    val panel = createHBox(6.0, Label("Progress:"), slider, progressIndicator)
    configureBorder(panel)
    return panel
  }

  private fun configureBorder(region: Region) {
    region.style = ("-fx-background-color: white;"
      + "-fx-border-color: black;"
      + "-fx-border-width: 1;"
      + "-fx-border-radius: 6;"
      + "-fx-padding: 6;")
  }

  private fun createRadioButton(text: String, toggleGroup: ToggleGroup, selected: Boolean) =
    RadioButton(text).also {
      it.toggleGroup = toggleGroup
      it.isSelected = selected
    }

  private fun createHBox(spacing: Double, vararg children: Node) =
    HBox(spacing).also { it.children += children }

  private fun createVBox(spacing: Double, vararg children: Node) =
    VBox(spacing).also { it.children += children }
}
