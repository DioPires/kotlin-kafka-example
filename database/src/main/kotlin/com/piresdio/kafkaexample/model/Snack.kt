package com.piresdio.kafkaexample.model

import javax.persistence.*

@Entity
@Table(name = "snacks")
data class Snack(var name: String, var price: Int): AuditModel() {
    @Id
    var id: String? = null

    @Transient
    var reviews: List<Review>? = emptyList()
}

