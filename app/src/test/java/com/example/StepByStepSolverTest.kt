package com.example

import com.example.util.StepByStepSolver
import org.junit.Test
import org.junit.Assert.*

class StepByStepSolverTest {
    @Test
    fun testSolver() {
        assertNull(StepByStepSolver.solve("4+4", false))
        assertNotNull(StepByStepSolver.solve("sin(30)", true))
    }
}
