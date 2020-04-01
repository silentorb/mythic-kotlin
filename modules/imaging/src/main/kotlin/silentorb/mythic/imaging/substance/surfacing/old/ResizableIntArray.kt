package silentorb.mythic.imaging.substance.surfacing.old

data class ResizableIntArray(
    val data: IntArray,
    val size: Int
)

fun checkArrayResize(array: ResizableIntArray, additional: Int): ResizableIntArray {
  val newSize = array.size + additional
  return if (newSize > array.size) {
    val newArray = IntArray(array.size * 2)
    System.arraycopy(array, 0, newArray, 0, array.size)
    ResizableIntArray(
        data = newArray,
        size = array.size
    )
  } else
    array
}
