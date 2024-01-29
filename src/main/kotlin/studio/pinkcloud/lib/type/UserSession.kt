package studio.pinkcloud.lib.type

import studio.pinkcloud.module.authentication.lib.IUserSession

data class UserSession(
    override val username: String,
    override val id: String)
: IUserSession(username, id)