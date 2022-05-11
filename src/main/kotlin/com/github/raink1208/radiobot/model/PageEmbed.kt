package com.github.raink1208.radiobot.model

import net.dv8tion.jda.api.entities.MessageEmbed.Field

class PageEmbed private constructor(val title: String, fields: List<Field>, separateCount: Int){
    private val chunkedFields = fields.chunked(separateCount)

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