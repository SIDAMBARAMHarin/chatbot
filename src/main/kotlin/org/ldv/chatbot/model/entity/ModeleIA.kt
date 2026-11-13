
package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "modele_ia")
open class ModeleIA(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Long? = null,

    @Column(nullable = false)
    open var nom: String = "",

    open var identifiantAPI: String = "",

    open var urlAPI: String = "",

    @Column(columnDefinition = "TEXT")
    open var description: String = "",

    open var logo: String = "",

    // Association Many to One avec FournisseurIA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_ia_id", nullable = false)
    open var fournisseurIA: FournisseurIA? = null

)
