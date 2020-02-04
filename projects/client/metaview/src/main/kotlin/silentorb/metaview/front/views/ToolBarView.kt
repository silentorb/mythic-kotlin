package silentorb.metaview.front.views

import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.ToolBar
import silentorb.metaview.common.Emitter
import silentorb.metaview.common.Event
import silentorb.metaview.front.AppState
import silentorb.metaview.front.Domain
import silentorb.metaview.front.DomainEvent


fun toolBarView(state: AppState, emit: Emitter): Node {
  val domainSelect = ComboBox<Domain>()
  domainSelect.items.addAll(Domain.modeling, Domain.texturing)
  domainSelect.value = state.domain
  domainSelect.valueProperty().addListener { event ->
    emit(Event(DomainEvent.switchDomain, domainSelect.value))
  }
  val toolbar = ToolBar(
      domainSelect
  )
  return toolbar
}