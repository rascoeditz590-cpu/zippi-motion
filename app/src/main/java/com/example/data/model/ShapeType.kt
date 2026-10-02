package com.example.data.model

enum class ShapeType(val displayName: String, val category: String) {
    RECTANGLE("Rectangle", "Geometric"),
    ROUNDED_RECT("Rounded Rectangle", "Geometric"),
    CIRCLE("Circle", "Geometric"),
    ELLIPSE("Ellipse", "Geometric"),
    TRIANGLE("Triangle", "Geometric"),
    RIGHT_TRIANGLE("Right Triangle", "Geometric"),
    DIAMOND("Diamond", "Geometric"),
    PENTAGON("Pentagon", "Polygons"),
    HEXAGON("Hexagon", "Polygons"),
    OCTAGON("Octagon", "Polygons"),
    STAR_4("4-Point Star", "Stars"),
    STAR_5("5-Point Star", "Stars"),
    STAR_6("6-Point Star", "Stars"),
    STAR_8("8-Point Star", "Stars"),
    BURST_12("12-Point Burst", "Stars"),
    HEART("Heart", "Symbols"),
    CROSS("Cross / Plus", "Symbols"),
    MOON("Crescent Moon", "Symbols"),
    SUN("Sun", "Symbols"),
    LIGHTNING("Lightning Bolt", "Symbols"),
    CLOUD("Cloud", "Symbols"),
    FLOWER("Flower", "Symbols"),
    ARROW_RIGHT("Arrow Right", "Arrows"),
    ARROW_LEFT("Arrow Left", "Arrows"),
    ARROW_UP("Arrow Up", "Arrows"),
    ARROW_DOWN("Arrow Down", "Arrows"),
    ARROW_DOUBLE("Double Arrow", "Arrows"),
    CHEVRON_RIGHT("Chevron Right", "Arrows"),
    SPEECH_BUBBLE("Speech Bubble", "Callouts"),
    THOUGHT_BUBBLE("Thought Bubble", "Callouts"),
    CALLOUT_RECT("Rectangular Callout", "Callouts"),
    BANNER_RIBBON("Banner Ribbon", "Badges"),
    SHIELD("Shield", "Badges"),
    HEX_BADGE("Hexagonal Badge", "Badges"),
    CAPSULE("Pill / Capsule", "Geometric"),
    RING("Donut / Ring", "Geometric"),
    GEAR("Gear / Cog", "Symbols"),
    TAG("Price Tag", "Badges"),
    CHECKMARK("Checkmark", "Symbols"),
    PARALLELOGRAM("Parallelogram", "Geometric"),
    TRAPEZOID("Trapezoid", "Geometric");

    companion object {
        fun fromString(value: String): ShapeType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RECTANGLE
        }
    }
}
