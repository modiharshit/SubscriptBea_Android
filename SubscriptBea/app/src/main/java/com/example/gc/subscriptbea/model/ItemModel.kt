package com.example.gc.subscriptbea.model

import java.util.*

open class ItemsViewModel {

    var id = ""
    var title = ""

    constructor(id: String, title: String) {
        this.id = id
        this.title = title
    }
}
