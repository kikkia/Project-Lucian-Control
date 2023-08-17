package com.kikkia.project_lucian.enums

import com.kikkia.project_lucian.models.RGB

enum class ColorProfile(val pri: RGB, val sec: RGB) {
    DEFAULT(RGB(237, 130, 14), RGB(255, 0, 0)),
    C9(RGB(0, 174, 239), RGB(0, 174, 239)),
    NRG(RGB(255, 255, 255), RGB(255, 255, 255)),
    TL(RGB(12, 34, 63), RGB(12, 34, 63)),
}