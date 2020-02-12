package silentorb.mythic.breeze

typealias AnimationName = String

data class AnimationInfo(
    val duration: Float,
    val markers: List<TimelineMarker>
)

typealias AnimationInfoMap = Map<AnimationName, AnimationInfo>
