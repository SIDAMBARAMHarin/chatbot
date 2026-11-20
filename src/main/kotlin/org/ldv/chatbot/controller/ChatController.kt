
package org.ldv.chatbot.controller

import org.ldv.chatbot.model.dao.ConversationDAO
import org.ldv.chatbot.model.dao.MessageDAO
import org.ldv.chatbot.model.dao.ModeleIADAO
import org.ldv.chatbot.model.entity.Conversation
import org.ldv.chatbot.model.entity.Message
import org.ldv.chatbot.model.entity.TypeMessage
import org.ldv.chatbot.service.IAServiceFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Controller
class ChatController(
    private val iaServiceFactory: IAServiceFactory,
    private val conversationDAO: ConversationDAO,
    private val messageDAO: MessageDAO,
    private val modeleIADAO: ModeleIADAO
) {

    // Page d'accueil
    @GetMapping("/")
    fun accueil(model: Model): String {

        model.addAttribute("conversation", null)
        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("messages", emptyList<Message>())

        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateModificationDesc()
        )

        return "utilisateur/chat"
    }
    // Page de connexion
    @GetMapping("/connexion")
    fun afficherConnexion(): String {
        return "auth/connexion"
    }
    @GetMapping("/inscription")
    fun afficherInscription(): String {
        return "auth/inscription"
    }

    // Création d'une nouvelle conversation
    @PostMapping("/conversations")
    fun creerConversation(
        @RequestParam prompt: String,
        @RequestParam idModele: Long
    ): String {

        // Récupération du modèle IA
        val modele = modeleIADAO.findById(idModele)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Modèle IA introuvable"
                )
            }

        // Création de la conversation
        val conversation = Conversation(
            titre = prompt.take(50),
            dateCreation = LocalDateTime.now(),
            dateModification = LocalDateTime.now(),
            modeleIA = modele
        )

        val conversationEnregistree =
            conversationDAO.save(conversation)

        // Enregistrement du message utilisateur
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversationEnregistree
        )

        messageDAO.save(messageUtilisateur)

        try {
            // Sélection du service IA
            val iaService = iaServiceFactory.getService(modele)

            // Appel de l'IA
            val reponse = iaService.envoyerPrompt(prompt, modele)

            // Enregistrement de la réponse
            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversationEnregistree
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l'appel au modèle ${modele.nom} : ${e.message}")
        }

        // Mise à jour de la date
        conversationEnregistree.dateModification = LocalDateTime.now()
        conversationDAO.save(conversationEnregistree)

        return "redirect:/conversations/${conversationEnregistree.id}"
    }

    // Affichage d'une conversation
    @GetMapping("/conversations/{id}")
    fun afficherConversation(
        @PathVariable id: Long,
        model: Model
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conversation introuvable"
                )
            }

        model.addAttribute(
            "conversations",
            conversationDAO.findAllByOrderByDateModificationDesc()
        )

        model.addAttribute("modeles", modeleIADAO.findAll())
        model.addAttribute("conversation", conversation)

        model.addAttribute(
            "messages",
            conversation.messages.sortedBy { it.dateCreation }
        )

        return "utilisateur/chat"
    }

    // Suppression d'une conversation
    @PostMapping("/conversations/{id}/supprimer")
    @Transactional
    fun supprimerConversation(
        @PathVariable id: Long
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conversation introuvable"
                )
            }

        // Suppression des messages associés
        messageDAO.deleteAll(conversation.messages)

        // Suppression de la conversation
        conversationDAO.delete(conversation)

        return "redirect:/"
    }

    // Envoi d'un nouveau message
    @PostMapping("/conversations/{id}/messages")
    fun envoyerMessage(
        @PathVariable id: Long,
        @RequestParam prompt: String
    ): String {

        val conversation = conversationDAO.findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conversation introuvable"
                )
            }

        // Enregistrement du message utilisateur
        val messageUtilisateur = Message(
            contenu = prompt,
            dateCreation = LocalDateTime.now(),
            typeMessage = TypeMessage.UTILISATEUR,
            conversation = conversation
        )

        messageDAO.save(messageUtilisateur)

        try {
            // Récupération du modèle associé
            val modele = conversation.modeleIA
                ?: throw IllegalStateException(
                    "Aucun modèle IA associé à cette conversation"
                )

            // Sélection du service IA
            val iaService = iaServiceFactory.getService(modele)

            // Appel de l'IA
            val reponse = iaService.envoyerPrompt(prompt, modele)

            // Enregistrement de la réponse
            val messageAssistant = Message(
                contenu = reponse,
                dateCreation = LocalDateTime.now(),
                typeMessage = TypeMessage.ASSISTANT,
                conversation = conversation
            )

            messageDAO.save(messageAssistant)

        } catch (e: Exception) {
            println("Erreur lors de l'appel à l'IA : ${e.message}")
        }

        // Mise à jour de la conversation
        conversation.dateModification = LocalDateTime.now()
        conversationDAO.save(conversation)

        return "redirect:/conversations/$id"
    }
}
