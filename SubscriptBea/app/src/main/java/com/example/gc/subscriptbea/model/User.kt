package com.example.gc.subscriptbea.model

open class User {

    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""

    constructor(id: String, firstName: String, lastName: String, email: String) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }
}