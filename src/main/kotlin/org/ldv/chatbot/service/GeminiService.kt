
package org.ldv.chatbot.service

import org.ldv.chatbot.dto.gemini.GeminiCandidate
import org.ldv.chatbot.dto.gemini.GeminiChatRequest
import org.ldv.chatbot.dto.gemini.GeminiChatResponse
import org.ldv.chatbot.dto.gemini.GeminiContent
import org.ldv.chatbot.dto.gemini.GeminiPart
import org.ldv.chatbot.model.entity.ModeleIA
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class GeminiService(
    private val restClientBuilder: RestClient.Builder,

    @Value("\${gemini.api.key}")
    private val apiKey: String
) : IAService {

    override fun getFournisseur(): String {
        return "Gemini"
    }

    override fun envoyerPrompt(
        prompt: String,
        modele: ModeleIA
    ): String {

        // Création de la requête
        val requete = GeminiChatRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            )
        )

        // Création du client HTTP
        val client = restClientBuilder.build()

        // Envoi de la requête à Gemini
        val reponse = client.post()
            .uri(modele.urlAPI!!)
            .header("x-goog-api-key", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requete)
            .retrieve()
            .body(GeminiChatResponse::class.java)

        // Récupération de la réponse
        return reponse?.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.joinToString("") { it.text }
            ?: "Aucune réponse reçue de Gemini."
    }
}
