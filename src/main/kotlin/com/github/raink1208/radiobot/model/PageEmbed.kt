package com.github.raink1208.radiobot.model

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.Field

class PageEmbed private constructor(val title: String, fields: List<Field>, separateCount: Int){
    private val chunkedFields = fields.chunked(separateCount)
    var page: Int = 0

    fun getNext(): MessageEmbed {
        if (page < chunkedFields.size - 1) page++
        return getEmbed(page)
    }

    fun getPrev(): MessageEmbed {
        if (page > 0) page--
        return getEmbed(page)
    }

    fun getEmbed(page: Int = 0): MessageEmbed {
        val fields = getFields(page)
        val builder = EmbedBuilder()
        builder.setTitle(title)
        for (field in fields) {
            builder.addField(field)
        }
        return builder.build()
    }

    fun getFields(page: Int): List<Field> {
        return chunkedFields[page]
    }

    class PageEmbedBuilder {
        var title = ""
        var separateCount = 10
        private val fields = mutableListOf<Field>()

        fun addField(field: Field) {
            fields.add(field)
        }

        fun build(): PageEmbed {
            return PageEmbed(title, fields, separateCount)
        }
    }
}