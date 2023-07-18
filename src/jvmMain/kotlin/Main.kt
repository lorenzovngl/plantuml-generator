import UMLGenerator.Generator
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File

@Composable
@Preview
fun App() {
    val pleaseSelect = "Please select the project folder"
    var inputDir by remember { mutableStateOf("") }
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.wrapContentSize()
            ) {
                if (inputDir == "") {
                    Button(onClick = {
                        inputDir = chooseFolder() ?: pleaseSelect
                    }) {
                        Text(pleaseSelect)
                    }
                } else {
                    val fileList = ArrayList<SourceFile>()
                    File(inputDir).walk(FileWalkDirection.TOP_DOWN).forEach {
                        if (it.name.endsWith(".java") || it.name.endsWith(".kt")) {
                            fileList.add(SourceFile(it, remember { mutableStateOf(false) }))
                        }
                    }
                    Generator(inputDir, fileList)
                }
            }
        }
    }
}

// TODO delete temp file created during the usage of the app

fun main() = application {
    val windowState = rememberWindowState(position = WindowPosition(Alignment.Center))
    Window(
        title = "PlantUMLGenerator",
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

/*fun main(args: Array<String>) {
    if (args.size == 1 && (args[0] == "-v" || args[0] == "--version")){
        println("PlantUMLGenerator 1.0 by Lorenzo Vainigli")
        println("MIT License 2022")
        println("https://github.com/lorenzovngl/plantuml-generator")
        return
    }

    if (args.size < 2) {
        println("Please pass the following arguments:")
        println("1) Path of the folder containing the input files")
        println("2) Path of the output file")
        println("3) [Optional] Name of the file to focus on")
        return
    }

    val outputFilename = args[1]
    val outputFile = File(outputFilename)
    if (!outputFile.exists()) {
        outputFile.createNewFile()
    }

    outputFile.writeText("@startuml\n")
    val inputDir = args[0]
}*/
