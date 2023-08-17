package com.kikkia.project_lucian.enums

import com.kikkia.project_lucian.R

/**
 * PresetId - References the standardized WLED playlist ID to change to
 */
enum class  AnimationStates(val playlistId: Int, val sfxId: Int) {
    IDLE(1, 0),
    LASER(2, R.raw.lucian_q),
    BIGSHOT(3, R.raw.lucian_w),
    ULTIMATE(4, R.raw.ashes_and_dust),
    TWOSHOT(5, R.raw.lucian_p),
}