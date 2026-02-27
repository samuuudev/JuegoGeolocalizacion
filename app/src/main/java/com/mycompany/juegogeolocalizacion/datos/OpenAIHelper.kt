package com.mycompany.juegogeolocalizacion.datos

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Clase auxiliar para obtener pistas de OpenAI
 * Esta es una versión simplificada que usa HttpURLConnection en lugar de OkHttp
 */
object OpenAIHelper {
    private val TAG = "OpenAIHelper"
    private const val API_KEY = "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" // TODO: Cambiar por tu API key real
    private const val API_URL = "https://api.openai.com/v1/chat/completions"
    private const val MODEL = "gpt-3.5-turbo"

    init {
        Log.d(TAG, "OpenAIHelper inicializado")
    }

    /**
     * Obtiene pistas de IA sobre un lugar usando texto y descripción
     * Ejecuta en thread separado para no bloquear UI
     * @param lugarReferencia: Lugar de referencia
     * @param callback: Callback para devolver las pistas o error
     */
    fun obtenerPistasTexto(
        lugarReferencia: Lugar,
        callback: (AIPistas?, String?) -> Unit
    ) {
        Thread {
            try {
                Log.d(TAG, "=== INICIO CONSULTA OPENAI ===")
                Log.d(TAG, "Solicitando pistas a OpenAI para: ${lugarReferencia.nombre}")
                Log.d(TAG, "Lugar - Nombre: ${lugarReferencia.nombre}, Ciudad: ${lugarReferencia.ciudad}")

                if (API_KEY == "sk-proj-YOUR_API_KEY_HERE") {
                    Log.w(TAG, "API Key no configurada. Usando pistas locales.")
                    callback(generarPistasLocales(lugarReferencia), null)
                    return@Thread
                }

                Log.d(TAG, "API Key detectada (primeros 20 chars): ${API_KEY.take(20)}...")

                val prompt = construirPrompt(lugarReferencia)
                Log.d(TAG, "Prompt construido (longitud: ${prompt.length} chars)")
                Log.d(TAG, "Prompt preview: ${prompt.take(100)}...")

                val requestBody = construirRequestBody(prompt)
                Log.d(TAG, "Request body construido (longitud: ${requestBody.length} chars)")
                Log.d(TAG, "Request body completo:\n$requestBody")

                val connection = URL(API_URL).openConnection() as HttpURLConnection
                Log.d(TAG, "Conexión abierta a: $API_URL")

                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $API_KEY")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                Log.d(TAG, "Headers configurados:")
                Log.d(TAG, "  - Method: POST")
                Log.d(TAG, "  - Authorization: Bearer [API_KEY]")
                Log.d(TAG, "  - Content-Type: application/json")
                Log.d(TAG, "  - doOutput: true")

                Log.d(TAG, "Escribiendo request body en output stream...")
                val bytes = requestBody.toByteArray(Charsets.UTF_8)
                Log.d(TAG, "Bytes a escribir: ${bytes.size}")

                connection.outputStream.write(bytes)
                connection.outputStream.flush()
                Log.d(TAG, "Request body enviado correctamente")

                val responseCode = connection.responseCode
                Log.d(TAG, "🔴 Código de respuesta recibido: $responseCode")

                // Leer respuesta completa incluso si es error
                val inputStream = if (responseCode == 200 || responseCode == 400 || responseCode == 401) {
                    if (responseCode == 200) connection.inputStream else connection.errorStream
                } else {
                    Log.e(TAG, "Error: Código inesperado $responseCode")
                    callback(null, "Error en respuesta de OpenAI (código: $responseCode)")
                    return@Thread
                }

                val response = BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }

                Log.d(TAG, "Respuesta recibida (longitud: ${response.length} chars)")
                Log.d(TAG, "Respuesta completa:\n$response")

                if (responseCode == 400) {
                    Log.e(TAG, "⚠️ ERROR 400 - Bad Request")
                    Log.e(TAG, "Analizando respuesta de error:")
                    try {
                        val errorJson = JSONObject(response)
                        val error = errorJson.optJSONObject("error")
                        if (error != null) {
                            val errorMsg = error.optString("message", "No message")
                            val errorType = error.optString("type", "No type")
                            Log.e(TAG, "Error type: $errorType")
                            Log.e(TAG, "Error message: $errorMsg")
                            callback(null, "Error de OpenAI: $errorMsg")
                        } else {
                            Log.e(TAG, "No se encontró objeto 'error' en respuesta")
                            callback(null, "Error 400: Solicitud malformada")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parseando respuesta de error: ${e.message}")
                        callback(null, "Error 400: ${response.take(100)}")
                    }
                    return@Thread
                }

                if (responseCode == 401) {
                    Log.e(TAG, "⚠️ ERROR 401 - API Key inválida o expirada")
                    callback(null, "Error 401: API Key inválida")
                    return@Thread
                }

                Log.d(TAG, "Parseando respuesta exitosa...")
                val pistas = parsearRespuestaJSON(response)
                if (pistas != null) {
                    Log.d(TAG, "✅ Pistas parseadas exitosamente")
                    Log.d(TAG, "Número de pistas: ${pistas.pistas.size}")
                    Log.d(TAG, "Clima: ${pistas.clima}")
                    Log.d(TAG, "Región: ${pistas.region}")
                    callback(pistas, null)
                } else {
                    Log.e(TAG, "❌ No se pudieron parsear las pistas")
                    callback(null, "Error parseando respuesta de OpenAI")
                }

                connection.disconnect()
                Log.d(TAG, "Conexión cerrada")
                Log.d(TAG, "=== FIN CONSULTA OPENAI ===\n")

            } catch (e: Exception) {
                Log.e(TAG, "❌ EXCEPCIÓN en obtenerPistasTexto: ${e.message}")
                Log.e(TAG, "Stack trace:", e)
                callback(null, "Error: ${e.message}")
            }
        }.start()
    }

    private fun construirPrompt(lugar: Lugar): String {
        return """Eres un experto en geolocalización y acertijos geográficos. 
Dado el siguiente lugar, proporciona pistas indirectas para ayudar a adivinar su ubicación
SIN REVELAR LA UBICACIÓN EXACTA.

Lugar: ${lugar.nombre}
Ciudad: ${lugar.ciudad}
Descripción: ${lugar.descripcion}

Proporciona SOLO la respuesta en JSON (sin markdown) con esta estructura exacta:
{
    "pistas": ["pista1", "pista2", "pista3", "pista4", "pista5"],
    "clima": "descripción breve del clima",
    "region": "región aproximada (continente o zona general, NO ciudad)",
    "idiomas": ["idioma1", "idioma2"],
    "puntosClave": ["característica1", "característica2"],
    "erroresEvitar": ["error común 1", "error común 2"]
}

IMPORTANTE:
- Las pistas deben ser vagas y útiles
- NO reveles la ciudad exacta ni coordenadas
- NO menciones el nombre del lugar
- Sé creativo pero impreciso
- Devuelve SOLO JSON válido, sin explicación adicional""".trimIndent()
    }

    private fun construirRequestBody(prompt: String): String {
        Log.d(TAG, "Construyendo request body con JSONObject")

        try {
            // Usar JSONObject para construcción segura de JSON
            val messagesArray = org.json.JSONArray()
            val messageObj = JSONObject()
            messageObj.put("role", "user")
            messageObj.put("content", prompt)
            messagesArray.put(messageObj)

            val requestJson = JSONObject()
            requestJson.put("model", MODEL)
            requestJson.put("messages", messagesArray)
            requestJson.put("max_tokens", 500)
            requestJson.put("temperature", 0.7)

            val requestBody = requestJson.toString()
            Log.d(TAG, "JSONObject convertido a String exitosamente")
            Log.d(TAG, "Request body size: ${requestBody.length} bytes")

            return requestBody
        } catch (e: Exception) {
            Log.e(TAG, "Error construyendo JSONObject: ${e.message}")
            // Fallback a construcción manual
            return """
            {
                "model": "$MODEL",
                "messages": [
                    {
                        "role": "user",
                        "content": "${escapeJson(prompt)}"
                    }
                ],
                "max_tokens": 500,
                "temperature": 0.7
            }
        """.trimIndent()
        }
    }

    private fun escapeJson(str: String): String {
        return str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun parsearRespuestaJSON(response: String): AIPistas? {
        return try {
            Log.d(TAG, "=== INICIO PARSEO RESPUESTA ===")
            Log.d(TAG, "Respuesta a parsear (primeros 300 chars):\n${response.take(300)}")

            val jsonResponse = JSONObject(response)
            Log.d(TAG, "JSONObject creado exitosamente")

            val choices = jsonResponse.getJSONArray("choices")
            Log.d(TAG, "Array 'choices' obtenido. Longitud: ${choices.length()}")

            if (choices.length() == 0) {
                Log.e(TAG, "❌ No hay elementos en choices")
                return null
            }

            val firstChoice = choices.getJSONObject(0)
            Log.d(TAG, "Primer choice obtenido")

            val message = firstChoice.getJSONObject("message")
            Log.d(TAG, "Objeto 'message' obtenido")

            val content = message.getString("content")
            Log.d(TAG, "Content extraído (longitud: ${content.length} chars)")
            Log.d(TAG, "Content preview (primeros 200 chars):\n${content.take(200)}")

            // Limpiar el contenido de markdown si existe
            val jsonString = when {
                content.contains("```json") -> {
                    Log.d(TAG, "Detectado markdown ```json")
                    val result = content.substringAfter("```json").substringBefore("```").trim()
                    Log.d(TAG, "Markdown removido. Longitud JSON: ${result.length}")
                    result
                }
                content.contains("```") -> {
                    Log.d(TAG, "Detectado markdown ```")
                    val result = content.substringAfter("```").substringBefore("```").trim()
                    Log.d(TAG, "Markdown removido. Longitud JSON: ${result.length}")
                    result
                }
                else -> {
                    Log.d(TAG, "No se detectó markdown")
                    content.trim()
                }
            }

            Log.d(TAG, "JSON string final (primeros 300 chars):\n${jsonString.take(300)}")

            val json = JSONObject(jsonString)
            Log.d(TAG, "✅ JSONObject parseado correctamente")

            val pistas = mutableListOf<String>()
            val pistasArray = json.getJSONArray("pistas")
            Log.d(TAG, "Array 'pistas' obtenido. Elementos: ${pistasArray.length()}")
            for (i in 0 until pistasArray.length()) {
                val pista = pistasArray.getString(i)
                pistas.add(pista)
                Log.d(TAG, "  Pista[$i]: $pista")
            }

            val idiomas = mutableListOf<String>()
            val idiomasArray = json.getJSONArray("idiomas")
            Log.d(TAG, "Array 'idiomas' obtenido. Elementos: ${idiomasArray.length()}")
            for (i in 0 until idiomasArray.length()) {
                idiomas.add(idiomasArray.getString(i))
                Log.d(TAG, "  Idioma[$i]: ${idiomasArray.getString(i)}")
            }

            val puntosClave = mutableListOf<String>()
            val puntosArray = json.getJSONArray("puntosClave")
            Log.d(TAG, "Array 'puntosClave' obtenido. Elementos: ${puntosArray.length()}")
            for (i in 0 until puntosArray.length()) {
                puntosClave.add(puntosArray.getString(i))
                Log.d(TAG, "  Punto[$i]: ${puntosArray.getString(i)}")
            }

            val erroresEvitar = mutableListOf<String>()
            val erroresArray = json.getJSONArray("erroresEvitar")
            Log.d(TAG, "Array 'erroresEvitar' obtenido. Elementos: ${erroresArray.length()}")
            for (i in 0 until erroresArray.length()) {
                erroresEvitar.add(erroresArray.getString(i))
                Log.d(TAG, "  Error[$i]: ${erroresArray.getString(i)}")
            }

            val clima = json.getString("clima")
            Log.d(TAG, "Campo 'clima' obtenido: $clima")

            val region = json.getString("region")
            Log.d(TAG, "Campo 'region' obtenido: $region")

            val resultado = AIPistas(
                pistas = pistas,
                clima = clima,
                region = region,
                idiomas = idiomas,
                puntosClave = puntosClave,
                erroresEvitar = erroresEvitar
            )

            Log.d(TAG, "✅ AIPistas creado exitosamente")
            Log.d(TAG, "=== FIN PARSEO RESPUESTA ===\n")

            resultado
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR parseando JSON: ${e.message}")
            Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Stack trace:", e)
            null
        }
    }

    /**
     * Genera pistas locales cuando no hay API key disponible
     * Proporciona pistas genéricas basadas en la descripción del lugar
     */
    private fun generarPistasLocales(lugar: Lugar): AIPistas {
        Log.d(TAG, "Generando pistas locales para ${lugar.nombre}")

        val palabrasClave = lugar.descripcion.lowercase().split(" ")

        val pistas = listOf(
            "Este es un lugar emblemático de importancia histórica o arquitectónica",
            "Se puede identificar por sus características visuales únicas",
            "Es un destino turístico popular que atrae a visitantes de todo el mundo",
            "La descripción proporciona pistas sobre su naturaleza y función",
            "Observa los detalles arquitectónicos y la ubicación geográfica"
        )

        val clima = if (palabrasClave.contains("tropical")) {
            "Tropical o subtropical"
        } else if (palabrasClave.contains("árido")) {
            "Árido o desértico"
        } else if (palabrasClave.contains("templado")) {
            "Templado o moderado"
        } else {
            "Variable según la región"
        }

        val region = "Europa, América, Asia, África u Oceanía"

        return AIPistas(
            pistas = pistas,
            clima = clima,
            region = region,
            idiomas = listOf("Varios posibles"),
            puntosClave = listOf(lugar.nombre, lugar.ciudad),
            erroresEvitar = listOf(
                "Confundir con lugares similares",
                "Asumir ubicación por la arquitectura únicamente"
            )
        )
    }
}

// Modelos de datos
data class AIPistas(
    val pistas: List<String>,
    val clima: String,
    val region: String,
    val idiomas: List<String>,
    val puntosClave: List<String>,
    val erroresEvitar: List<String>
)
