package kr.jadekim.kroto.http

import kotlinx.serialization.KSerializer

interface KrotoConverter<Out> {

    fun convert(serializer: KSerializer<*>): KSerializer<Out>
}