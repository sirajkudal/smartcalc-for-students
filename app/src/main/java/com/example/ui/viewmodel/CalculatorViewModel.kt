package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CalculatorDatabase
import com.example.data.database.CalculatorRepository
import com.example.data.database.HistoryEntity
import com.example.util.ChemicalFormulaParser
import com.example.util.EquationSolver
import com.example.util.FormulaDefinition
import com.example.util.FormulaFieldType
import com.example.util.FormulaProvider
import com.example.util.MathParser
import com.example.util.UnitConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val database = CalculatorDatabase.getDatabase(application)
    private val repository = CalculatorRepository(database.dao)

    // SYSTEM STATES
    private val _isDarkMode = MutableStateFlow(true) // Start in cool dark blue state by default
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _activeTab = MutableStateFlow("Basic") // "Basic", "Scientific", "Formulas", "Converter", "Solver"
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // DATABASE STREAMS
    val recentHistory: StateFlow<List<HistoryEntity>> = repository.recentHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteIds: StateFlow<Set<String>> = repository.allFavoritesFlow.map { list ->
        list.map { it.formulaId }.toSet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    // CO_NVERTER STATES
    private val _converterCategory = MutableStateFlow(UnitConverter.Category.LENGTH)
    val converterCategory: StateFlow<UnitConverter.Category> = _converterCategory.asStateFlow()

    private val _converterFromValue = MutableStateFlow("1.0")
    val converterFromValue: StateFlow<String> = _converterFromValue.asStateFlow()

    private val _converterFromUnit = MutableStateFlow(UnitConverter.unitsMap[UnitConverter.Category.LENGTH]!![0])
    val converterFromUnit: StateFlow<UnitConverter.UnitDefinition> = _converterFromUnit.asStateFlow()

    private val _converterToUnit = MutableStateFlow(UnitConverter.unitsMap[UnitConverter.Category.LENGTH]!![1])
    val converterToUnit: StateFlow<UnitConverter.UnitDefinition> = _converterToUnit.asStateFlow()

    private val _converterResult = MutableStateFlow("1000.0")
    val converterResult: StateFlow<String> = _converterResult.asStateFlow()

    // FORMULA STATES
    private val _selectedFormula = MutableStateFlow<FormulaDefinition?>(null)
    val selectedFormula: StateFlow<FormulaDefinition?> = _selectedFormula.asStateFlow()

    private val _formulaInputs = MutableStateFlow<Map<String, String>>(emptyMap())
    val formulaInputs: StateFlow<Map<String, String>> = _formulaInputs.asStateFlow()

    private val _formulaResult = MutableStateFlow("")
    val formulaResult: StateFlow<String> = _formulaResult.asStateFlow()

    private val _formulaSearchQuery = MutableStateFlow("")
    val formulaSearchQuery: StateFlow<String> = _formulaSearchQuery.asStateFlow()

    // BASIC/SCIENTIFIC CALC STATES
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _calculationResult = MutableStateFlow("")
    val calculationResult: StateFlow<String> = _calculationResult.asStateFlow()

    private val _isDegreeMode = MutableStateFlow(true) // Degrees by default for students
    val isDegreeMode: StateFlow<Boolean> = _isDegreeMode.asStateFlow()

    private val _activeSolution = MutableStateFlow<com.example.util.StepByStepSolver.Solution?>(null)
    val activeSolution: StateFlow<com.example.util.StepByStepSolver.Solution?> = _activeSolution.asStateFlow()

    // SOLVER STATES
    private val _solverType = MutableStateFlow("Linear") // "Linear" | "Quadratic"
    val solverType: StateFlow<String> = _solverType.asStateFlow()

    private val _solverA = MutableStateFlow("1.0")
    private val _solverB = MutableStateFlow("-5.0")
    private val _solverC = MutableStateFlow("6.0")

    val solverA: StateFlow<String> = _solverA.asStateFlow()
    val solverB: StateFlow<String> = _solverB.asStateFlow()
    val solverC: StateFlow<String> = _solverC.asStateFlow()

    private val _solverResult = MutableStateFlow<EquationSolver.EquationResult?>(null)
    val solverResult: StateFlow<EquationSolver.EquationResult?> = _solverResult.asStateFlow()

    init {
        // Run initial solver calculation
        solveEquation()
    }

    // SYSTEM ACTIONS
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun selectTab(tab: String) {
        _activeTab.value = tab
    }

    // BASIC/SCIENTIFIC ACTIONS
    fun appendExpression(char: String) {
        _expression.value = _expression.value + char
        // Real-time evaluation (preview) of mathematically complete strings
        evaluateRealTime()
    }

    fun clearAll() {
        _expression.value = ""
        _calculationResult.value = ""
        _activeSolution.value = null
    }

    fun backspace() {
        val current = _expression.value
        if (current.isNotEmpty()) {
            // If deleting functions like "sin(", "cos(", "tan(", etc., delete the whole word
            val deletedWord = checkWordBackspace(current)
            _expression.value = current.substring(0, current.length - deletedWord)
        }
        evaluateRealTime()
    }

    private fun checkWordBackspace(str: String): Int {
        val words = listOf(
            "sin⁻¹(", "cos⁻¹(", "tan⁻¹(", "sin(", "cos(", "tan(", 
            "logbase(", "logN(", "log(", "ln(", "sqrt(", "abs(", "π", "e"
        )
        for (w in words) {
            if (str.endsWith(w)) return w.length
        }
        return 1
    }

    fun toggleDegreeMode() {
        _isDegreeMode.value = !_isDegreeMode.value
        evaluateRealTime()
    }

    private fun evaluateRealTime() {
        val expr = _expression.value
        if (expr.isEmpty()) {
            _calculationResult.value = ""
            _activeSolution.value = null
            return
        }
        try {
            // Attempt a silent real time solve. If we get a syntax error, we don't display it immediately on partial input
            // unless the user presses equals.
            val solved = MathParser.evaluate(expr, _isDegreeMode.value)
            // Beautiful formatting of outcome
            _calculationResult.value = formatNumber(solved)
            _activeSolution.value = com.example.util.StepByStepSolver.solve(expr, _isDegreeMode.value)
        } catch (e: Exception) {
            // Do not update result with error on keypresses to maintain good UX
        }
    }

    fun performEquals() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        try {
            val solved = MathParser.evaluate(expr, _isDegreeMode.value)
            val finalRes = formatNumber(solved)
            _calculationResult.value = finalRes
            _activeSolution.value = com.example.util.StepByStepSolver.solve(expr, _isDegreeMode.value)
            
            // Save to historical DB inside coroutine scope
            viewModelScope.launch {
                repository.insertHistory(expr, finalRes)
            }
        } catch (e: Exception) {
            _calculationResult.value = "Error: ${e.message ?: "Invalid Syntax"}"
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun formatNumber(num: Double): String {
        return if (num.isInfinite()) "Infinity"
        else if (num.isNaN()) "NaN"
        else if (num % 1.0 == 0.0) num.toLong().toString()
        else "%.6f".format(num).trimEnd('0').trimEnd('.')
    }

    // FORMULA ACTIONS
    fun selectFormula(formula: FormulaDefinition?) {
        _selectedFormula.value = formula
        _formulaInputs.value = emptyMap()
        _formulaResult.value = ""
        if (formula != null) {
            // Pre-fill fields with empty or defaults
            val initial = mutableMapOf<String, String>()
            formula.fields.forEach { 
                initial[it.key] = if (it.type == FormulaFieldType.TEXT) "" else ""
            }
            _formulaInputs.value = initial
        }
    }

    fun updateFormulaInput(fieldKey: String, value: String) {
        val current = _formulaInputs.value.toMutableMap()
        current[fieldKey] = value
        _formulaInputs.value = current

        // Do not auto-calculate immediately
        _formulaResult.value = ""
    }

    fun calculateFormula() {
        val formula = _selectedFormula.value ?: return
        val currentInputs = _formulaInputs.value
        
        try {
            val resultString = formula.calculate(currentInputs)
            _formulaResult.value = resultString
        } catch (e: Exception) {
            _formulaResult.value = "Calculation error: ${e.message}"
        }
    }

    fun toggleFormulaFavorite(formulaId: String) {
        viewModelScope.launch {
            val currentFavs = favoriteIds.value
            val isFav = currentFavs.contains(formulaId)
            repository.toggleFavorite(formulaId, !isFav)
        }
    }

    fun setFormulaSearchQuery(query: String) {
        _formulaSearchQuery.value = query
    }

    // CONVERTER ACTIONS
    fun selectConverterCategory(category: UnitConverter.Category) {
        _converterCategory.value = category
        val units = UnitConverter.unitsMap[category] ?: return
        _converterFromUnit.value = units[0]
        _converterToUnit.value = if (units.size > 1) units[1] else units[0]
        performConversion()
    }

    fun updateConverterFromValue(value: String) {
        _converterFromValue.value = value
        performConversion()
    }

    fun selectFromUnit(unit: UnitConverter.UnitDefinition) {
        _converterFromUnit.value = unit
        performConversion()
    }

    fun selectToUnit(unit: UnitConverter.UnitDefinition) {
        _converterToUnit.value = unit
        performConversion()
    }

    private fun performConversion() {
        val valDouble = _converterFromValue.value.toDoubleOrNull()
        if (valDouble == null) {
            _converterResult.value = ""
            return
        }
        val fromU = _converterFromUnit.value
        val toU = _converterToUnit.value
        val category = _converterCategory.value
        try {
            val converted = UnitConverter.convert(valDouble, fromU, toU, category)
            _converterResult.value = "%.6f".format(converted).trimEnd('0').trimEnd('.')
        } catch (e: Exception) {
            _converterResult.value = "Error"
        }
    }

    // EQUATION SOLVER ACTIONS
    fun selectSolverType(type: String) {
        _solverType.value = type
        solveEquation()
    }

    fun updateSolverParam(param: String, value: String) {
        when (param) {
            "a" -> _solverA.value = value
            "b" -> _solverB.value = value
            "c" -> _solverC.value = value
        }
        _solverResult.value = null
    }

    fun solveEquation() {
        val valA = _solverA.value.toDoubleOrNull() ?: 0.0
        val valB = _solverB.value.toDoubleOrNull() ?: 0.0
        val valC = _solverC.value.toDoubleOrNull() ?: 0.0

        _solverResult.value = if (_solverType.value == "Linear") {
            EquationSolver.solveLinear(valA, valB, valC)
        } else {
            EquationSolver.solveQuadratic(valA, valB, valC)
        }
    }
}
