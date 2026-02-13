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

import java.io.IOException
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