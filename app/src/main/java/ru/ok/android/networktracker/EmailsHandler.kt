package ru.ok.android.networktracker

import android.util.Log
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class Email(
    host: String,
    port: String,
    from: String,
    password: String,
    to: String,
    subject: String,
    body: String,
    attachment: File?
) {
    var host: String = host
    var port: String = port
    var from: String = from
    var password: String = password
    var to: String = to
    var subject: String = subject
    var body: String = body
    var attachment: File? = File("")
}

class EmailHandler {
    fun sendEmail(email: Email) {
        val properties = Properties().apply {
            put("mail.smtp.host", email.host)
            put("mail.smtp.port", email.port)
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email.from, email.password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(email.from))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.to))
                setSubject(subject)
            }

            if (email.attachment != null) {
                val messageBodyPart = MimeBodyPart()
                messageBodyPart.setText(email.body)
                val attachmentBodyPart = MimeBodyPart()
                attachmentBodyPart.setDataHandler(DataHandler(FileDataSource(email.attachment)))
                attachmentBodyPart.setFileName(email.attachment!!.name)

                val multipart = MimeMultipart()
                multipart.addBodyPart(messageBodyPart)
                multipart.addBodyPart(attachmentBodyPart)

                message.setContent(multipart)

            } else {

                //setText(email.body)
            }

            Transport.send(message)
            //println("Сообщение успешно отправлено")
            Log.d("TrafficUsageJobService", "ok")
        } catch (e: MessagingException) {
            e.printStackTrace()
            //println("Ошибка при отправке сообщения")
            Log.d("TrafficUsageJobService", "ошибка")
        }
    }
}