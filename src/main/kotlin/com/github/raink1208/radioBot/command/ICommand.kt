package com.github.raink1208.radioBot.command

import net.dv8tion.jda.api.entities.Message

interface ICommand {
    fun execute(message: Message, args: String)

    val description: CommandDescription?
        get() = javaClass.getAnnotation(CommandDescription::class.java)

    fun getAttributeValue(key: String): String? {
        if (!hasAttribute(key)) return null
        return description?.attributes!!.filter { it.key == key }[0].value
    }

    fun hasAttribute(key: String): Boolean {
        return description?.attributes?.any { it.key == key } ?: false
    }
}