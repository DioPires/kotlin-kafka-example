package com.piresdio.kafkaexample.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "reviews")
data class Review(var rating: Int, var title: String, var text: String): AuditModel() {
    @Id
    var id: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "snack_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    var snack: Snack? = null
}