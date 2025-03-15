package ru.nativespeakers.data.auth

import io.ktor.resources.Resource

@Resource("/auth")
internal class Auth {
    @Resource("login")
    internal class Login(val parent: Auth = Auth())
}

@Resource("/user")
internal class User {
    @Resource("roles")
    internal class Roles(val parent: User = User())
}