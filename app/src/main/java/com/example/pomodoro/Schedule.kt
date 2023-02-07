package com.example.pomodoro

data class Schedule(val title: String, val workTime: Int, val breakTime: Int, val intervals: Int) {
    var id: Int =  0

    constructor(id: Int, title: String, workTime: Int, breakTime: Int, intervals: Int): this(title, workTime, breakTime, intervals) {
        this.id = id
    }
}