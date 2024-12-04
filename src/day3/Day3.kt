package day3

import day3.InstructionState.*
import readInput

fun main() {
    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("day3/input")
    val solution = part1(testInput)
    if (solution == 174336360) {
        println("correct")
    } else {
        println("incorrect")
    }
}

enum class EvalState {
    BLANK, M, U, L, OPEN, DIGIT, COMMA
}

fun part1(input: List<String>): Int {
    var sum = 0


    fun readLine(line: String) {

        var instruction = Instruction()

        fun isDigit(char: Char): Boolean = char in '0'..'9'

        fun reset(): EvalState {
            instruction = Instruction()
            return EvalState.BLANK
        }

        for (char: Char in line) {

            instruction.currentState = when (instruction.currentState) {
                EvalState.BLANK -> if (char == 'm') EvalState.M else EvalState.BLANK
                EvalState.M -> if (char == 'u') EvalState.U else EvalState.BLANK
                EvalState.U -> if (char == 'l') EvalState.L else EvalState.BLANK
                EvalState.L -> if (char == '(') EvalState.OPEN else EvalState.BLANK
                EvalState.OPEN -> {
                    if (isDigit(char)) {
                        instruction.firstNumber += char
                        EvalState.DIGIT
                    } else {
                        EvalState.BLANK
                    }
                }

                EvalState.DIGIT -> {
                    when {
                        char == ')' -> if (instruction.hasComma) {
                            sum += instruction.getResult()
                            reset()
                        } else {
                            reset()
                        }

                        char == ',' -> if (!instruction.hasComma) {
                            EvalState.COMMA
                        } else {
                            reset()
                        }

                        isDigit(char) -> {
                            if (instruction.hasComma) {
                                if (instruction.secondNumber.length < 3) {
                                    instruction.secondNumber += char
                                    EvalState.DIGIT
                                }
                            } else {
                                if (instruction.firstNumber.length < 3) {
                                    instruction.firstNumber += char
                                    EvalState.DIGIT
                                }
                            }
                            reset()
                        }

                        else -> reset()
                    }
                }

                EvalState.COMMA -> if (isDigit(char)) {
                    instruction.hasComma = true
                    instruction.secondNumber += char
                    EvalState.DIGIT
                } else {
                    reset()
                }
            }


        }

    }

    input.forEach { readLine(it) }

    return sum
}


class Instruction {
    var currentState: EvalState = EvalState.BLANK
    var hasComma = false
    var firstNumber = ""
    var secondNumber = ""


    fun getResult(): Int = firstNumber.toInt() * secondNumber.toInt()
}

sealed class InstructionState {
    fun isDigit(char: Char): Boolean = char in '0'..'9'

    abstract fun eval(char: Char): InstructionState

    // NONE, M, U, L, OPEN, DIGIT, COMMA, CLOSE
    data object None : InstructionState() {
        override fun eval(char: Char): InstructionState {
            return if (char == 'm') M else None
        }
    }

    data object M : InstructionState() {
        override fun eval(char: Char): InstructionState {
            return if (char == 'u') U else None
        }
    }

    data object U : InstructionState() {
        override fun eval(char: Char): InstructionState {
            return if (char == 'l') L else None
        }
    }

    data object L : InstructionState() {
        override fun eval(char: Char): InstructionState {
            return if (char == '(') Open else None
        }
    }

    data object Open : InstructionState() {
        override fun eval(char: Char): InstructionState {
            return if (isDigit(char)) Number(char.toString(), false) else None
        }
    }

    class Number(var currentNumber: String, hasComma: Boolean) : InstructionState() {
        override fun eval(char: Char): InstructionState {
            if (isDigit(char)) {
                if (currentNumber.length != 3) {
                    currentNumber += char
                    return this
                }
                return None
            }

            return if (!hasComma) {
                if (char == ',') {
                    Comma
                } else None
            } else {
                // TODO finish
                if (char == ')') None else None
            }
        }
    }

    data object Comma : InstructionState() {
        override fun eval(char: Char): InstructionState {
            hasComma = true
            return if (isDigit(char)) Number(char.toString()) else None
        }
    }

}
