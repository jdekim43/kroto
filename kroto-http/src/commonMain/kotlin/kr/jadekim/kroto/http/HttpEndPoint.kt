package kr.jadekim.kroto.http

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer

enum class HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE
}

interface HttpEndPoint<PathParameter : Any, QueryParameter : Any, RequestBody : Any, ResponseBody : Any> {
    val pathParameterSerializer: KSerializer<PathParameter>
    val queryParameterSerializer: KSerializer<QueryParameter>
    val requestBodySerializer: KSerializer<RequestBody>
    val responseBodySerializer: KSerializer<ResponseBody>

    val method: HttpMethod
    val pathTemplate: String

    fun getPath(parameter: PathParameter?): String = parameter?.let { pathTemplate.fillVariables(it) } ?: pathTemplate

    private fun String.fillVariables(
        value: PathParameter
    ) = buildString(length + 32) {
        val data = KrotoHttpJson.encodeToJsonElement(pathParameterSerializer, value).jsonObject
        var opened = false
        var openIndex: Int? = null
        var appendIndex = 0

        for ((i, c) in toCharArray().withIndex()) {
            if (c == '{') {
                opened = true
                openIndex = i + 1
                append(this@fillVariables.substring(appendIndex, i - 1))
                appendIndex = i - 1
                continue
            }

            if (c == '}') {
                if (opened && openIndex != null) {
                    val key = this@fillVariables.substring(openIndex, i - 1)
                    val variableValue = data[key]?.jsonPrimitive?.content
                        ?: throw IllegalArgumentException("Not found $key from pathParameter")

                    append(variableValue)
                    appendIndex = i + 1

                    opened = false
                    openIndex = null
                }
                continue
            }
        }

        if (appendIndex < this@fillVariables.length) {
            append(this@fillVariables.substring(appendIndex, this@fillVariables.length))
        }
    }
}

inline fun <reified P : Any, reified Q : Any, reified Rq : Any, reified Rs : Any> http(
    method: HttpMethod,
    pathTemplate: String
) = object : HttpEndPoint<P, Q, Rq, Rs> {
    override val pathParameterSerializer: KSerializer<P> = serializer()
    override val queryParameterSerializer: KSerializer<Q> = serializer()
    override val requestBodySerializer: KSerializer<Rq> = serializer()
    override val responseBodySerializer: KSerializer<Rs> = serializer()

    override val method: HttpMethod = method
    override val pathTemplate: String = pathTemplate
}