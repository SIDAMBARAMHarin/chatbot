
package org.ldv.chatbot.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "message")
open class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Long? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    open var contenu: String = "",

    open var dateCreation: LocalDateTime = LocalDateTime.now(),

    // Association Many to One avec Conversation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    open var conversation: Conversation? = null,

    // Type du message
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var typeMessage: TypeMessage = TypeMessage.UTILISATEUR

)
