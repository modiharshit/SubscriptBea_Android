package com.example.gc.subscriptbea.model

import java.util.*

open class Subscription {

    var id = ""
    var title = ""
    var amount = ""
    var type = ""
    var startDate = ""

    constructor(id: String, title: String, amount: String, type: String, startDate: String ) {
        this.id = id
        this.title = title
        this.amount = amount
        this.type = type
        this.startDate = startDate
    }
}
