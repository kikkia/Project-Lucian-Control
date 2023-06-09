package com.kikkia.project_lucian.models

import com.kikkia.project_lucian.enums.LEDController
import org.json.JSONObject

data class LEDState(val controller: LEDController,
                    val presetId: Int,
                    val on: Boolean,
                    val brightness: Int,
                    val presetCycle: Boolean) {

    companion object {
        fun fromJSON(controller: LEDController, json: JSONObject): LEDState {
            return LEDState(controller,
                             json.getInt("ps"),
                             json.getBoolean("on"),
                             json.getInt("bri"),
                             json.getInt("pl") == 0
            )
        }

        fun blank() : LEDState {
            return LEDState(LEDController.REVOLVER, 0, false, 0, false)
        }
    }
}
