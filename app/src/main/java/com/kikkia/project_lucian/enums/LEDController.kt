package com.kikkia.project_lucian.enums

enum class LEDController(val ip: String, val numSegments: Int) {
    HELMET("192.168.8.100", 14),
    LASER("192.168.8.101", 8),
    REVOLVER("192.168.8.102", 7);

    fun getJsonApiPath() : String {
        return "http://${ip}/json/state";
    }
}