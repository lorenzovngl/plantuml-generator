import java.io.File
import java.util.concurrent.TimeUnit
import javax.swing.JFileChooser
import javax.swing.JFrame


// From https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code/41495542#41495542
fun String.runCommand(workingDir: File) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}

fun chooseFolder(): String? {
    val frame = JFrame()
    val folderChooser = JFileChooser()
    folderChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    val option: Int = folderChooser.showOpenDialog(frame)
    var filePath: String? = null
    if (option == JFileChooser.APPROVE_OPTION) {
        // User selected a file
        filePath = folderChooser.selectedFile.absolutePath
        println("Selected file: $filePath")
    } else {
        // User canceled the file selection
        println("File selection canceled.")
    }
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true
    return filePath
}