package studio.pinkcloud.helpers

import org.mindrot.jbcrypt.BCrypt
import studio.pinkcloud.config.API_CONFIG

fun getPwdHash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(API_CONFIG.security.logRounds))

fun checkPwdHash(
  plaintext: String?,
  hash: String?,
): Boolean {
  require(hash != null && hash.startsWith("$2a$")) { "Invalid hash provided for comparison" }

  return BCrypt.checkpw(plaintext, hash)
}
