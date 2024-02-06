package studio.pinkcloud.module.agentauth.helpers

import studio.pinkcloud.config.API_CONFIG
import java.time.LocalDate
import java.util.Date
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

private fun sendEmail(
  to: String,
  subject: String,
  body: String,
) {
  val properties = Properties()
  properties["mail.smtp.host"] = "mail.smtp2go.com"
  properties["mail.smtp.auth"] = "true"
  properties["mail.smtp.port"] = "587"
  properties["mail.smtp.starttls.enable"] = "true"
  properties["mail.smtp.ssl.protocols"] = "TLSv1.2"

  val session: Session =
    Session.getDefaultInstance(
      properties,
      object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
          return PasswordAuthentication(API_CONFIG.email.address, API_CONFIG.email.password)
        }
      },
    )

  val message = MimeMessage(session)
  message.setFrom(InternetAddress(API_CONFIG.email.address, "PinkCloud Studios"))
  message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
  message.sentDate = Date()
  message.subject = subject
  message.setText(body)
  Transport.send(message)
}

fun sendRegistrationEmail(
  to: String,
  token: String,
) {
  val subject = "Complete Your Registration at PinkCloud!"
  val body =
    "Hey there!\n\nYou have been invited, and are about to gain access to the PinkCloud " +
      "Management Dashboard!\nSimply click the following link to confirm your registration:\n" +
      String.format(
        "https://pinkcloud.studio/auth/register?token=%s",
        token,
      ) +
      "\n\n" +
      "If you are not our client and did not request for an account, you can ignore this email.\n\n" +
      "Thanks,\n" +
      "The PinkCloud Studios Team\n" +
      "\n© " + (LocalDate.now().year) + ", PinkCloud Studios. All rights reserved."

  sendEmail(to, subject, body)
}
