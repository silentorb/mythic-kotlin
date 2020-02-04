package silentorb.metaview.front

import silentorb.metaview.common.CommonState
import silentorb.metaview.common.GuiState
import silentorb.metaview.texturing.TexturingState

enum class Domain {
  modeling,
  texturing
}

data class ConfigState(
    val domain: Domain = Domain.texturing,
    val guis: Map<Domain, GuiState> = mapOf(),
    val texturing: TexturingState = TexturingState()
)

data class AppState(
    val common: CommonState,
    val domain: Domain = Domain.texturing,
    val texturing: TexturingState = TexturingState(),
    val otherDomains: Map<Domain, GuiState> = mapOf()
)

enum class DomainEvent {
  switchDomain
}
