package com.example.calculator

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var currentInput = "" // То, что сейчас вводит пользователь

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Настройка отступов под системные панели (чтобы дизайн не прилипал к краям)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val base = (16 * resources.displayMetrics.density).toInt()
            v.setPadding(
                systemBars.left + base,
                systemBars.top + base,
                systemBars.right + base,
                systemBars.bottom + base
            )
            insets
        }

        tvDisplay = findViewById(R.id.tvDisplay)

        // --- НАХОДИМ ВСЕ КНОПКИ ---
        val btnClear = findViewById<MaterialButton>(R.id.btnClear)
        val btnDivide = findViewById<MaterialButton>(R.id.btnDivide)
        val btnMultiply = findViewById<MaterialButton>(R.id.btnMultiply)
        val btnMinus = findViewById<MaterialButton>(R.id.btnMinus)
        val btnPlus = findViewById<MaterialButton>(R.id.btnPlus)
        val btnEquals = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnEquals)
        val btnDot = findViewById<MaterialButton>(R.id.btnDot)
        val btnBackspace = findViewById<MaterialButton>(R.id.btnBackspace)

        // Цифры
        val btn0 = findViewById<MaterialButton>(R.id.btn0)
        val btn00 = findViewById<MaterialButton>(R.id.btn00)
        val btn1 = findViewById<MaterialButton>(R.id.btn1)
        val btn2 = findViewById<MaterialButton>(R.id.btn2)
        val btn3 = findViewById<MaterialButton>(R.id.btn3)
        val btn4 = findViewById<MaterialButton>(R.id.btn4)
        val btn5 = findViewById<MaterialButton>(R.id.btn5)
        val btn6 = findViewById<MaterialButton>(R.id.btn6)
        val btn7 = findViewById<MaterialButton>(R.id.btn7)
        val btn8 = findViewById<MaterialButton>(R.id.btn8)
        val btn9 = findViewById<MaterialButton>(R.id.btn9)


        // --- ЛОГИКА НАЖАТИЙ ---

        // Ввод цифр и точки
        val numberButtons = listOf(btn0, btn00, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnDot)
        for (button in numberButtons) {
            button.setOnClickListener {
                currentInput += button.text.toString()
                tvDisplay.text = currentInput
            }
        }

        // Ввод математических знаков (+, -, *, /)
        val operatorButtons = listOf(btnPlus, btnMinus, btnMultiply, btnDivide)
        for (button in operatorButtons) {
            button.setOnClickListener {
                // Если в строке уже есть знак (кроме первого символа), не даем ввести второй
                if (currentInput.isNotEmpty() && !isOperator(currentInput.last()) && !isOperator(currentInput.first())) {
                    // Заменяем умножение и деление на символы, понятные для вычисления в коде
                    val op = when(button.text.toString()) {
                        "×" -> "*"
                        "÷" -> "/"
                        "−" -> "-"
                        else -> button.text.toString()
                    }
                    currentInput += op
                    tvDisplay.text = currentInput
                }
            }
        }

        // Кнопка "="
        btnEquals.setOnClickListener {
            try {
                val result = calculate(currentInput)
                tvDisplay.text = result
                currentInput = result // Сохраняем результат для дальнейших вычислений
            } catch (e: Exception) {
                tvDisplay.text = "Ошибка"
                currentInput = ""
            }
        }

        // Очистка (C)
        btnClear.setOnClickListener {
            currentInput = ""
            tvDisplay.text = "0"
        }

        // Стирание последнего символа (⌫)
        btnBackspace.setOnClickListener {
            if (currentInput.isNotEmpty()) {
                currentInput = currentInput.dropLast(1)
                if (currentInput.isEmpty()) {
                    tvDisplay.text = "0"
                } else {
                    tvDisplay.text = currentInput
                }
            }
        }
    }

    // Проверка, является ли символ оператором
    private fun isOperator(char: Char): Boolean {
        return char == '+' || char == '-' || char == '*' || char == '/'
    }

    // Самый простой парсер математического выражения (слева направо, без приоритета скобок)
    // Для полноценного калькулятора лучше использовать библиотеки, но для начала сойдет
    private fun calculate(expression: String): String {
        if (expression.isEmpty()) return "0"

        var result = 0.0
        var currentNumber = ""
        var currentOperator = '+' // По умолчанию первое число складываем

        for (i in expression.indices) {
            val char = expression[i]

            if (char.isDigit() || char == '.') {
                currentNumber += char
            }

            // Если встретили оператор ИЛИ дошли до конца строки
            if (isOperator(char) || i == expression.length - 1) {
                if (currentNumber.isNotEmpty()) {
                    val number = currentNumber.toDouble()
                    when (currentOperator) {
                        '+' -> result += number
                        '-' -> result -= number
                        '*' -> result *= number
                        '/' -> {
                            if (number == 0.0) return "Ошибка"
                            result /= number
                        }
                    }
                    currentNumber = ""
                }
                currentOperator = char
            }
        }

        // Красиво форматируем результат (убираем .0 если число целое)
        return if (result % 1.0 == 0.0) {
            result.toInt().toString()
        } else {
            String.format("%.2f", result).replace(",", ".").trimEnd('0').trimEnd('.')
        }
    }
}
