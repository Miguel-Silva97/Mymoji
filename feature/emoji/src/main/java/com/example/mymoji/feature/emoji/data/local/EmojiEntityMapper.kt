package com.example.mymoji.feature.emoji.data.local

import com.example.mymoji.core.data.EmojiEntity
import com.example.mymoji.feature.emoji.domain.model.Emoji

fun EmojiEntity.toDomain(): Emoji = Emoji(name = name, url = url)

fun List<EmojiEntity>.toDomain(): List<Emoji> = map { it.toDomain() }

fun Emoji.toEntity(): EmojiEntity = EmojiEntity(name = name, url = url)

fun List<Emoji>.toEntity(): List<EmojiEntity> = map { it.toEntity() }
