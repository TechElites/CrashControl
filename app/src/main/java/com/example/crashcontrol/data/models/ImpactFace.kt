package com.example.crashcontrol.data.models

import com.example.crashcontrol.R

data class ImpactFace(
    val left: Int = R.string.left,
    val right: Int = R.string.right,
    val up: Int = R.string.up,
    val down: Int = R.string.down,
    val front: Int = R.string.front,
    val back: Int = R.string.back
) {
    fun toList(): List<Int> = listOf(left, right, up, down, front, back)
}