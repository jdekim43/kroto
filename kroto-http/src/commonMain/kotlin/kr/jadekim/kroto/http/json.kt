package kr.jadekim.kroto.http

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

var KrotoHttpJson = Json {
    encodeDefaults = true

    serializersModule = DEFAULT_SERIALIZERS_MODULE
}

internal expect val DEFAULT_SERIALIZERS_MODULE: SerializersModule