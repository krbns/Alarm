package com.kurban.alarm.domain.model

data class MathChallenge(
    val firstOperand: Int,
    val secondOperand: Int,
    val operator: MathOperator,
    val correctAnswer: Int
) {
    companion object {
        fun generate(): MathChallenge {
            val isAddition = (0..1).random() == 0
            val a = (1..20).random()
            val b = (1..20).random()

            return if (isAddition) {
                MathChallenge(
                    firstOperand = a,
                    secondOperand = b,
                    operator = MathOperator.PLUS,
                    correctAnswer = a + b
                )
            } else {
                val (first, second) = if (a >= b) a to b else a to b
                MathChallenge(
                    firstOperand = first,
                    secondOperand = second,
                    operator = MathOperator.MINUS,
                    correctAnswer = first - second
                )
            }
        }
    }
}

enum class MathOperator {
    PLUS, MINUS
}