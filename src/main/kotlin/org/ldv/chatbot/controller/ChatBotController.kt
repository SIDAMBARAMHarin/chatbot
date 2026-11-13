
package org.ldv.chatbot.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatBotController {

    @GetMapping("/")
    fun accueil(model: Model): String {
        model.addAttribute("pageTitle", "Accueil - Seal IA")
        return "utilisateur/accueil"
    }

    @GetMapping("/connexion")
    fun connexion(model: Model): String {
        model.addAttribute("pageTitle", "Connexion - Seal IA")
        return "auth/connexion"
    }
}
