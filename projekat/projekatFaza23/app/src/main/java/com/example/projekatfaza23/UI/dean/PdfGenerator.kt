package com.example.projekatfaza23.UI.dean

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.projekatfaza23.data.db.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Paint
import android.widget.Toast
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

suspend fun exportEmployeesToPdf(
    context: Context,
    uri: Uri,
    employees: List<UserEntity>
) {
    withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // definisanje olovki za tekst
        val titlePaint = Paint().apply {
            textSize = 24f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val textPaint = TextPaint().apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 1f
        }

        // postavke a4 formata stranice
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        val colNameX = margin
        val colRoleX = margin + 180
        val colEmailX = margin + 340

        val emailColumnWidth = (pageWidth - margin - colEmailX).toInt()

        var pageNumber = 1
        var myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(myPageInfo)
        var canvas = page.canvas

        var y = 60f

        canvas.drawText("Lista zaposlenih", pageWidth / 2f, y, titlePaint)
        y += 40f

        canvas.drawLine(margin, y, pageWidth - margin, y, paint)
        y += 20f

        val headerPaint = Paint().apply {
            textSize = 12f
            isFakeBoldText = true
        }
        canvas.drawText("Ime i Prezime", colNameX, y, headerPaint)
        canvas.drawText("Uloga", colRoleX, y, headerPaint)
        canvas.drawText("Email", colEmailX, y, headerPaint)

        y += 10f
        canvas.drawLine(margin, y, pageWidth - margin, y, paint)
        y += 10f

        // petlja za upis liste zaposlenih
        for (employee in employees) {
            val fullName = "${employee.firstName} ${employee.lastName}"

            val emailLayout = StaticLayout.Builder.obtain(
                employee.email,
                0,
                employee.email.length,
                textPaint,
                emailColumnWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(1.0f, 1.0f)
                .setIncludePad(false)
                .build()


            val dynamicRowHeight = maxOf(30f, emailLayout.height.toFloat() + 10f)

            if (y + dynamicRowHeight > pageHeight - 50) {
                pdfDocument.finishPage(page)
                pageNumber++
                myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(myPageInfo)
                canvas = page.canvas
                y = 60f
            }

            canvas.drawText(fullName, colNameX, y + 10, textPaint)
            canvas.drawText(employee.role, colRoleX, y + 10, textPaint)

            canvas.save()
            canvas.translate(colEmailX, y)
            emailLayout.draw(canvas)
            canvas.restore()

            val lineY = y + dynamicRowHeight
            canvas.drawLine(margin, lineY, pageWidth - margin, lineY, linePaint)

            y += dynamicRowHeight + 10f
        }

        pdfDocument.finishPage(page)

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "PDF uspješno sačuvan!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Greška pri čuvanju PDF-a", Toast.LENGTH_SHORT).show()            }
        } finally {
            pdfDocument.close()
        }
    }
}

suspend fun exportLeaveRequestToPdf(
    context: Context,
    uri: Uri,
    request: LeaveRequest,
) {
    withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()

        val titlePaint = Paint().apply {
            textSize = 24f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }

        val boldPaint = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
            color = android.graphics.Color.DKGRAY
        }

        val textPaint = TextPaint().apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 1f
        }

        val signaturePaint = Paint().apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        var pageNumber = 1
        var myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(myPageInfo)
        var canvas = page.canvas

        var y = 60f

        canvas.drawText("Zahtjev za odsustvo", pageWidth / 2f, y, titlePaint)
        y += 30f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 30f

        val sdfDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val sdfDateTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        canvas.drawText("ID zahtjeva:", margin, y, boldPaint)
        canvas.drawText(request.id, margin + 120f, y, textPaint)
        y += 25f

        canvas.drawText("Korisnik:", margin, y, boldPaint)
        canvas.drawText(request.userEmail, margin + 120f, y, textPaint)
        y += 25f

        canvas.drawText("Tip odsustva:", margin, y, boldPaint)
        canvas.drawText(request.type, margin + 120f, y, textPaint)
        y += 25f

        canvas.drawText("Status:", margin, y, boldPaint)
        canvas.drawText(request.status.naBosanskom(), margin + 120f, y, textPaint)
        y += 25f

        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 30f

        canvas.drawText("Traženi datumi:", margin, y, boldPaint)
        y += 20f
        request.leave_dates?.forEach { dateRange ->
            if (dateRange != null) {
                val startStr = dateRange.start?.toDate()?.let { sdfDate.format(it) } ?: ""
                val endStr = dateRange.end?.toDate()?.let { sdfDate.format(it) } ?: ""
                canvas.drawText("• $startStr - $endStr", margin + 10f, y, textPaint)
                y += 20f
            }
        }


        // obrazlozenje iz explanation
        y += 10f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 30f

        canvas.drawText("Obrazloženje:", margin, y, boldPaint)
        y += 20f

        val explanationText = request.explanation ?: "Nema navedenog obrazloženja."
        val explanationWidth = (pageWidth - 2 * margin).toInt()
        val explanationLayout = StaticLayout.Builder.obtain(
            explanationText,
            0,
            explanationText.length,
            textPaint,
            explanationWidth
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.0f)
            .setIncludePad(false)
            .build()

        if (y + explanationLayout.height > pageHeight - margin) {
            pdfDocument.finishPage(page)
            pageNumber++
            myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(myPageInfo)
            canvas = page.canvas
            y = 60f
        }

        canvas.save()
        canvas.translate(margin, y)
        explanationLayout.draw(canvas)
        canvas.restore()

        y += explanationLayout.height + 20f

        y += 80f
        if (y + 50f > pageHeight - margin) {
            pdfDocument.finishPage(page)
            pageNumber++
            myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(myPageInfo)
            canvas = page.canvas
            y = 100f
        }

        val stampDateStr = request.createdAt?.toDate()?.let { sdfDate.format(it) } ?: ""
        val locationDateText = if (stampDateStr.isNotEmpty()) "Tuzla, $stampDateStr" else "Tuzla"

        val signatureWidth = 200f
        val startX = pageWidth - margin - signatureWidth
        val darkLinePaint = Paint().apply { color = android.graphics.Color.BLACK; strokeWidth = 1f }


        canvas.drawText(locationDateText, margin, y, textPaint)

        val firstName = UserManager.currentUser.value?.name ?: ""
        val lastName = UserManager.currentUser.value?.lastName ?: ""
        val fullName = "$firstName $lastName".trim()

        val signatureCenterX = startX + (signatureWidth / 2)
        canvas.drawText("prof. $fullName", signatureCenterX, y, signaturePaint)
        y += 20f

        canvas.drawLine(startX, y, pageWidth - margin, y, darkLinePaint)

        pdfDocument.finishPage(page)

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "PDF uspješno sačuvan!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Greška pri čuvanju PDF-a", Toast.LENGTH_SHORT).show()
            }
        } finally {
            pdfDocument.close()
        }

    }
}


fun RequestSatus.naBosanskom(): String {
    return when (this) {
        RequestSatus.Pending -> "Na čekanju"
        RequestSatus.PendingDean -> "Na čekanju"
        RequestSatus.Approved -> "Odobreno"
        RequestSatus.Denied -> "Odbijeno"
    }
}


