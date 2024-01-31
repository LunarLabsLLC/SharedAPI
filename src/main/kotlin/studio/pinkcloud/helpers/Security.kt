package studio.pinkcloud.helpers

import org.mindrot.jbcrypt.BCrypt
import studio.pinkcloud.config.API_CONFIG

fun getPwdHash(password: String) = BCrypt.hashpw(password, BCrypt.gensalt(API_CONFIG.security.logRounds))

fun checkPassword(
  password_plaintext: String?,
  stored_hash: String?,
): Boolean {
  require(stored_hash != null && stored_hash.startsWith("$2a$")) { "Invalid hash provided for comparison" }

  return BCrypt.checkpw(password_plaintext, stored_hash)
}
