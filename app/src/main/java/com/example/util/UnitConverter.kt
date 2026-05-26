package com.example.util

object UnitConverter {

    enum class Category {
        LENGTH, WEIGHT, TEMPERATURE, SPEED, AREA, VOLUME, CURRENCY
    }

    data class UnitDefinition(
        val name: String,
        val symbol: String,
        val factorToStandard: Double // Relative to reference unit
    )

    val unitsMap = mapOf(
        Category.LENGTH to listOf(
            UnitDefinition("Meters", "m", 1.0),
            UnitDefinition("Kilometers", "km", 1000.0),
            UnitDefinition("Centimeters", "cm", 0.01),
            UnitDefinition("Millimeters", "mm", 0.001),
            UnitDefinition("Miles", "mi", 1609.344),
            UnitDefinition("Yards", "yd", 0.9144),
            UnitDefinition("Feet", "ft", 0.3048),
            UnitDefinition("Inches", "in", 0.0254)
        ),
        Category.WEIGHT to listOf(
            UnitDefinition("Grams", "g", 1.0),
            UnitDefinition("Kilograms", "kg", 1000.0),
            UnitDefinition("Milligrams", "mg", 0.001),
            UnitDefinition("Pounds", "lb", 453.59237),
            UnitDefinition("Ounces", "oz", 28.349523)
        ),
        Category.TEMPERATURE to listOf(
            UnitDefinition("Celsius", "°C", 1.0),
            UnitDefinition("Fahrenheit", "°F", 1.0),
            UnitDefinition("Kelvin", "K", 1.0)
        ),
        Category.SPEED to listOf(
            UnitDefinition("Meters per second", "m/s", 1.0),
            UnitDefinition("Kilometers per hour", "km/h", 1.0 / 3.6),
            UnitDefinition("Miles per hour", "mph", 0.44704),
            UnitDefinition("Knots", "kt", 0.514444)
        ),
        Category.AREA to listOf(
            UnitDefinition("Square Meters", "m²", 1.0),
            UnitDefinition("Square Kilometers", "km²", 1000000.0),
            UnitDefinition("Square Miles", "mi²", 2589988.11),
            UnitDefinition("Acres", "ac", 4046.8564),
            UnitDefinition("Hectares", "ha", 10000.0)
        ),
        Category.VOLUME to listOf(
            UnitDefinition("Liters", "L", 1.0),
            UnitDefinition("Milliliters", "mL", 0.001),
            UnitDefinition("Gallons (US)", "gal", 3.78541),
            UnitDefinition("Cups (US)", "cup", 0.236588),
            UnitDefinition("Cubic Meters", "m³", 1000.0)
        ),
        Category.CURRENCY to listOf(
            UnitDefinition("US Dollar", "USD ($)", 1.0),
            UnitDefinition("Euro", "EUR (€)", 1.08),
            UnitDefinition("British Pound", "GBP (£)", 1.27),
            UnitDefinition("Indian Rupee", "INR (₹)", 0.012),
            UnitDefinition("Japanese Yen", "JPY (¥)", 0.0064),
            UnitDefinition("Canadian Dollar", "CAD ($)", 0.73),
            UnitDefinition("Australian Dollar", "AUD ($)", 0.66),
            UnitDefinition("Chinese Yuan", "CNY (¥)", 0.14),
            UnitDefinition("Saudi Riyal", "SAR (SR)", 0.27),
            UnitDefinition("UAE Dirham", "AED (DH)", 0.272),
            UnitDefinition("Swiss Franc", "CHF (CHF)", 1.10)
        )
    )

    fun convert(value: Double, fromUnit: UnitDefinition, toUnit: UnitDefinition, category: Category): Double {
        if (fromUnit.symbol == toUnit.symbol) return value

        if (category == Category.TEMPERATURE) {
            // Temperature requires custom relative offset formulas
            val celsiusValue = when (fromUnit.symbol) {
                "°C" -> value
                "°F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            return when (toUnit.symbol) {
                "°C" -> celsiusValue
                "°F" -> (celsiusValue * 9.0 / 5.0) + 32.0
                "K" -> celsiusValue + 273.15
                else -> celsiusValue
            }
        } else {
            // General multiplicative units conversion: convert to standard baseline, then to destination
            val standardValue = value * fromUnit.factorToStandard
            return standardValue / toUnit.factorToStandard
        }
    }
}
