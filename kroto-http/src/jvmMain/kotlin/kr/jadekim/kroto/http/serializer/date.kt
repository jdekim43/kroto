package kr.jadekim.kroto.http.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

val DateAsLongModule = SerializersModule {
    contextual(DateAsLongSerializer)
    contextual(LocalDateTimeAsLongSerializer)
}

object DateAsLongSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Date = Date(decoder.decodeLong())
}

object LocalDateTimeAsLongSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val value = value.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        encoder.encodeLong(value)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val value = decoder.decodeLong()

        return LocalDateTime.ofEpochSecond(
            value / 1000,
            (value % 1000).toInt() * 1000000,
            ZoneOffset.UTC
        )
    }
}