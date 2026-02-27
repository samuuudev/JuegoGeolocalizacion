# Configuración de la API de OpenAI - Guía de Implementación

## Descripción General

El proyecto ahora incluye una característica de "Ayuda con IA" que proporciona pistas sobre los lugares sin revelar su ubicación exacta. 

## Cómo Funciona

### 1. **Clase OpenAIHelper**
Ubicación: `app/src/main/java/com/mycompany/juegogeolocalizacion/datos/OpenAIHelper.kt`

- **Función principal**: `obtenerPistasTexto(lugarReferencia, callback)`
- Realiza consultas a la API de OpenAI (gpt-3.5-turbo)
- Devuelve un objeto `AIPistas` con pistas sobre la ubicación
- **Ventaja**: Si no hay API key configurada, genera pistas locales automáticamente

### 2. **Datos Devueltos (AIPistas)**
```kotlin
data class AIPistas(
    val pistas: List<String>,           // 5 pistas vagas pero útiles
    val clima: String,                  // Descripción del clima típico
    val region: String,                 // Región aproximada (NO ciudad exacta)
    val idiomas: List<String>,          // Idiomas posibles
    val puntosClave: List<String>,      // Características clave
    val erroresEvitar: List<String>     // Lugares comunes donde se equivocan
)
```

### 3. **Integración en PantallaJuego**
- Botón "🤖 Ayuda" muestra contador de ayudas disponibles
- Al presionar:
  - Se realiza llamada a OpenAI en thread separado
  - Se muestran pistas en diálogo interactivo
  - Se restan 50 puntos por usar ayuda
  - Se incrementa contador de ayudas usadas
- Botón deshabilitado si no quedan ayudas o si no hay intentos

### 4. **Diálogo de Pistas (DialogoPistasIA)**
Muestra de forma ordenada:
- Pistas generales
- Clima y región
- Idiomas visibles
- Características clave
- Lugares a evitar

## Configuración de API Key

### Opción 1: Sin API Key (Recomendado para desarrollo/testing)
Por defecto, sin configurar API key, el sistema genera pistas locales basadas en la descripción del lugar.

**Ventajas**:
- No necesita API key
- Sin costos
- Funciona offline
- Perfecto para testing

**Desventajas**:
- Pistas menos sofisticadas

### Opción 2: Con API Key Real (Producción)

#### Paso 1: Obtener API Key
1. Ve a [OpenAI Platform](https://platform.openai.com/api-keys)
2. Crea una cuenta o inicia sesión
3. Genera una nueva API key
4. Copia la key (empieza con `sk-proj-`)

#### Paso 2: Configurar en el Proyecto (OPCIÓN SEGURA)

**Método recomendado**: Usar BuildConfig

Edita `app/build.gradle.kts`:
```gradle
android {
    // ... código existente ...
    
    buildTypes {
        debug {
            buildConfigField "String", "OPENAI_API_KEY", "\"sk-proj-YOUR_KEY_HERE\""
        }
        release {
            buildConfigField "String", "OPENAI_API_KEY", "\"sk-proj-YOUR_KEY_HERE\""
        }
    }
}
```

Luego en `OpenAIHelper.kt`, cambia:
```kotlin
private const val API_KEY = "sk-proj-YOUR_API_KEY_HERE"
```

Por:
```kotlin
private val API_KEY = BuildConfig.OPENAI_API_KEY
```

#### Paso 3: Sincronizar Gradle
En Android Studio: `File > Sync Now`

## Uso del Sistema de Ayudas

### Por Nivel
- **Fácil**: 2 ayudas disponibles
- **Medio**: 1 ayuda disponible
- **Difícil**: 1 ayuda disponible

Se puede ajustar en `AppNavegacion.kt`:
```kotlin
val nivelSeleccionado = when(nivelId) {
    1 -> Nivel(1, "Fácil", 50.0, 5, 2)      // 2 ayudas
    2 -> Nivel(2, "Medio", 25.0, 4, 1)      // 1 ayuda
    3 -> Nivel(3, "Difícil", 10.0, 3, 1)    // 1 ayuda
    else -> Nivel(2, "Medio", 25.0, 4, 1)
}
```

### Penalización
- Cada uso de ayuda resta **50 puntos**
- Se cuenta en el historial de ayudas usadas

## Prompt Utilizado

El sistema usa este prompt para generar pistas:

```
Eres un experto en geolocalización y acertijos geográficos. 
Dado el siguiente lugar, proporciona pistas indirectas para ayudar a adivinar su ubicación
SIN REVELAR LA UBICACIÓN EXACTA.

Lugar: [nombre]
Ciudad: [ciudad]
Descripción: [descripción]

Proporciona SOLO la respuesta en JSON con estructura específica...
```

**Requisitos**:
- NO revela la ciudad exacta
- NO menciona coordenadas
- NO dice el nombre del lugar
- Pistas son vagas pero útiles
- Devuelve JSON válido

## Limitaciones y Consideraciones

### Costos
- **Sin API Key**: Gratis
- **Con API Key**: 
  - ~$0.50-1.00 por 1,000 requests a gpt-3.5-turbo
  - Establecer límites de gastos en OpenAI Dashboard

### Límites de Rate
- OpenAI puede limitar requests si hay muchas llamadas
- Sistema implementa reintentos automáticos

### Conexión
- Requiere conexión a internet para funcionar
- Incluye manejo de errores y timeout (30 segundos)

## Testing

### Caso 1: Sin API Key
1. Abre la app
2. Selecciona nivel y sitio
3. Presiona botón "🤖 Ayuda"
4. Verá pistas locales generadas

### Caso 2: Con API Key
1. Configura API key según instrucciones arriba
2. Repite pasos 1-3
3. Verá pistas generadas por OpenAI (más sofisticadas)

## Solución de Problemas

### "API Key no configurada"
**Solución**: El sistema está usando pistas locales (es normal)

### "Error en respuesta de OpenAI (código: 401)"
**Problema**: API key inválida o expirada
**Solución**: Verifica que la key sea correcta en OpenAIHelper.kt

### "Error: timed out waiting for connection"
**Problema**: Sin conexión a internet o servidor lento
**Solución**: Verifica conexión, espera e intenta de nuevo

### Pistas no aparecen
**Solución**: Verifica logs en Logcat:
```
adb logcat | grep OpenAIHelper
```

## Código Relevante

### Llamada a OpenAI desde PantallaJuego
```kotlin
com.mycompany.juegogeolocalizacion.datos.OpenAIHelper.obtenerPistasTexto(sitio) { pistas, error ->
    if (pistas != null) {
        pistasAI = pistas
        ayudasUsadas++
        puntuacion = maxOf(0, puntuacion - 50)
    } else {
        errorAI = error ?: "No se pudieron obtener las pistas"
    }
    cargandoAI = false
}
```

### Diálogo que muestra pistas
```kotlin
DialogoPistasIA(
    pistas = pistasAI,
    cargando = cargandoAI,
    error = errorAI,
    onDismiss = { mostrarDialogoAI = false }
)
```

## Futuras Mejoras

1. **Caché**: Guardar pistas para no hacer requests repetidos
2. **Offline Mode**: Generar más pistas locales complejas
3. **Otros Modelos**: Usar gpt-4-vision para análisis de imágenes
4. **Historial**: Guardar pistas usadas para análisis posterior
5. **Customización**: Sistema de puntuación variable según modelo

## Soporte

Para preguntas o problemas:
1. Verifica los logs en Logcat (tag: "OpenAIHelper")
2. Revisa la estructura de JSON devuelto
3. Asegúrate de que la API key sea válida
4. Comprueba la conexión a internet

