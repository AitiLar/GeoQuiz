package com.zenpath.dev.geoquiz

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlin.math.round

class QuizViewModel: ViewModel() {

    companion object{
        private const val TAG = "QuizViewModel"
    }

    var currentIndex = 0
    var isCheater = false

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var answerList: MutableList<Boolean?> = MutableList(questionBank.size){null}
    private var cheatListQuestion: MutableList<Int?> = mutableListOf()

    val getAnswerList : MutableList<Boolean?> get() = answerList
    val currentQuestionAnswer: Boolean get() = questionBank[currentIndex].answer
    val currentQuestionText: Int get() = questionBank[currentIndex].textResId
    val getCheatList: MutableList<Int?> get() = cheatListQuestion


    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun backToNext() {
        currentIndex = (currentIndex - 1) % questionBank.size
    }

    fun updateAnswerList(userAnswer: Boolean){
        answerList[currentIndex] = userAnswer
    }

    fun updateAnswersHistory(map: Map<Int, Boolean>){
        for (i in map){
            answerList[i.key] = i.value
        }
    }

    fun updateCheatHistory(numberQuestion: Int){
        cheatListQuestion.add(numberQuestion)
    }

    fun refreshIsCheater(){
        isCheater = cheatListQuestion.contains(currentIndex)
    }

    fun showResult(): Int{
        var r = 0
        for (i in answerList.indices){
            if(answerList[i] == questionBank[i].answer){
                r += 1
            }
        }

        var result = (r.toDouble() / questionBank.size) * 100
        val fResult = round(result).toInt()
        return fResult;
    }

    fun isFinal(): Boolean{
        if (currentIndex == questionBank.size-1){
            answerList.clear()
            return true
        }
        return false
    }
}