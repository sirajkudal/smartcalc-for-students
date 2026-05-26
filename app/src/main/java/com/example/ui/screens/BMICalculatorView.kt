package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CalculatorViewModel
import kotlin.math.pow

@Composable
fun BMICalculatorView(viewModel: CalculatorViewModel) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    // Key input states
    var isMetric by remember { mutableStateOf(true) }
    var isMale by remember { mutableStateOf(true) }
    var age by remember { mutableStateOf(25) }

    // Metric inputs
    var weightKgStr by remember { mutableStateOf("70") }
    var heightCmStr by remember { mutableStateOf("175") }

    // Imperial inputs
    var weightLbsStr by remember { mutableStateOf("154") }
    var heightFtStr by remember { mutableStateOf("5") }
    var heightInStr by remember { mutableStateOf("9") }

    // Parse values
    val weightKg: Double = if (isMetric) {
        weightKgStr.toDoubleOrNull() ?: 0.0
    } else {
        val lbs = weightLbsStr.toDoubleOrNull() ?: 0.0
        lbs * 0.45359237
    }

    val heightM: Double = if (isMetric) {
        val cm = heightCmStr.toDoubleOrNull() ?: 0.0
        cm / 100.0
    } else {
        val ft = heightFtStr.toDoubleOrNull() ?: 0.0
        val inches = heightInStr.toDoubleOrNull() ?: 0.0
        val totalInches = (ft * 12) + inches
        totalInches * 0.0254
    }

    // Calculations
    val bmi = if (heightM > 0.0 && weightKg > 0.0) {
        weightKg / (heightM * heightM)
    } else {
        0.0
    }

    val bmiCategory = when {
        bmi <= 0.0 -> "N/A"
        bmi < 16.0 -> "Severe Thinness"
        bmi < 17.0 -> "Moderate Thinness"
        bmi < 18.5 -> "Mild Thinness"
        bmi < 25.0 -> "Normal Weight"
        bmi < 30.0 -> "Overweight"
        bmi < 35.0 -> "Obese Class I"
        bmi < 40.0 -> "Obese Class II"
        else -> "Obese Class III"
    }

    val categoryColor = when {
        bmi <= 0.0 -> Color.Gray
        bmi < 18.5 -> Color(0xFF29B6F6) // Cool Blue
        bmi < 25.0 -> Color(0xFF66BB6A) // Healthy Green
        bmi < 30.0 -> Color(0xFFFFCA28) // Orange warning
        else -> Color(0xFFEF5350) // Red alert
    }

    // Ideal Weight limit standard 18.5 - 24.9
    val minIdealWeightKg = 18.5 * (heightM * heightM)
    val maxIdealWeightKg = 24.9 * (heightM * heightM)

    // Body fat formula: (1.20 × BMI) + (0.23 × Age) − (16.2 × Gender) − 5.4  (Gender: Male = 1, Female = 0)
    val genderFactor = if (isMale) 1.0 else 0.0
    val bodyFatPct = if (bmi > 0.0) {
        (1.20 * bmi) + (0.23 * age) - (16.2 * genderFactor) - 5.4
    } else {
        0.0
    }

    // Daily hydration guide: weight in kg * 35ml
    val waterIntakeLiters = weightKg * 0.035

    // Ponderal Index = mass / height³
    val ponderalIndex = if (heightM > 0.0) weightKg / heightM.pow(3) else 0.0

    // Body surface area calculations (DuBois formula)
    val bsa = if (weightKg > 0.0 && heightM > 0.0) {
        0.007184 * weightKg.pow(0.425) * (heightM * 100.0).pow(0.725)
    } else {
        0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) HighDensityBgDark else HighDensityBgLight)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Intro Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) HighDensitySurfaceDark else HighDensitySurfaceLight
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonitorWeight,
                        contentDescription = "BMI",
                        tint = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Clinical BMI Calculator",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) HighDensityHeaderDark else HighDensityHeaderLight
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Analyze Body Mass Index, body fat estimation, body surface area, and fluid requirements instantly with physiological metrics.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Primary input controllers
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) HighDensitySurfaceDark else HighDensitySurfaceLight
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // System selector tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0xFF131A26) else Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isMetric) (if (isDark) Color(0xFF1D2D50) else Color(0xFFD3E3FD))
                                else Color.Transparent
                            )
                            .clickable { isMetric = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Metric (kg/cm)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isMetric) (if (isDark) HighDensityPrimaryDark else Color(0xFF001C38))
                                    else (if (isDark) Color.LightGray else Color.Gray)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (!isMetric) (if (isDark) Color(0xFF1D2D50) else Color(0xFFD3E3FD))
                                else Color.Transparent
                            )
                            .clickable { isMetric = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Imperial (lbs/in)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (!isMetric) (if (isDark) HighDensityPrimaryDark else Color(0xFF001C38))
                                    else (if (isDark) Color.LightGray else Color.Gray)
                        )
                    }
                }

                // Gender & Age selection row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Male box
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isMale) (if (isDark) Color(0xFF152642) else Color(0xFFE3F2FD))
                                else (if (isDark) Color(0xFF1A2234) else Color(0xFFF8FAFC))
                            )
                            .border(
                                width = 1.dp,
                                color = if (isMale) (if (isDark) HighDensityPrimaryDark else Color(0xFF90CAF9))
                                        else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { isMale = true }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Male,
                            contentDescription = "Male",
                            tint = if (isMale) Color(0xFF2196F3) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Male",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isMale) (if (isDark) Color.White else Color(0xFF0D47A1)) else Color.Gray
                        )
                    }

                    // Female box
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (!isMale) (if (isDark) Color(0xFF381B2B) else Color(0xFFFCE4EC))
                                else (if (isDark) Color(0xFF1A2234) else Color(0xFFF8FAFC))
                            )
                            .border(
                                width = 1.dp,
                                color = if (!isMale) Color(0xFFF48FB1) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { isMale = false }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Female,
                            contentDescription = "Female",
                            tint = if (!isMale) Color(0xFFE91E63) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Female",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (!isMale) (if (isDark) Color.White else Color(0xFF880E4F)) else Color.Gray
                        )
                    }
                }

                // Age input slider
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Age",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "$age Yrs",
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                        )
                    }
                    Slider(
                        value = age.toFloat(),
                        onValueChange = { age = it.toInt() },
                        valueRange = 10f..90f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight,
                            activeTrackColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                        )
                    )
                }

                Divider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))

                // Numerical Fields
                if (isMetric) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Height cm
                        OutlinedTextField(
                            value = heightCmStr,
                            onValueChange = { heightCmStr = it },
                            label = { Text("Height (cm)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Weight kg
                        OutlinedTextField(
                            value = weightKgStr,
                            onValueChange = { weightKgStr = it },
                            label = { Text("Weight (kg)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = heightFtStr,
                                onValueChange = { heightFtStr = it },
                                label = { Text("Height (ft)", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = heightInStr,
                                onValueChange = { heightInStr = it },
                                label = { Text("Height (in)", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = weightLbsStr,
                            onValueChange = { weightLbsStr = it },
                            label = { Text("Weight (lbs)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Output Result Dashboard
        if (bmi > 0.0) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) HighDensitySurfaceDark else HighDensitySurfaceLight
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Physiological Diagnosis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    // Core Result Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "%.1f".format(bmi),
                                fontSize = 48.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = categoryColor,
                                lineHeight = 52.sp
                            )
                            Text(
                                "kg/m² (BMI)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        // Status Badge Box
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(categoryColor.copy(alpha = 0.15f))
                                .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bmiCategory.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = categoryColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Clinical Gauge Meter
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val progress = (bmi / 40.0).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = progress,
                            color = categoryColor,
                            trackColor = if (isDark) Color(0xFF131A26) else Color(0xFFE2E8F0),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("18.5 Under", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text("25.0 Normal", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text("30.0 Over", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text("Extremely Obese", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))

                    // Advanced Biological Analytics table grid
                    Text(
                        "Advanced Secondary Biomarkers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Line 1: Best Healthy Weight Bracket
                        BiomarkerRow(
                            label = "Healthy Weight Bracket",
                            value = if (isMetric) {
                                "%.1f - %.1f kg".format(minIdealWeightKg, maxIdealWeightKg)
                            } else {
                                "%.1f - %.1f lbs".format(minIdealWeightKg * 2.20462, maxIdealWeightKg * 2.20462)
                            },
                            icon = Icons.Default.VerifiedUser,
                            isDark = isDark
                        )

                        // Line 2: Ponderal Index
                        BiomarkerRow(
                            label = "Ponderal Index",
                            value = "%.2f kg/m³".format(ponderalIndex),
                            icon = Icons.Default.Shield,
                            isDark = isDark
                        )

                        // Line 3: Estimation of Body Fat Pct
                        BiomarkerRow(
                            label = "Est. Body Fat Ratio",
                            value = "%.1f%%".format(bodyFatPct.coerceAtLeast(0.0)),
                            icon = Icons.Default.PieChart,
                            isDark = isDark
                        )

                        // Line 4: Body Surface Area DuBois
                        BiomarkerRow(
                            label = "Est. Body Surface Area (BSA)",
                            value = "%.3f m²".format(bsa),
                            icon = Icons.Default.Fullscreen,
                            isDark = isDark
                        )

                        // Line 5: Water requirement
                        BiomarkerRow(
                            label = "Min Fluid Target",
                            value = if (isMetric) {
                                "%.2f Liters / day".format(waterIntakeLiters)
                            } else {
                                "%.2f fl oz / day".format(waterIntakeLiters * 33.814)
                            },
                            icon = Icons.Default.WaterDrop,
                            isDark = isDark
                        )
                    }

                    // Personalized clinical guidelines
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = categoryColor.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "★ Physiological Insights",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = categoryColor
                        )
                        val advice = when {
                            bmi < 18.5 -> "Your calculated BMI corresponds to Underweight. Discuss clinical nutrition plans with professional caregivers to increase calorie intake and lean muscular density comfortably."
                            bmi < 25.0 -> "Congratulations! Your BMI resides cleanly within the optimal medical standard zone. Exercise consistently and sustain nutrient-dense food ratios to perpetuate balanced homeostatic energy."
                            bmi < 30.0 -> "Your BMI classification signifies mild Overweight parameters. Regulating simple sodium ratios alongside regular aerobic activity of 150 minutes per week can easily stabilize parameters."
                            else -> "Clinical obesity indicators identified. Gradual heart-rate based cardiovascular exercises mixed with balanced caloric deficits is medically advised under qualified dietary consultation."
                        }
                        Text(
                            text = advice,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BiomarkerRow(
    label: String,
    value: String,
    icon: ImageVector,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDark) Color(0xFF0F172A).copy(alpha = 0.4f) else Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.5.dp,
                color = if (isDark) Color(0xFF2E3E5B).copy(alpha = 0.4f) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
