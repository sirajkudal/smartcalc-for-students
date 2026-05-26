package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.MathParser
import com.example.util.StepByStepSolver
import com.example.util.EquationSolver
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SmartCalc", appName)
  }

  @Test
  fun `test math parser basic operations`() {
    val result = MathParser.evaluate("2 + 3 * 5", false)
    assertEquals(17.0, result, 1e-9)
  }

  @Test
  fun `test math parser trigonometric functions`() {
    // 30 degrees = 0.52359877 radians
    val resultRad = MathParser.evaluate("sin(30)", false) // 30 radians
    val resultDeg = MathParser.evaluate("sin(30)", true)  // 30 degrees
    assertEquals(-0.98803162409, resultRad, 1e-5)
    assertEquals(0.5, resultDeg, 1e-5)
  }

  @Test
  fun `test math parser division by zero throws exception`() {
    try {
      MathParser.evaluate("10 / 0", false)
      fail("Should have thrown ArithmeticException")
    } catch (e: ArithmeticException) {
      assertEquals("Division by zero", e.message)
    }
  }

  @Test
  fun `test step by step solver trigonometric sine`() {
    val solution = StepByStepSolver.solve("sin(30)", true)
    assertNotNull(solution)
    assertEquals("Sine Calculation (Degrees)", solution?.title)
    assertTrue(solution!!.steps.any { it.contains("convert the angle to Radians") })
    assertEquals("0.5", solution.finalResult)
  }

  @Test
  fun `test linear equation resolver`() {
    // 2x + 4 = 10 -> x = 3
    val result = EquationSolver.solveLinear(2.0, 4.0, 10.0)
    assertFalse(result.isError)
    assertEquals(1, result.roots.size)
    assertEquals("x = 3.0000", result.roots[0])
  }

  @Test
  fun `test linear equation contradiction`() {
    // 0x + 4 = 10 -> contradiction
    val result = EquationSolver.solveLinear(0.0, 4.0, 10.0)
    assertTrue(result.isError)
    assertEquals(0, result.roots.size)
    assertEquals("No solution (contradiction)", result.errorMessage)
  }

  @Test
  fun `test quadratic equation distinct roots`() {
    // x^2 - 5x + 6 = 0 -> (x-2)(x-3) = 0 -> x = 2, 3
    val result = EquationSolver.solveQuadratic(1.0, -5.0, 6.0)
    assertFalse(result.isError)
    assertEquals(2, result.roots.size)
    assertEquals("x₁ = 3.0000", result.roots[0])
    assertEquals("x₂ = 2.0000", result.roots[1])
  }

  @Test
  fun `test quadratic equation complex roots`() {
    // x^2 + 1 = 0 -> x = ±i
    val result = EquationSolver.solveQuadratic(1.0, 0.0, 1.0)
    assertEquals(2, result.roots.size)
    assertEquals("x₁ = 0.0000 + 1.0000i", result.roots[0])
    assertEquals("x₂ = 0.0000 - 1.0000i", result.roots[1])
  }
}

