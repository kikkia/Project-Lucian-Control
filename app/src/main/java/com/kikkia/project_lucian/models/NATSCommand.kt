package com.kikkia.project_lucian.models

import com.kikkia.project_lucian.enums.LEDController

data class NATSCommand(val controller: LEDController, val command: NATSCommand, val data: String) {
}