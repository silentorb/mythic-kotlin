package silentorb.metaview.front

import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile
import silentorb.metaview.common.CommonState
import silentorb.metaview.common.loadGraphs

fun newState(): AppState {
  val config = loadYamlFile<ConfigState>("metaview.yaml")
  if (config == null)
    throw Error("Could not find required configuration file metaview.yaml")

  val guis = config.guis
  val texturing = config.texturing
  val gui = guis[config.domain]!!

  val commonState = loadGraphs(CommonState(
      gui = gui
  ))

  return AppState(
      domain = config.domain,
      otherDomains = guis.minus(config.domain),
      texturing = texturing,
      common = commonState
  )
}

fun saveConfig(state: AppState) {
  val config = ConfigState(
      domain = state.domain,
      guis = mapOf(state.domain to state.common.gui).plus(state.otherDomains),
      texturing = state.texturing
  )
  saveYamlFile("metaview.yaml", config)
}
