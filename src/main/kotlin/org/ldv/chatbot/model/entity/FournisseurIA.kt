
package org.ldv.chatbot.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "fournisseur_ia")
open class FournisseurIA(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    open var id: Long? = null,

    @Column(nullable = false)
    open var nom: String = "",

    @Column(columnDefinition = "TEXT")
    open var description: String = "",

    open var logo: String = ""

) {

    // Association One to Many avec ModeleIA
    @OneToMany(mappedBy = "fournisseurIA")
    open var modeles: MutableList<ModeleIA> = mutableListOf()

}
