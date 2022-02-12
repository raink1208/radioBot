package com.github.raink1208.radioBot.command

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CommandDescription(

    val name: String,

    val description: String = "",

    val aliases: Array<String>,

    val args: Int = 0,

    val attributes: Array<CommandAttribute> = []
)