import androidx.compose.runtime.MutableState
import java.io.File

data class SourceFile(
    val file: File,
    var checked: MutableState<Boolean>
)
