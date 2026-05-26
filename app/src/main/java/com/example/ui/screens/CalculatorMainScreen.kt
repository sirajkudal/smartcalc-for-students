package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.database.HistoryEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.util.FormulaDefinition
import com.example.util.FormulaProvider
import com.example.util.UnitConverter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorMainScreen(viewModel: CalculatorViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    // Clipboard copy helper
    val copyToClipboard: (String) -> Unit = { text ->
        if (text.isNotEmpty()) {
            clipboardManager.setText(AnnotatedString(text))
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Calculate Image Badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (isDarkMode) HighDensityPrimaryDark else HighDensityPrimaryLight,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Calculator",
                                tint = if (isDarkMode) HighDensityBgDark else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Column {
                            Text(
                                text = "SmartCalc",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkMode) HighDensityHeaderDark else HighDensityHeaderLight
                            )
                            Text(
                                text = "BY SK DEVELOPERS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkMode) Color.LightGray else Color.Gray,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                },
                actions = {
                    // Dark theme toggle
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = (if (isDarkMode) HighDensitySurfaceDark else Color(0xFFDDE3EA)).copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (isDarkMode) HighDensityPrimaryDark else HighDensityPrimaryLight
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                color = if (isDarkMode) Color(0xFF131A26) else Color(0xFFEEF2F9),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = (if (isDarkMode) Color(0xFF233148) else Color(0xFFE2E8F0)).copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(vertical = 8.dp, horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        NavigationItem("Basic", Icons.Default.PlusOne, "Basic", "Basic Calculator"),
                        NavigationItem("Scientific", Icons.Default.Functions, "Scientific", "Scientific Suite"),
                        NavigationItem("Formulas", Icons.Default.MenuBook, "Formulas", "Formulas"),
                        NavigationItem("Converter", Icons.Default.SwapHoriz, "Converter", "Converters"),
                        NavigationItem("Solver", Icons.Default.SmsFailed, "Solver", "Equation Solver"),
                        NavigationItem("BMI", Icons.Default.Favorite, "BMI", "BMI Calculator")
                    )
                    
                    tabs.forEach { item ->
                        val selected = activeTab == item.id
                        val contentColor = if (selected) {
                            if (isDarkMode) HighDensityPrimaryDark else Color(0xFF001C38)
                        } else {
                            if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        }
                        
                        val indicatorBg = if (selected) {
                            if (isDarkMode) Color(0xFF1D2D50) else Color(0xFFD3E3FD)
                        } else {
                            Color.Transparent
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectTab(item.id) }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(indicatorBg)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val iconVector = when (item.id) {
                                    "Basic" -> Icons.Default.Keyboard
                                    "Scientific" -> Icons.Default.Science
                                    "Formulas" -> Icons.Default.AutoStories
                                    "Converter" -> Icons.Default.CompareArrows
                                    "Solver" -> Icons.Default.Gavel
                                    "BMI" -> Icons.Default.Favorite
                                    else -> Icons.Default.Accessibility
                                }
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = item.desc,
                                    tint = contentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = item.label,
                                fontSize = 9.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = contentColor,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen routing transition
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "TabTransition"
            ) { tab ->
                when (tab) {
                    "Basic" -> BasicCalculatorView(viewModel, copyToClipboard)
                    "Scientific" -> ScientificCalculatorView(viewModel, copyToClipboard)
                    "Formulas" -> FormulaLibraryView(viewModel, copyToClipboard)
                    "Converter" -> UnitConverterView(viewModel, copyToClipboard)
                    "Solver" -> EquationSolverView(viewModel)
                    "BMI" -> BMICalculatorView(viewModel)
                }
            }
        }
    }
}

data class NavigationItem(val id: String, val icon: ImageVector, val label: String, val desc: String = "")

// ==========================================
// QUICK FORMULA SHORTCUT CHIPS ROW (HIGH DENSITY DESIGN THEME)
// ==========================================
@Composable
fun QuickFormulaShortcutsRow(viewModel: CalculatorViewModel) {
    val isDark = isSystemInDarkTheme()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chip 1: Quadratic Formula (Switches to Equation Solver tab and selects solver type Quadratic)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF1D2D50) else Color(0xFFD3E3FD))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF2B3A5A) else Color(0xFFAFC6EA),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    viewModel.selectTab("Solver")
                    viewModel.selectSolverType("Quadratic")
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("★", color = if (isDark) HighDensityPrimaryDark else Color(0xFF001C38), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Quadratic Formula",
                    color = if (isDark) HighDensityPrimaryDark else Color(0xFF001C38),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Chip 2: Ohm's Law
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF1E283A) else Color(0xFFF1F5F9))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    viewModel.selectTab("Formulas")
                    viewModel.selectFormula(FormulaProvider.formulas.firstOrNull { it.id == "phys_ohms_law" })
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("★", color = if (isDark) Color.LightGray else Color.Gray, fontSize = 11.sp)
                Text(
                    text = "Ohm's Law",
                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Chip 3: Molar Mass Calculator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0xFF1E283A) else Color(0xFFF1F5F9))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    viewModel.selectTab("Formulas")
                    viewModel.selectFormula(FormulaProvider.formulas.firstOrNull { it.id == "chem_molar_mass" })
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("★", color = if (isDark) Color.LightGray else Color.Gray, fontSize = 11.sp)
                Text(
                    text = "Molar Mass",
                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 1. BASIC CALCULATOR TAB
// ==========================================
@Composable
fun BasicCalculatorView(viewModel: CalculatorViewModel, onCopy: (String) -> Unit) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.calculationResult.collectAsState()
    val history by viewModel.recentHistory.collectAsState()
    var isHistoryOpen by remember { mutableStateOf(false) }
    var isDiscountOpen by remember { mutableStateOf(false) }
    var isCurrencyOpen by remember { mutableStateOf(false) }

    val activeSolution by viewModel.activeSolution.collectAsState()

    // Extract any existing numeric result to feed directly into the discount tool
    val computedPrice = remember(result, expression) {
        val cleanValue = if (result.isNotEmpty() && !result.startsWith("Error")) result else expression
        val digitsAndDot = cleanValue.filter { it.isDigit() || it == '.' }
        digitsAndDot.toDoubleOrNull() ?: 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display Terminal Screen
        CalculatorScreenDisplay(
            expression = expression,
            result = result,
            onCopy = onCopy,
            modifier = Modifier.weight(2.4f),
            activeSolution = activeSolution
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Student Content Chips (From Design Mockup)
        QuickFormulaShortcutsRow(viewModel = viewModel)

        Spacer(modifier = Modifier.height(6.dp))

        // Toggle buttons row for History, Discount & Currency tools
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = { 
                        isHistoryOpen = !isHistoryOpen 
                        if (isHistoryOpen) {
                            isDiscountOpen = false
                            isCurrencyOpen = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isHistoryOpen) Icons.Default.KeyboardArrowDown else Icons.Default.History,
                        contentDescription = "History Toggle",
                        modifier = Modifier.padding(end = 4.dp).size(16.dp)
                    )
                    Text(if (isHistoryOpen) "Hide History" else "History (${history.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { 
                        isDiscountOpen = !isDiscountOpen 
                        if (isDiscountOpen) {
                            isHistoryOpen = false
                            isCurrencyOpen = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isDiscountOpen) Icons.Default.KeyboardArrowDown else Icons.Default.Percent,
                        contentDescription = "Discount Toggle",
                        modifier = Modifier.padding(end = 4.dp).size(16.dp)
                    )
                    Text(if (isDiscountOpen) "Hide Discount" else "Discount Tool %", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { 
                        isCurrencyOpen = !isCurrencyOpen 
                        if (isCurrencyOpen) {
                            isHistoryOpen = false
                            isDiscountOpen = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isCurrencyOpen) Icons.Default.KeyboardArrowDown else Icons.Default.CompareArrows,
                        contentDescription = "Currency Toggle",
                        modifier = Modifier.padding(end = 4.dp).size(16.dp)
                    )
                    Text(if (isCurrencyOpen) "Hide Currency" else "Currency Ex 💱", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isHistoryOpen && history.isNotEmpty()) {
                IconButton(
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.weight(3.5f)) {
            AnimatedContent(
                targetState = when {
                    isHistoryOpen -> 1
                    isDiscountOpen -> 2
                    isCurrencyOpen -> 3
                    else -> 0
                },
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "BasicOverlaySwitcher"
            ) { state ->
                when (state) {
                    1 -> {
                        HistoryListView(history = history, onHistoryClick = { entry ->
                            viewModel.clearAll()
                            viewModel.appendExpression(entry.expression)
                            isHistoryOpen = false
                        })
                    }
                    2 -> {
                        QuickDiscountPanel(
                            initialPrice = computedPrice,
                            onClose = { isDiscountOpen = false },
                            onCopy = onCopy
                        )
                    }
                    3 -> {
                        QuickCurrencyExchangePanel(
                            initialAmount = computedPrice,
                            onClose = { isCurrencyOpen = false },
                            onCopy = onCopy
                        )
                    }
                    else -> {
                        BasicKeypad(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickDiscountPanel(
    initialPrice: Double,
    onClose: () -> Unit,
    onCopy: (String) -> Unit
) {
    var rawPrice by remember(initialPrice) { mutableStateOf(if (initialPrice > 0) "%.2f".format(initialPrice) else "") }
    var discountPercent by remember { mutableStateOf("15") } // Default 15% discount
    var taxPercent by remember { mutableStateOf("0") }
    
    val isDark = isSystemInDarkTheme()
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF131A26) else Color(0xFFEEF2F9)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 1.dp,
                color = if (isDark) Color(0xFF233148) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Percent,
                        contentDescription = "Discount Tool",
                        tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Quick Discount & Tax Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp),
                        tint = if (isDark) Color.LightGray else Color.Gray
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = rawPrice,
                    onValueChange = { rawPrice = it },
                    label = { Text("Original Price (₹)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary) },
                    placeholder = { Text("0.00") },
                    textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1.2f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = discountPercent,
                    onValueChange = { discountPercent = it },
                    label = { Text("Discount (%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary) },
                    placeholder = { Text("20") },
                    textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = taxPercent,
                    onValueChange = { taxPercent = it },
                    label = { Text("Sales Tax (%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary) },
                    placeholder = { Text("5") },
                    textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            // Fast presets buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.LightGray else Color.DarkGray)
                listOf("5", "10", "15", "20", "25", "30", "50", "75").forEach { preset ->
                    val isSelected = discountPercent == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) (if (isDark) Color(0xFF1D2D50) else Color(0xFFD3E3FD))
                                else (if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                            )
                            .clickable { discountPercent = preset }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$preset%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) (if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7))
                                    else (if (isDark) Color(0xFF94A3B8) else Color(0xFF475569))
                        )
                    }
                }
            }

            // Calculations live processing
            val priceVal = rawPrice.toDoubleOrNull() ?: 0.0
            val discVal = discountPercent.toDoubleOrNull() ?: 0.0
            val taxVal = taxPercent.toDoubleOrNull() ?: 0.0
            
            val discFraction = (discVal / 100.0).coerceIn(0.0, 1.0)
            val baseSavings = priceVal * discFraction
            val discountedPrice = priceVal - baseSavings
            val taxAmount = discountedPrice * (taxVal / 100.0).coerceAtLeast(0.0)
            val finalPrice = discountedPrice + taxAmount
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = (if (isDark) Color(0xFF0F172A) else Color.White).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Discount: -₹%.2f (%s%%)".format(baseSavings, discountPercent.ifEmpty { "0" }),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF10B981) else Color(0xFF059669)
                    )
                    Text(
                        text = "Final Net Price: ₹%.2f".format(finalPrice),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
                    )
                }
                
                Button(
                    onClick = {
                        onCopy("%.2f".format(finalPrice))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF1E2D4A) else Color(0xFFE0E7FF),
                        contentColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF1E40AF)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, "Copy price", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Net", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickCurrencyExchangePanel(
    initialAmount: Double,
    onClose: () -> Unit,
    onCopy: (String) -> Unit
) {
    var rawAmount by remember(initialAmount) { mutableStateOf(if (initialAmount > 0) "%.2f".format(initialAmount) else "") }
    
    // Choose currency lists
    val currencies = com.example.util.UnitConverter.unitsMap[com.example.util.UnitConverter.Category.CURRENCY] ?: emptyList()
    
    var fromCurrency by remember { mutableStateOf(currencies.firstOrNull { it.symbol.contains("USD") } ?: (currencies.firstOrNull() ?: com.example.util.UnitConverter.UnitDefinition("US Dollar", "USD ($)", 1.0))) }
    var toCurrency by remember { mutableStateOf(currencies.firstOrNull { it.symbol.contains("EUR") } ?: (currencies.getOrNull(1) ?: (currencies.firstOrNull() ?: com.example.util.UnitConverter.UnitDefinition("Euro", "EUR (€)", 1.08)))) }
    
    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }
    
    val isDark = isSystemInDarkTheme()
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF131A26) else Color(0xFFEEF2F9)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 1.dp,
                color = if (isDark) Color(0xFF233148) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = "Currency Exchange",
                        tint = if (isDark) Color(0xFFFFB300) else Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Quick Currency Exchange",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp),
                        tint = if (isDark) Color.LightGray else Color.Gray
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = rawAmount,
                    onValueChange = { rawAmount = it },
                    label = { Text("Exchange Amount", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary) },
                    placeholder = { Text("100.00") },
                    textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                    modifier = Modifier.weight(1.2f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                // From select
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { showFromDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                            contentColor = if (isDark) Color.White else Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(fromCurrency.symbol, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, "Select", modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = showFromDropdown,
                        onDismissRequest = { showFromDropdown = false }
                    ) {
                        currencies.forEach { curr ->
                            DropdownMenuItem(
                                text = { Text("${curr.name} (${curr.symbol})") },
                                onClick = {
                                    fromCurrency = curr
                                    showFromDropdown = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        val temp = fromCurrency
                        fromCurrency = toCurrency
                        toCurrency = temp
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = "Swap Currencies",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // To select
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { showToDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                            contentColor = if (isDark) Color.White else Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(toCurrency.symbol, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, "Select", modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = showToDropdown,
                        onDismissRequest = { showToDropdown = false }
                    ) {
                        currencies.forEach { curr ->
                            DropdownMenuItem(
                                text = { Text("${curr.name} (${curr.symbol})") },
                                onClick = {
                                    toCurrency = curr
                                    showToDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Calculations live processing
            val amtVal = rawAmount.toDoubleOrNull() ?: 0.0
            val convertedVal = com.example.util.UnitConverter.convert(amtVal, fromCurrency, toCurrency, com.example.util.UnitConverter.Category.CURRENCY)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = (if (isDark) Color(0xFF0F172A) else Color.White).copy(alpha = 0.8f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Rate: 1 %s = %.4f %s".format(
                            fromCurrency.symbol.substringBefore(" "), 
                            com.example.util.UnitConverter.convert(1.0, fromCurrency, toCurrency, com.example.util.UnitConverter.Category.CURRENCY), 
                            toCurrency.symbol.substringBefore(" ")
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.LightGray else Color.DarkGray
                    )
                    Text(
                        text = "%,.2f %s".format(convertedVal, toCurrency.symbol),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFFFFB300) else Color(0xFFD97706)
                    )
                }
                
                Button(
                    onClick = {
                        onCopy("%.2f".format(convertedVal))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF2C1F10) else Color(0xFFFEF3C7),
                        contentColor = if (isDark) Color(0xFFFFB300) else Color(0xFFD97706)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, "Copy amount", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Value", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculatorScreenDisplay(
    expression: String,
    result: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    activeSolution: com.example.util.StepByStepSolver.Solution? = null
) {
    var showTooltip by remember { mutableStateOf(false) }
    var showSolutionDialog by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { 
                    if (activeSolution != null) {
                        showSolutionDialog = true
                    }
                },
                onLongClick = { onCopy(if (result.isNotEmpty() && !result.startsWith("Error")) result else expression) }
            )
            .border(
                width = 2.dp,
                color = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF0B1329) else Color(0xFFF8FAFC),
            contentColor = if (isDark) Color.White else Color(0xFF0F172A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SCREEN • STUDENT LAB",
                    fontSize = 11.sp,
                    color = (if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight).copy(alpha = 0.9f),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )

                // Tooltip trigger
                Box {
                    IconButton(
                        onClick = { showTooltip = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tips",
                            tint = (if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (showTooltip) {
                        Dialog(onDismissRequest = { showTooltip = false }) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (isDark) HighDensitySurfaceDark else HighDensitySurfaceLight),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Student Shortcuts", fontWeight = FontWeight.Bold, color = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("• Long-press the display screen any time to copy the current output value to your clipboard.", fontSize = 13.sp)
                                    Text("• Tap the terminal display when solving scientific functions to view step-by-step breakdowns.", fontSize = 13.sp)
                                    Text("• In Scientific Tab, toggle Degrees/Radians to switch trigonometry bases automatically.", fontSize = 13.sp)
                                    Text("• Formulas auto-calculate results instantly as you edit raw student fields.", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { showTooltip = false },
                                        modifier = Modifier.align(Alignment.End),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight,
                                            contentColor = if (isDark) HighDensityBgDark else Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Got it")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Math Expression Input Terminal (aligned right, auto-scroll and container bounds safe)
            val expressionScrollState = rememberScrollState()
            LaunchedEffect(expression) {
                expressionScrollState.scrollTo(expressionScrollState.maxValue)
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    modifier = Modifier.horizontalScroll(expressionScrollState),
                    textAlign = TextAlign.End,
                    fontSize = when {
                        expression.length > 30 -> 16.sp
                        expression.length > 20 -> 22.sp
                        expression.length > 12 -> 28.sp
                        else -> 36.sp
                    },
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                // Auto solved output label (aligned right, auto-scroll and container bounds safe)
                val resultScrollState = rememberScrollState()
                LaunchedEffect(result) {
                    resultScrollState.scrollTo(resultScrollState.maxValue)
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = if (result.isNotEmpty()) result else "",
                        modifier = Modifier.horizontalScroll(resultScrollState),
                        textAlign = TextAlign.End,
                        fontSize = when {
                            result.length > 30 -> 18.sp
                            result.length > 20 -> 24.sp
                            result.length > 12 -> 32.sp
                            else -> 42.sp
                        },
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (result.startsWith("Error")) MaterialTheme.colorScheme.error 
                                else (if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)),
                        maxLines = 1
                    )
                }

                if (activeSolution != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .background(
                                color = (if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight).copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showSolutionDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Solution Guide",
                            tint = if (isDark) Color(0xFFFFB300) else Color(0xFFF57F17),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Step-by-Step Solution",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                        )
                    }
                }
            }
        }
    }

    if (showSolutionDialog && activeSolution != null) {
        Dialog(onDismissRequest = { showSolutionDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(
                        width = 1.dp,
                        color = (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeSolution.title,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showSolutionDialog = false },
                            modifier = Modifier.size(28.dp).padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeSolution.steps.forEach { step ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isDark) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 0.5.dp,
                                        color = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = step,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = (if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "FINAL OUTCOME",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = activeSolution.finalResult,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = {
                                onCopy(activeSolution.finalResult)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight,
                                contentColor = if (isDark) HighDensityBgDark else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BasicKeypad(viewModel: CalculatorViewModel) {
    val buttons = listOf(
        listOf("C", "⌫", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("√", "0", ".", "=")
    )

    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { label ->
                    val isAction = label == "C" || label == "⌫" || label == "%"
                    val isOperator = label == "÷" || label == "×" || label == "−" || label == "+" || label == "="
                    val btnBg = when {
                        label == "C" -> if (isDark) ClearBgDark else ClearBgLight
                        label == "=" -> if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                        isAction -> if (isDark) SciBgDark else SciBgLight
                        isOperator -> if (isDark) OperatorBgDark else OperatorBgLight
                        else -> if (isDark) DigitBgDark else DigitBgLight
                    }
                    val btnFg = when {
                        label == "C" -> if (isDark) ClearTextDark else ClearTextLight
                        label == "=" -> if (isDark) HighDensityBgDark else Color.White
                        isAction -> if (isDark) SciTextDark else SciTextLight
                        isOperator -> if (isDark) OperatorTextDark else OperatorTextLight
                        else -> if (isDark) DigitTextDark else DigitTextLight
                    }
                    val btnShape = when {
                        label == "C" || label == "=" -> RoundedCornerShape(24.dp)
                        else -> RoundedCornerShape(20.dp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(btnShape)
                            .background(btnBg)
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0xFF2E3E5B).copy(alpha = 0.4f) else Color(0xFFCBD5E1).copy(alpha = 0.5f),
                                shape = btnShape
                            )
                            .clickable {
                                when (label) {
                                    "C" -> viewModel.clearAll()
                                    "⌫" -> viewModel.backspace()
                                    "=" -> viewModel.performEquals()
                                    "−" -> viewModel.appendExpression("-")
                                    else -> viewModel.appendExpression(label)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = btnFg
                            )
                            // Short educational labels under buttons to assist beginners
                            val subtitle = when(label) {
                                "C" -> "clear"
                                "⌫" -> "del"
                                "%" -> "percent"
                                "÷" -> "divide"
                                "×" -> "multiply"
                                "−" -> "minus"
                                "+" -> "plus"
                                "√" -> "root"
                                "=" -> "solve"
                                else -> ""
                            }
                            if (subtitle.isNotEmpty()) {
                                Text(subtitle, fontSize = 8.sp, color = btnFg.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryListView(history: List<HistoryEntity>, onHistoryClick: (HistoryEntity) -> Unit) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.HistoryToggleOff,
                    contentDescription = "Empty",
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Your calculation history is empty.",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryClick(item) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.expression,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "= ${item.result}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CallReceived,
                            contentDescription = "Load",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// 2. SCIENTIFIC CALCULATOR TAB
// ==========================================
@Composable
fun ScientificCalculatorView(viewModel: CalculatorViewModel, onCopy: (String) -> Unit) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.calculationResult.collectAsState()
    val isDegree by viewModel.isDegreeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Degree / Radian toggle on top with display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Scientific Mode",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.toggleDegreeMode() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Base",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isDegree) "DEGREES (DEG)" else "RADIANS (RAD)",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val activeSolution by viewModel.activeSolution.collectAsState()

        // Display
        CalculatorScreenDisplay(
            expression = expression,
            result = result,
            onCopy = onCopy,
            modifier = Modifier.weight(2.2f),
            activeSolution = activeSolution
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick student shortcuts
        QuickFormulaShortcutsRow(viewModel = viewModel)

        Spacer(modifier = Modifier.height(10.dp))

        // Keypad grid
        Box(modifier = Modifier.weight(3.5f)) {
            ScientificKeypad(viewModel)
        }
    }
}

@Composable
fun ScientificKeypad(viewModel: CalculatorViewModel) {
    val buttons = listOf(
        listOf("sin", "cos", "tan", "sin⁻¹"),
        listOf("cos⁻¹", "tan⁻¹", "log", "ln"),
        listOf("logbase", ",", "^", "abs"),
        listOf("!", "π", "e", "("),
        listOf(")", "C", "⌫", "=")
    )

    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    val isAction = label == "C" || label == "⌫"
                    val isEquals = label == "="
                    val isTrigger = label.endsWith("(") || label == "sin" || label == "cos" || label == "tan" || label == "sin⁻¹" || label == "cos⁻¹" || label == "tan⁻¹" || label == "log" || label == "ln" || label == "logbase" || label == "abs" || label == "^" || label == "!"
                    
                    val btnBg = when {
                        isEquals -> if (isDark) HighDensityPrimaryDark else HighDensityPrimaryLight
                        isAction -> if (isDark) ClearBgDark else ClearBgLight
                        isTrigger -> if (isDark) OperatorBgDark else OperatorBgLight
                        else -> if (isDark) DigitBgDark else DigitBgLight
                    }
                    val btnFg = when {
                        isEquals -> if (isDark) HighDensityBgDark else Color.White
                        isAction -> if (isDark) ClearTextDark else ClearTextLight
                        isTrigger -> if (isDark) OperatorTextDark else OperatorTextLight
                        else -> if (isDark) DigitTextDark else DigitTextLight
                    }
                    val btnShape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(btnShape)
                            .background(btnBg)
                            .border(
                                width = 1.dp,
                                color = if (isDark) Color(0xFF2E3E5B).copy(alpha = 0.4f) else Color(0xFFCBD5E1).copy(alpha = 0.5f),
                                shape = btnShape
                            )
                            .clickable {
                                when (label) {
                                    "C" -> viewModel.clearAll()
                                    "⌫" -> viewModel.backspace()
                                    "=" -> viewModel.performEquals()
                                    "sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹", "log", "ln", "abs" -> {
                                        viewModel.appendExpression("$label(")
                                    }
                                    "logbase" -> viewModel.appendExpression("logbase(")
                                    else -> viewModel.appendExpression(label)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontSize = if (label.length > 5) 12.sp else 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = btnFg
                            )
                            // Student explanation labels under science equations
                            val caption = when (label) {
                                "sin" -> "sine"
                                "cos" -> "cosine"
                                "tan" -> "tangent"
                                "sin⁻¹" -> "arcsin"
                                "cos⁻¹" -> "arccos"
                                "tan⁻¹" -> "arctan"
                                "log" -> "log₁₀"
                                "ln" -> "log_e"
                                "logbase" -> "log_b(x)"
                                "," -> "comma"
                                "^" -> "power"
                                "abs" -> "absolute"
                                "!" -> "factorial"
                                "π" -> "pi (3.14)"
                                "e" -> "euler"
                                "=" -> "solve"
                                else -> ""
                            }
                            if (caption.isNotEmpty()) {
                                Text(caption, fontSize = 7.sp, color = btnFg.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. FORMULA LIBRARY TAB
// ==========================================
@Composable
fun FormulaLibraryView(viewModel: CalculatorViewModel, onCopy: (String) -> Unit) {
    val selectedFormula by viewModel.selectedFormula.collectAsState()
    val searchQuery by viewModel.formulaSearchQuery.collectAsState()
    val favorites by viewModel.favoriteIds.collectAsState()

    var activeCategoryFilter by remember { mutableStateOf("All") }

    if (selectedFormula != null) {
        FormulaSolverView(
            formula = selectedFormula!!,
            viewModel = viewModel,
            isFavorite = favorites.contains(selectedFormula!!.id),
            onCopy = onCopy,
            onBack = { viewModel.selectFormula(null) }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Formula Search Top Header
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setFormulaSearchQuery(it) },
                placeholder = { Text("Search speed, area, concentration...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setFormulaSearchQuery("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            val categories = listOf("All", "Finance & Retail", "Physics", "Maths: Area", "Maths: Volume", "Maths: Perimeter", "Chemistry", "Health", "★ Favorites")
            val isDark = isSystemInDarkTheme()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    val isSelected = activeCategoryFilter == cat
                    val chipBg = if (isSelected) {
                        if (isDark) Color(0xFF1D2D50) else Color(0xFFD3E3FD)
                    } else {
                        if (isDark) Color(0xFF1E283A) else Color(0xFFF1F5F9)
                    }
                    val chipBorderColor = if (isSelected) {
                        if (isDark) Color(0xFF2B3A5A) else Color(0xFFAFC6EA)
                    } else {
                        if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                    }
                    val textColor = if (isSelected) {
                        if (isDark) HighDensityPrimaryDark else Color(0xFF001C38)
                    } else {
                        if (isDark) Color.LightGray else Color.Gray
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorderColor, RoundedCornerShape(20.dp))
                            .clickable { activeCategoryFilter = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter formulas list
            val filteredFormulas = FormulaProvider.formulas.filter { form ->
                val matchesSearch = form.name.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT)) ||
                        form.description.lowercase(Locale.ROOT).contains(searchQuery.lowercase(Locale.ROOT))
                
                val matchesCategory = when (activeCategoryFilter) {
                    "All" -> true
                    "★ Favorites" -> favorites.contains(form.id)
                    "♥ Bookmarks" -> favorites.contains(form.id)
                    else -> form.category == activeCategoryFilter
                }

                matchesSearch && matchesCategory
            }

            if (filteredFormulas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "No formulas found",
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No matching student formulas found.",
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredFormulas) { formula ->
                        FormulaCardItem(
                            formula = formula,
                            isFavorite = favorites.contains(formula.id),
                            onSelect = { viewModel.selectFormula(formula) },
                            onToggleFav = { viewModel.toggleFormulaFavorite(formula.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaCardItem(
    formula: FormulaDefinition,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFav: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formula.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formula.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                IconButton(onClick = onToggleFav) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Formula Display Highlight in grey card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = formula.formulaDisplay,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formula.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FormulaSolverView(
    formula: FormulaDefinition,
    viewModel: CalculatorViewModel,
    isFavorite: Boolean,
    onCopy: (String) -> Unit,
    onBack: () -> Unit
) {
    val inputs by viewModel.formulaInputs.collectAsState()
    val result by viewModel.formulaResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Formula interactive top controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text(
                text = "Interactive Solver",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { viewModel.toggleFormulaFavorite(formula.id) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Fav",
                    tint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(formula.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(formula.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(formula.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                Spacer(modifier = Modifier.height(12.dp))

                // Render Equation Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        formula.formulaDisplay,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fields listing
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(formula.fields) { field ->
                val textVal = inputs[field.key] ?: ""
                
                OutlinedTextField(
                    value = textVal,
                    onValueChange = { viewModel.updateFormulaInput(field.key, it) },
                    label = { Text(field.label) },
                    placeholder = { Text(field.unit) },
                    suffix = { Text(field.unit) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.calculateFormula() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Solve", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Solution Container
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "RESULT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (result.isNotEmpty()) {
                        IconButton(
                            onClick = { onCopy(result) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = result.ifEmpty { "Enter input parameters above..." },
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}


// ==========================================
// 4. UNIT CONVERTER TAB
// ==========================================
@Composable
fun UnitConverterView(viewModel: CalculatorViewModel, onCopy: (String) -> Unit) {
    val category by viewModel.converterCategory.collectAsState()
    val fromValue by viewModel.converterFromValue.collectAsState()
    val fromUnit by viewModel.converterFromUnit.collectAsState()
    val toUnit by viewModel.converterToUnit.collectAsState()
    val result by viewModel.converterResult.collectAsState()

    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Integrated Unit Converter & Exchange", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        Text("Convert length, weight, temperature, area, speed, volumes, or currencies instantly", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(16.dp))

        // Category selections scroller
        val categories = UnitConverter.Category.values()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = category == cat
                val chipColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(chipColor)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectConverterCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.name,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // FROM VALUE input
                OutlinedTextField(
                    value = fromValue,
                    onValueChange = { viewModel.updateConverterFromValue(it) },
                    label = { Text("From Value") },
                    placeholder = { Text("e.g. 1.0") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // UNIT SELECTORS Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // FROM UNIT Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { showFromDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(fromUnit.symbol, fontWeight = FontWeight.Bold, maxLines = 1)
                            Icon(Icons.Default.ArrowDropDown, "Select")
                        }
                        DropdownMenu(
                            expanded = showFromDropdown,
                            onDismissRequest = { showFromDropdown = false }
                        ) {
                            UnitConverter.unitsMap[category]?.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text("${u.name} (${u.symbol})") },
                                    onClick = {
                                        viewModel.selectFromUnit(u)
                                        showFromDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = "swap",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // TO UNIT Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { showToDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(toUnit.symbol, fontWeight = FontWeight.Bold, maxLines = 1)
                            Icon(Icons.Default.ArrowDropDown, "Select")
                        }
                        DropdownMenu(
                            expanded = showToDropdown,
                            onDismissRequest = { showToDropdown = false }
                        ) {
                            UnitConverter.unitsMap[category]?.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text("${u.name} (${u.symbol})") },
                                    onClick = {
                                        viewModel.selectToUnit(u)
                                        showToDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // RESULT Display
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CONVERTED ANSWER", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { onCopy(result) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, "copy", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (result.isNotEmpty()) "$result  ${toUnit.symbol}" else "Enter valid values...",
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// 5. EQUATION SOLVER TAB
// ==========================================
@Composable
fun EquationSolverView(viewModel: CalculatorViewModel) {
    val solverType by viewModel.solverType.collectAsState()
    val solverA by viewModel.solverA.collectAsState()
    val solverB by viewModel.solverB.collectAsState()
    val solverC by viewModel.solverC.collectAsState()
    val result by viewModel.solverResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Smart Equation Solver", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Text("Select linear or quadratic, supply parameters, view formulas & steps", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }

        // Equation Type selector switch
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Linear", "Quadratic").forEach { type ->
                    val isSelected = solverType == type
                    val btnBg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    val btnFg = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(btnBg)
                            .clickable { viewModel.selectSolverType(type) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(type, color = btnFg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Input parameter fields
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (solverType == "Linear") "Equation:  a·x + b = c" else "Standard Form:  a·x² + b·x + c = 0",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = solverA,
                            onValueChange = { viewModel.updateSolverParam("a", it) },
                            label = { Text("a") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = solverB,
                            onValueChange = { viewModel.updateSolverParam("b", it) },
                            label = { Text("b") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = solverC,
                            onValueChange = { viewModel.updateSolverParam("c", it) },
                            label = { Text("c") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.solveEquation() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Solve", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Output Display Card
        if (result != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (result!!.isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SOLUTON ROOTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (result!!.isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (result!!.roots.isEmpty()) {
                            Text(
                                text = "No solutions or contradiction",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            result!!.roots.forEach { root ->
                                Text(
                                    text = root,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result!!.isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Calculation steps (LaTeX styled reduction steps)
            item {
                Text(
                    text = "STEP-BY-STEP REDUCTION DETAILS",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(result!!.steps) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (result!!.steps.indexOf(step) + 1).toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = step.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mathematical formulation box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = step.formula,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = step.explanation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
