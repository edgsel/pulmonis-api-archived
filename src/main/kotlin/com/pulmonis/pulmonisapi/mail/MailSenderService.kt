package com.pulmonis.pulmonisapi.mail

import com.pulmonis.pulmonisapi.mail.model.Mail
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine

@Component
class MailSenderService {
    @Autowired
    private val mailSender: JavaMailSender? = null

    @Autowired
    private val templateEngine: SpringTemplateEngine? = null

    private val logger = LoggerFactory.getLogger(MailSenderService::class.java)

    fun sendTemplateEmail(mail: Mail, template: String?): Boolean {
        val message = mailSender?.createMimeMessage()

        val context = Context().also {
            it.setVariables(mail.props)
            it.locale = LocaleContextHolder.getLocale()
        }

        if (template.isNullOrEmpty()) {
            logger.error("Email template not provided")
            return false
        }

        val html = try {
            templateEngine?.process(template, context)
        } catch (e: FileNotFoundException) {
            logger.error("Template not found")
            return false
        }

        val helper = MimeMessageHelper(
            message!!,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        ).apply {
            setTo(mail.to!!)
            setText(html!!, true)
            setSubject(mail.subject!!)
            setFrom(mail.from!!)
        }
        try {
            mailSender?.send(message)
        } catch (e: MailException) {
            logger.error("Failed to send email: ${e.message}", e)
            return false
        }
        return true
    }
}
