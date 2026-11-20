
document.addEventListener("DOMContentLoaded", function () {

    // =====================================
    // 1. AFFICHAGE DES MESSAGES MARKDOWN
    // =====================================

    const messages = document.querySelectorAll(".markdown-content");

    messages.forEach(function (message) {

        // Vérifier que les bibliothèques sont chargées
        if (typeof marked === "undefined" ||
            typeof DOMPurify === "undefined") {
            return;
        }

        const markdown = message.textContent;
        const html = marked.parse(markdown);
        const htmlSecurise = DOMPurify.sanitize(html);

        message.innerHTML = htmlSecurise;
    });


    // =====================================
    // 2. GESTION DES FORMULAIRES DU CHAT
    // =====================================

    document.querySelectorAll(".chat-page form").forEach(function (form) {

        const textarea = form.querySelector("textarea");
        const button = form.querySelector('button[type="submit"]');

        if (!textarea) return;


        // Envoi avec la touche Entrée
        textarea.addEventListener("keydown", function (event) {

            if (event.key === "Enter" && !event.shiftKey) {

                event.preventDefault();

                form.requestSubmit();
            }

        });


        // Vérification avant l'envoi
        form.addEventListener("submit", function (event) {

            // Supprimer les espaces inutiles
            const message = textarea.value.trim();

            // Bloquer les messages vides ou composés uniquement d'espaces
            if (message.length === 0) {

                event.preventDefault();

                textarea.value = "";
                textarea.focus();

                return;
            }

            // Empêcher les doubles envois
            if (form.dataset.submitted === "true") {

                event.preventDefault();

                return;
            }

            // Nettoyer le message
            textarea.value = message;

            // Mémoriser l'envoi
            form.dataset.submitted = "true";

            // Désactiver le bouton
            if (button) {

                button.disabled = true;
                button.textContent = "Envoi en cours...";

            }

        });

    });

});
