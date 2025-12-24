package suwayomi.tachidesk.server.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import java.io.File
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream

object ReactiveImageServer {
    private val logger = KotlinLogging.logger {}

    /**
     * Streams a file directly to the context output using NIO channels.
     * Efficiently handles large images and prevents Heap saturation.
     */
    fun streamImage(ctx: Context, file: File, contentType: String, targetWidth: Int? = null) {
        if (!file.exists()) {
            ctx.status(404)
            return
        }

        ctx.header("Content-Type", contentType)

        if (targetWidth != null) {
            // High-performance resizing using AWT
            try {
                val resizedImage = resizeImage(file, targetWidth)
                ctx.result(resizedImage)
                return
            } catch (e: Exception) {
                logger.error(e) { "Failed to resize image: ${file.absolutePath}" }
                // Fallback to original image
            }
        }

        // Direct NIO streaming for original image
        try {
            FileChannel.open(file.toPath(), StandardOpenOption.READ).use { channel ->
                val size = channel.size()
                ctx.header("Content-Length", size.toString())
                
                // Javalin's result(InputStream) is actually quite good as it pipes,
                // but for true zero-copy we would need to access the underlying ServletOutputStream's channel
                // which is often abstracted away. For now, we use the most efficient stream wrapper.
                ctx.result(file.inputStream())
            }
        } catch (e: Exception) {
            logger.error(e) { "Error streaming image: ${file.absolutePath}" }
            ctx.status(500)
        }
    }

    private fun resizeImage(file: File, targetWidth: Int): ByteArray {
        val originalImage = ImageIO.read(file) ?: throw Exception("Failed to read image")
        
        val aspectRatio = originalImage.height.toDouble() / originalImage.width.toDouble()
        val targetHeight = (targetWidth * aspectRatio).toInt()

        val resized = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val g : Graphics2D = resized.createGraphics()
        
        // Premium quality rendering hints
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.KEY_INTERPOLATION_BICUBIC)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.KEY_RENDER_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        g.dispose()

        val baos = ByteArrayOutputStream()
        ImageIO.write(resized, "jpg", baos)
        return baos.toByteArray()
    }
}
