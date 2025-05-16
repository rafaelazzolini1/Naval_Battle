package com.example.navalbattle.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.example.navalbattle.R

fun sendVictoryEmailIntent(context: Context, userEmail: String, winnerScore: Int, totalMoves: Int) {
    val subject = context.getString(R.string.email_subject_victory)
    val body = context.getString(R.string.email_body_victory, winnerScore, totalMoves)

    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        // putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail)) // IF we let it as comment, the user has to fill his destinatary
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    val pm = context.packageManager

    val gmailPackage = "com.google.android.gm"
    val outlookPackage = "com.microsoft.office.outlook"

    when {
        isPackageInstalled(pm, gmailPackage) -> {
            emailIntent.setPackage(gmailPackage)
            context.startActivity(emailIntent)
        }
        isPackageInstalled(pm, outlookPackage) -> {
            emailIntent.setPackage(outlookPackage)
            context.startActivity(emailIntent)
        }
        else -> {
            // Nenhum Gmail ou Outlook, abrir chooser
            val chooser = Intent.createChooser(emailIntent, "Enviar e-mail")
            // Verifica se tem app para responder
            if (emailIntent.resolveActivity(pm) != null) {
                context.startActivity(chooser)
            } else {
                Toast.makeText(context, "Nenhum app de e-mail encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
