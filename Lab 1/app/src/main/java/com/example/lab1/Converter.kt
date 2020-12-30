package com.example.lab1
import kotlin.math.round
import java.math.RoundingMode
import java.text.DecimalFormat

class Converter {
    var categoryId: Int = 0
    var inputId: Int = 0
    var outputId: Int = 0

    var categories = listOf("Длина", "Масса", "Время")

    var unitsList = listOf(
        listOf("Метр", "См", "Км", "Дюйм"),
        listOf("Кг", "Грамм", "Тонна", "Фунт"),
        listOf("Час", "Минута", "Сек", "Сутки")
    )
    var convertList = listOf(
        listOf<Double>(1.0, 1000.0, 0.001, 2.205),
        listOf<Double>(1.0, 100.0, 0.001, 39.3701),
        listOf<Double>(1.0, 60.0, 3600.0, (1.0/24.0))
    )

    fun getUnitList(): List<String> {
        return unitsList[categoryId]
    }

    fun convert(inputText: String): String {
        if (inputText.isEmpty())
            return ""
        val df = DecimalFormat("#.######")
        df.roundingMode = RoundingMode.CEILING

        val input = inputText.toDouble()
        val res = input / convertList[categoryId][inputId]* convertList[categoryId][outputId]
        val output =  df.format(res)
        return output.replaceFirst(",", ".", true)
    }
}