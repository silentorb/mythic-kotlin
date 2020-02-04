package silentorb.metaview.common.views

import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import org.joml.Vector2i
import java.nio.ByteBuffer

fun newImage(dimensions: Vector2i, buffer: ByteBuffer): Image {
  buffer.rewind()
  val byteArray = ByteArray(buffer.capacity())
  buffer.get(byteArray)
  val image = WritableImage(dimensions.x, dimensions.y)
  image.pixelWriter.setPixels(0, 0, dimensions.x, dimensions.y,
      PixelFormat.getByteRgbInstance(),
      byteArray, 0, dimensions.x * 3)
  return image
}

//fun outputImage(image: Image, length: Double): Node {
//  val canvas = Canvas(length, length)
//  canvas.isMouseTransparent = true
//  canvas.graphicsContext2D.drawImage(image, 0.0, 0.0, length, length)
//  return canvas
//}
