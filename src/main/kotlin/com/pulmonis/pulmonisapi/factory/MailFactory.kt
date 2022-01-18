package com.pulmonis.pulmonisapi.factory

import com.pulmonis.pulmonisapi.enums.MailType
import com.pulmonis.pulmonisapi.exception.InvalidDataException
import com.pulmonis.pulmonisapi.hibernate.entities.User
import com.pulmonis.pulmonisapi.mail.model.Mail
import java.util.Locale
import org.springframework.context.i18n.LocaleContextHolder
import kotlin.jvm.Throws

object MailFactory {
    val enLocale: String = Locale("en", "US").language
    val etLocale: String = Locale("et", "EE").language
    val ruLocale: String = Locale("ru", "RU").language

    val availableLocales = listOf(enLocale, etLocale, ruLocale)

    private const val EMAIL_FROM: String = "noreply.pulmonis@gmail.com"
    private val EMAIL_TEMPLATES = mapOf(
        MailType.NEW_USER to "new-user-template",
        MailType.PASSWORD_RECOVERY to "password-recovery-template"
    )

    private val EMAIL_SUBJECTS = mapOf(
        MailType.NEW_USER to mapOf(
            enLocale to "New user registration",
            ruLocale to "Новый пользователь зарегистрирован",
            etLocale to "Uus kasutaja on registreeritud"
        ),
        MailType.USER_ACTIVATION to mapOf(
            enLocale to "Your account has been activated",
            ruLocale to "Ваш аккаунт активирован",
            etLocale to "Teie kasutaja konto on aktiveeritud"
        ),
        MailType.PASSWORD_RECOVERY to mapOf(
            enLocale to "Forgotten password reset",
            ruLocale to "Восстановление забытого пароля",
            etLocale to "Unustatud parooli lähtestamine"
        ),
        MailType.SUCCESSFUL_PASSWORD_RESET to mapOf(
            enLocale to "Your password has been successfully updated",
            ruLocale to "Ваш пароль успешно обновлен",
            etLocale to "Teie parooli on edukalt uuendatud"
        )
    )

    @Throws(InvalidDataException::class)
    fun build(user: User, emailType: MailType, passwordResetLink: String = ""): Mail {
        return Mail().also {
            it.from = EMAIL_FROM
            it.to = user.email
            it.subject = getEmailSubjectOrThrow(emailType)
            it.props = when (emailType) {
                MailType.NEW_USER -> buildUserEmailProps(user)
                MailType.PASSWORD_RECOVERY -> buildPasswordResetProps(user, passwordResetLink)
                else -> buildUserEmailProps(user)
            }
        }
    }

    private fun buildUserEmailProps(user: User): MutableMap<String, Any> {
        return mutableMapOf(
            "firstname" to user.firstName!!,
            "lastname" to user.lastName!!,
            "email" to user.email!!
        )
    }

    private fun buildPasswordResetProps(user: User, passwordResetLink: String): Map<String, Any> {
        if (passwordResetLink.isEmpty()) {
            throw InvalidDataException("Password reset link is empty!")
        }

        val defaultProps = buildUserEmailProps(user)
        defaultProps.putIfAbsent("resetPasswordLink", passwordResetLink)
        // temporary email
        defaultProps.putIfAbsent("supportEmail", "support.pulmonis@gmail.com")
        return defaultProps
    }

    @Throws(InvalidDataException::class)
    private fun getEmailSubjectOrThrow(emailType: MailType): String {
        var currentLocale: String = LocaleContextHolder.getLocale().language
        currentLocale = availableLocales.find { it == currentLocale } ?: enLocale

        return EMAIL_SUBJECTS[emailType]?.get(currentLocale)
            ?: throw InvalidDataException("Email subject for email type $emailType not found")
    }

    @Throws(InvalidDataException::class)
    fun getEmailTemplateOrThrow(mailType: MailType): String {
        return EMAIL_TEMPLATES[mailType] ?: throw InvalidDataException("Template for email type $mailType not found")
    }
}
