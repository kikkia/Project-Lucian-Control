package com.kikkia.project_lucian.enums

/**
 * PresetId - References the standardized WLED playlist ID to change to
 */
enum class AnimationStates(val playlistId: Int) {
    IDLE(1),
    LASER(2),
    BIGSHOT(3),
    ULTIMATE(4)
}