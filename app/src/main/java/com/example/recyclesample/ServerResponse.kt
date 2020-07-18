package com.example.recyclesample

data class ServerResponse (val status : String, val scores : MutableList<Score>, val details: MutableList<Detail>)