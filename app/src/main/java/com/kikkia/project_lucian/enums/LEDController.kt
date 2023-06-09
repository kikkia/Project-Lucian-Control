package com.kikkia.project_lucian.enums

enum class LEDController(val ip: String) {
    HELMET("192.168.8.100"),
    LASER("192.168.8.101"),
    //REVOLVER("192.168.8.102");
    REVOLVER("4.3.2.1");

    fun getJsonApiPath() : String {
        return "http://${ip}/json/state";
    }
}