package com.example.util

import java.util.Locale

object ChemicalFormulaParser {

    private val ATOMIC_WEIGHTS = mapOf(
        "H" to 1.008,
        "HE" to 4.0026,
        "LI" to 6.94,
        "BE" to 9.0122,
        "B" to 10.81,
        "C" to 12.011,
        "N" to 14.007,
        "O" to 15.999,
        "F" to 18.998,
        "NE" to 20.180,
        "NA" to 22.990,
        "MG" to 24.305,
        "AL" to 26.982,
        "SI" to 28.085,
        "P" to 30.974,
        "S" to 32.06,
        "CL" to 35.45,
        "AR" to 39.948,
        "K" to 39.098,
        "CA" to 40.078,
        "FE" to 55.845,
        "NI" to 58.693,
        "CU" to 63.546,
        "ZN" to 65.38,
        "AG" to 107.87,
        "I" to 126.90,
        "AU" to 196.97,
        "PB" to 207.2
    )

    data class MassBreakdown(
        val element: String,
        val atomCount: Int,
        val unitWeight: Double,
        val totalWeight: Double
    )

    data class ParseResult(
        val totalMolarMass: Double,
        val breakdown: List<MassBreakdown>,
        val isSuccess: Boolean,
        val error: String? = null
    )

    /**
     * Parses a chemical formula (e.g. H2O, C6H12O6, NaCl)
     */
    fun parseMolarMass(formula: String): ParseResult {
        if (formula.isBlank()) {
            return ParseResult(0.0, emptyList(), false, "Formula is empty")
        }

        val cleanFormula = formula.replace(" ", "")
        val breakdown = mutableListOf<MassBreakdown>()
        var i = 0
        val len = cleanFormula.length

        try {
            while (i < len) {
                val ch = cleanFormula[i]
                if (!ch.isLetter()) {
                    return ParseResult(
                        0.0, emptyList(), false, 
                        "Unexpected character '$ch' at position ${i + 1}. Expected element symbol."
                    )
                }

                // Parse element symbol (must start with Uppercase)
                if (!ch.isUpperCase()) {
                    return ParseResult(
                        0.0, emptyList(), false, 
                        "Element symbol must start with an uppercase letter at position ${i + 1}."
                    )
                }

                var element = ch.toString()
                i++
                
                // Read lowercase letters for multi-letter symbols (e.g. Na, Cl)
                while (i < len && cleanFormula[i].isLetter() && cleanFormula[i].isLowerCase()) {
                    element += cleanFormula[i]
                    i++
                }

                // Parse atom count multiplier
                var countStr = ""
                while (i < len && cleanFormula[i].isDigit()) {
                    countStr += cleanFormula[i]
                    i++
                }
                val count = if (countStr.isEmpty()) 1 else countStr.toInt()

                // Look up weight
                val key = element.uppercase(Locale.ROOT)
                val weight = ATOMIC_WEIGHTS[key]
                    ?: return ParseResult(
                        0.0, emptyList(), false, 
                        "Unknown chemical element '$element'. Try common elements like H, C, O, Na, Cl, Fe."
                    )

                val totalWeight = weight * count
                
                // Combine if element is already present in this parse iteration
                val existingIndex = breakdown.indexOfFirst { it.element == element }
                if (existingIndex >= 0) {
                    val existing = breakdown[existingIndex]
                    breakdown[existingIndex] = MassBreakdown(
                        element = element,
                        atomCount = existing.atomCount + count,
                        unitWeight = weight,
                        totalWeight = existing.totalWeight + totalWeight
                    )
                } else {
                    breakdown.add(MassBreakdown(element, count, weight, totalWeight))
                }
            }

            val total = breakdown.sumOf { it.totalWeight }
            return ParseResult(total, breakdown, true)

        } catch (e: Exception) {
            return ParseResult(0.0, emptyList(), false, "Parsing error: ${e.message}")
        }
    }
}
