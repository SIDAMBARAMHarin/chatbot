
package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "conversation")
open class Conversation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Long? = null,

    open var titre: String = "",

    open var dateCreation: LocalDateTime = LocalDateTime.now(),

    open var dateModification: LocalDateTime = LocalDateTime.now(),

    // Association Many to One avec ModeleIA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modele_ia_id", nullable = false)
    open var modeleIA: ModeleIA? = null

) {

    // Association One to Many avec Message
    @OneToMany(
        mappedBy = "conversation",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    open var messages: MutableList<Message> = mutableListOf()

    @PreUpdate
    fun mettreAJourDate() {
        dateModification = LocalDateTime.now()
    }
}
