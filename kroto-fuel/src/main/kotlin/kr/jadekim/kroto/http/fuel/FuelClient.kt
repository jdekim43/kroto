package kr.jadekim.kroto.http.fuel

import com.github.kittinunf.fuel.core.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kr.jadekim.kroto.http.HttpEndPoint
import kr.jadekim.kroto.http.HttpMethod
import kr.jadekim.kroto.http.KrotoHttpJson
import java.io.InputStream
import java.io.Reader

class FuelClient(
    val baseEndPoint: String,
    configure: FuelManager.() -> Unit = {}
) {

    private val fuelManager = FuelManager()
        .apply(configure)

    private val fuel = fuelManager as RequestFactory.Convenience

    private val HttpMethod.fuelType
        get() = when (this) {
            HttpMethod.GET -> Method.GET
            HttpMethod.POST -> Method.POST
            HttpMethod.PUT -> Method.PUT
            HttpMethod.PATCH -> Method.PATCH
            HttpMethod.DELETE -> Method.DELETE
            HttpMethod.HEAD -> Method.HEAD
            HttpMethod.OPTIONS -> Method.OPTIONS
            HttpMethod.TRACE -> Method.TRACE
        }

    suspend fun <P : Any, Q : Any, Rq : Any, Rs : Any> request(
        endPoint: HttpEndPoint<P, Q, Rq, Rs>,
        pathParameter: P? = null,
        query: Q? = null,
        body: Rq? = null
    ): Triple<Request, Response, Rs> {
        val queryParameter = query?.let {
            KrotoHttpJson.encodeToJsonElement(endPoint.queryParameterSerializer, it)
                .jsonObject.toList()
                .map { q -> q.first to q.second.jsonPrimitive.contentOrNull }
        }
        val requestBody = body?.let { KrotoHttpJson.encodeToString(endPoint.requestBodySerializer, it) }

        var request =
            fuel.request(endPoint.method.fuelType, baseEndPoint + endPoint.getPath(pathParameter), queryParameter)
                .header(Headers.CONTENT_TYPE, "application/json")

        if (requestBody != null) {
            request = request.body(requestBody)
        }

        return request.awaitResponse(Deserializable(endPoint.responseBodySerializer))
    }

    private class Deserializable<T : Any>(private val serializer: DeserializationStrategy<T>) :
        ResponseDeserializable<T> {
        override fun deserialize(content: String): T? = KrotoHttpJson.decodeFromString(serializer, content)
        override fun deserialize(reader: Reader): T? = deserialize(reader.readText())
        override fun deserialize(bytes: ByteArray): T? = deserialize(String(bytes))

        override fun deserialize(inputStream: InputStream): T? {
            inputStream.bufferedReader().use {
                return deserialize(it)
            }
        }
    }
}