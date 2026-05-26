package com.example.util

import kotlin.math.PI

enum class FormulaFieldType {
    NUMERIC, TEXT
}

data class FormulaField(
    val key: String,
    val label: String,
    val unit: String,
    val type: FormulaFieldType = FormulaFieldType.NUMERIC
)

data class FormulaDefinition(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // "Physics", "Maths: Area", "Maths: Volume", "Maths: Perimeter", "Chemistry"
    val formulaDisplay: String,
    val fields: List<FormulaField>,
    val calculate: (Map<String, String>) -> String
)

object FormulaProvider {

    val formulas = listOf(
        // PHYSICS FORMULAS
        FormulaDefinition(
            id = "phys_speed",
            name = "Speed (Velocity)",
            description = "Calculates the speed of an object moving a distance over a period of time.",
            category = "Physics",
            formulaDisplay = "v = d / t",
            fields = listOf(
                FormulaField("d", "Distance", "m"),
                FormulaField("t", "Time", "s")
            ),
            calculate = { inputs ->
                val d = inputs["d"]?.toDoubleOrNull() ?: 0.0
                val t = inputs["t"]?.toDoubleOrNull() ?: 1.0
                if (t == 0.0) "Time cannot be zero"
                else "v = %.4f m/s".format(d / t)
            }
        ),
        FormulaDefinition(
            id = "phys_force",
            name = "Newton's Second Law (Force)",
            description = "Calculates the force acting on an object with mass and acceleration.",
            category = "Physics",
            formulaDisplay = "F = m · a",
            fields = listOf(
                FormulaField("m", "Mass", "kg"),
                FormulaField("a", "Acceleration", "m/s²")
            ),
            calculate = { inputs ->
                val m = inputs["m"]?.toDoubleOrNull() ?: 0.0
                val a = inputs["a"]?.toDoubleOrNull() ?: 0.0
                "F = %.4f N".format(m * a)
            }
        ),
        FormulaDefinition(
            id = "phys_kinetic_energy",
            name = "Kinetic Energy",
            description = "Calculates the energy of an object in motion.",
            category = "Physics",
            formulaDisplay = "E_k = ½ · m · v²",
            fields = listOf(
                FormulaField("m", "Mass", "kg"),
                FormulaField("v", "Velocity", "m/s")
            ),
            calculate = { inputs ->
                val m = inputs["m"]?.toDoubleOrNull() ?: 0.0
                val v = inputs["v"]?.toDoubleOrNull() ?: 0.0
                val energy = 0.5 * m * v * v
                "E_k = %.4f J".format(energy)
            }
        ),
        FormulaDefinition(
            id = "phys_power",
            name = "Power",
            description = "Calculates the rate of performing work (or transferring energy) over time.",
            category = "Physics",
            formulaDisplay = "P = W / t",
            fields = listOf(
                FormulaField("W", "Work Done / Energy", "J"),
                FormulaField("t", "Elapsed Time", "s")
            ),
            calculate = { inputs ->
                val w = inputs["W"]?.toDoubleOrNull() ?: 0.0
                val t = inputs["t"]?.toDoubleOrNull() ?: 1.0
                if (t == 0.0) "Time cannot be zero"
                else "P = %.4f W".format(w / t)
            }
        ),
        FormulaDefinition(
            id = "phys_ohms_law",
            name = "Ohm's Law (Voltage)",
            description = "Calculates Voltage using electrical current and resistance.",
            category = "Physics",
            formulaDisplay = "V = I · R",
            fields = listOf(
                FormulaField("I", "Current", "A"),
                FormulaField("R", "Resistance", "Ω")
            ),
            calculate = { inputs ->
                val i = inputs["I"]?.toDoubleOrNull() ?: 0.0
                val r = inputs["R"]?.toDoubleOrNull() ?: 0.0
                "V = %.4f Volts".format(i * r)
            }
        ),

        // MATHS: AREA
        FormulaDefinition(
            id = "math_area_circle",
            name = "Circle Area",
            description = "Calculates the total two-dimensional space enclosed in a circle.",
            category = "Maths: Area",
            formulaDisplay = "A = π · r²",
            fields = listOf(
                FormulaField("r", "Radius", "m")
            ),
            calculate = { inputs ->
                val r = inputs["r"]?.toDoubleOrNull() ?: 0.0
                "A = %.4f m²".format(PI * r * r)
            }
        ),
        FormulaDefinition(
            id = "math_area_rect",
            name = "Rectangle Area",
            description = "Calculates the area of a rectangle with length/width or base/height.",
            category = "Maths: Area",
            formulaDisplay = "A = w · h",
            fields = listOf(
                FormulaField("w", "Width", "m"),
                FormulaField("h", "Height", "m")
            ),
            calculate = { inputs ->
                val w = inputs["w"]?.toDoubleOrNull() ?: 0.0
                val h = inputs["h"]?.toDoubleOrNull() ?: 0.0
                "A = %.4f m²".format(w * h)
            }
        ),
        FormulaDefinition(
            id = "math_area_triangle",
            name = "Triangle Area",
            description = "Calculates the area of a triangle given its base and vertical height.",
            category = "Maths: Area",
            formulaDisplay = "A = ½ · b · h",
            fields = listOf(
                FormulaField("b", "Base length", "m"),
                FormulaField("h", "Height", "m")
            ),
            calculate = { inputs ->
                val b = inputs["b"]?.toDoubleOrNull() ?: 0.0
                val h = inputs["h"]?.toDoubleOrNull() ?: 0.0
                "A = %.4f m²".format(0.5 * b * h)
            }
        ),
        FormulaDefinition(
            id = "math_area_sphere",
            name = "Sphere Surface Area",
            description = "Calculates the outer three-dimensional surface area of a perfect sphere.",
            category = "Maths: Area",
            formulaDisplay = "A = 4 · π · r²",
            fields = listOf(
                FormulaField("r", "Radius", "m")
            ),
            calculate = { inputs ->
                val r = inputs["r"]?.toDoubleOrNull() ?: 0.0
                "A = %.4f m²".format(4.0 * PI * r * r)
            }
        ),

        // MATHS: VOLUME
        FormulaDefinition(
            id = "math_vol_sphere",
            name = "Sphere Volume",
            description = "Calculates the carrying space occupied inside a solid sphere.",
            category = "Maths: Volume",
            formulaDisplay = "V = ⁴/₃ · π · r³",
            fields = listOf(
                FormulaField("r", "Radius", "m")
            ),
            calculate = { inputs ->
                val r = inputs["r"]?.toDoubleOrNull() ?: 0.0
                "V = %.4f m³".format((4.0 / 3.0) * PI * r * r * r)
            }
        ),
        FormulaDefinition(
            id = "math_vol_cylinder",
            name = "Cylinder Volume",
            description = "Calculates the volume of a circular cylinder given its cross-sectional radius and length.",
            category = "Maths: Volume",
            formulaDisplay = "V = π · r² · h",
            fields = listOf(
                FormulaField("r", "Radius", "m"),
                FormulaField("h", "Height", "m")
            ),
            calculate = { inputs ->
                val r = inputs["r"]?.toDoubleOrNull() ?: 0.0
                val h = inputs["h"]?.toDoubleOrNull() ?: 0.0
                "V = %.4f m³".format(PI * r * r * h)
            }
        ),
        FormulaDefinition(
            id = "math_vol_cube",
            name = "Cube Volume",
            description = "Calculates the internal volume of a regular cube from its side length.",
            category = "Maths: Volume",
            formulaDisplay = "V = s³",
            fields = listOf(
                FormulaField("s", "Side Side length (s)", "m")
            ),
            calculate = { inputs ->
                val s = inputs["s"]?.toDoubleOrNull() ?: 0.0
                "V = %.4f m³".format(s * s * s)
            }
        ),

        // MATHS: PERIMETER
        FormulaDefinition(
            id = "math_perim_circle",
            name = "Circle Circumference",
            description = "Calculates the enclosing boundary distance around a circle.",
            category = "Maths: Perimeter",
            formulaDisplay = "C = 2 · π · r",
            fields = listOf(
                FormulaField("r", "Radius", "m")
            ),
            calculate = { inputs ->
                val r = inputs["r"]?.toDoubleOrNull() ?: 0.0
                "C = %.4f m".format(2.0 * PI * r)
            }
        ),
        FormulaDefinition(
            id = "math_perim_rect",
            name = "Rectangle Perimeter",
            description = "Calculates the total boundary distance around a rectangle.",
            category = "Maths: Perimeter",
            formulaDisplay = "P = 2 · (w + h)",
            fields = listOf(
                FormulaField("w", "Width", "m"),
                FormulaField("h", "Height", "m")
            ),
            calculate = { inputs ->
                val w = inputs["w"]?.toDoubleOrNull() ?: 0.0
                val h = inputs["h"]?.toDoubleOrNull() ?: 0.0
                "P = %.4f m".format(2.0 * (w + h))
            }
        ),
        FormulaDefinition(
            id = "math_perim_triangle",
            name = "Triangle Perimeter",
            description = "Calculates the sum of all three outer boundary side segments.",
            category = "Maths: Perimeter",
            formulaDisplay = "P = a + b + c",
            fields = listOf(
                FormulaField("a", "Side a", "m"),
                FormulaField("b", "Side b", "m"),
                FormulaField("c", "Side c", "m")
            ),
            calculate = { inputs ->
                val a = inputs["a"]?.toDoubleOrNull() ?: 0.0
                val b = inputs["b"]?.toDoubleOrNull() ?: 0.0
                val c = inputs["c"]?.toDoubleOrNull() ?: 0.0
                "P = %.4f m".format(a + b + c)
            }
        ),

        // CHEMISTRY FORMULAS
        FormulaDefinition(
            id = "chem_molar_mass",
            name = "Chemical Molar Mass Solver",
            description = "Resolves chemical compound text (e.g., H2O, CO2, NaCl, C6H12O6) with standard structural atomic weights.",
            category = "Chemistry",
            formulaDisplay = "Molar Mass Calculator",
            fields = listOf(
                FormulaField("formula", "Chemical Formula", "e.g. H2O", FormulaFieldType.TEXT)
            ),
            calculate = { inputs ->
                val formula = inputs["formula"] ?: ""
                val res = ChemicalFormulaParser.parseMolarMass(formula)
                if (res.isSuccess) {
                    val sb = StringBuilder()
                    sb.append("M = %.4f g/mol\n\nBreakdown:\n".format(res.totalMolarMass))
                    res.breakdown.forEach {
                        sb.append("• %s: %d atoms × %.3f g/mol = %.3f g/mol\n".format(
                            it.element, it.atomCount, it.unitWeight, it.totalWeight
                        ))
                    }
                    sb.toString()
                } else {
                    res.error ?: "Failed to parse formula"
                }
            }
        ),
        FormulaDefinition(
            id = "chem_concentration",
            name = "Solution Molar Concentration",
            description = "Calculates the concentration of solutes dissolved in a physical solution volume.",
            category = "Chemistry",
            formulaDisplay = "C = n / V",
            fields = listOf(
                FormulaField("n", "Solute Moles (n)", "mol"),
                FormulaField("V", "Solution Volume", "L")
            ),
            calculate = { inputs ->
                val n = inputs["n"]?.toDoubleOrNull() ?: 0.0
                val v = inputs["V"]?.toDoubleOrNull() ?: 1.0
                if (v == 0.0) "Volume cannot be zero"
                else "C = %.4f mol/L (Molar)".format(n / v)
            }
        ),
        FormulaDefinition(
            id = "health_bmi",
            name = "Body Mass Index (BMI)",
            description = "Calculates the Body Mass Index of a person given weight in kg and height in meters.",
            category = "Health",
            formulaDisplay = "BMI = weight / height²",
            fields = listOf(
                FormulaField("weight", "Weight", "kg"),
                FormulaField("height", "Height", "m")
            ),
            calculate = { inputs ->
                val weight = inputs["weight"]?.toDoubleOrNull() ?: 0.0
                val height = inputs["height"]?.toDoubleOrNull() ?: 1.0
                if (height == 0.0) "Height cannot be zero"
                else {
                    val bmi = weight / (height * height)
                    val classification = when {
                        bmi < 18.5 -> "Underweight"
                        bmi < 25.0 -> "Normal weight"
                        bmi < 30.0 -> "Overweight"
                        else -> "Obese"
                    }
                    "BMI = %.2f kg/m²\nClassification: %s".format(bmi, classification)
                }
            }
        ),
        FormulaDefinition(
            id = "finance_discount",
            name = "Discount & Retail Savings",
            description = "Calculates the final cost, total discounted savings, and tax adjustments.",
            category = "Finance & Retail",
            formulaDisplay = "Final Price = Price × (1 - Discount%) × (1 + Tax%)",
            fields = listOf(
                FormulaField("price", "Original Price", "$"),
                FormulaField("discount", "Discount Rate", "%"),
                FormulaField("tax", "Sales Tax Rate (Optional)", "%")
            ),
            calculate = { inputs ->
                val price = inputs["price"]?.toDoubleOrNull() ?: 0.0
                val discount = inputs["discount"]?.toDoubleOrNull() ?: 0.0
                val tax = inputs["tax"]?.toDoubleOrNull() ?: 0.0
                
                val discountFraction = (discount / 100.0).coerceIn(0.0, 1.0)
                val discountAmount = price * discountFraction
                val priceAfterDiscount = price - discountAmount
                
                val taxFraction = if (tax < 0.0) 0.0 else tax / 100.0
                val taxAmount = priceAfterDiscount * taxFraction
                val finalPrice = priceAfterDiscount + taxAmount
                
                "• Original Price: $%.2f\n• Discount Savings: -$%.2f (%.1f%% off)\n• Price Before Tax: $%.2f\n• Tax Applied: +$%.2f (%.1f%%)\n\n👉 FINAL PRICE: $%.2f".format(
                    price, discountAmount, discount, priceAfterDiscount, taxAmount, tax, finalPrice
                )
            }
        )
    )
}
