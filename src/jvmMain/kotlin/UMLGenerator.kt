import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import androidx.compose.ui.Alignment


object UMLGenerator {

    @Composable
    fun Generator(inputDir: String, fileList: List<SourceFile>) {
        var allSelected by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth(.3f)
                .verticalScroll(rememberScrollState())
            ) {
            Button(
                onClick = {
                    allSelected = !allSelected
                    fileList.forEach {
                        it.checked.value = allSelected
                    }
                }
            ) {
                Text(if (allSelected) "Unselect all" else "Select all")
            }
            fileList.forEach { file ->
                Row {
                    Checkbox(
                        checked = file.checked.value,
                        onCheckedChange = {
                            file.checked.value = it
                        }
                    )
                    Text(file.file.path.substring(inputDir.length))
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val file = generate(fileList)
            if (file != null) {
                val imageBitmap = remember(file) {
                    loadImageBitmap(file.inputStream())
                }
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = BitmapPainter(image = imageBitmap),
                    contentDescription = ""
                )
            }
        }
    }

    fun generate(fileList: List<SourceFile>): File? {
        // List of packages, each package contains a list of files in that package
        val allPackages = mutableMapOf<String, List<String>>()
        // List of classes, each class is associated to its file content
        val classes = mutableMapOf<String, String>()

        // Visit the directory tree and load all files
        fileList.filter {
            it.checked.value
        }.forEach {
            val className = it.file.name.subSequence(0, it.file.name.indexOf(".")).toString()
            val fileContent = it.file.readText(Charsets.UTF_8)
            val startIdx = fileContent.indexOf("package") + "package".length
            var endIdx = fileContent.indexOf("\r\n", startIdx)
            if (endIdx == -1){
                endIdx = fileContent.indexOf("\n", startIdx)
            }
            val packageName = fileContent.subSequence(startIdx, endIdx).toString().trim()
            if (allPackages[packageName] == null) {
                allPackages[packageName] = arrayListOf()
            }
            allPackages[packageName] = allPackages[packageName]!!.plus(className)
            classes[className] = fileContent
        }

        // Build the list of associations. There is an association between A and B if A used B or vice versa
        val allAssociations = mutableSetOf<Pair<String, String>>()
        for (mainItem in classes) {
            for (subItem in classes) {
                if (mainItem.key != subItem.key && mainItem.value.contains(subItem.key) &&
                    !allAssociations.contains(Pair(subItem.key, mainItem.key))
                ) {
                    allAssociations.add(Pair(mainItem.key, subItem.key))
                }
            }
        }

        var mainClass = ""
        var packages = mutableMapOf<String, List<String>>()
        var associations = mutableSetOf<Pair<String, String>>()
        /*if (args.size == 3 && args[2].isNotEmpty()){
            mainClass = args[2]
            for (association in allAssociations) {
                if (association.first == mainClass || association.second == mainClass){
                    associations.add(association)
                }
            }
            for (thisPackage in allPackages){
                val packageName = thisPackage.key
                for (thisClass in thisPackage.value) {
                    var found = false
                    for (association in associations) {
                        if (association.first == thisClass || association.second == thisClass) {
                            found = true
                        }
                    }
                    if (found){
                        if (packages[packageName] == null) {
                            packages[packageName] = arrayListOf()
                        }
                        packages[packageName] = packages[packageName]!!.plus(thisClass)
                    }
                }
            }
        } else {*/
        packages = allPackages
        associations = allAssociations
        //}

        val outputFilePath = Files.createTempFile("plant_uml", ".puml") ?: return null
        val outputFile = outputFilePath.toFile()
        outputFile.appendText("@startuml\n")
        for (thisPackage in packages) {
            outputFile.appendText("package " + thisPackage.key + " {\n")
            for (thisClass in thisPackage.value) {
                outputFile.appendText("class $thisClass {\n}\n\n")
            }
            outputFile.appendText("}\n\n")
        }
        for (association in associations) {
            if (mainClass.isEmpty() || (mainClass.isNotEmpty() && (association.first == mainClass || association.second == mainClass))) {
                outputFile.appendText(association.first + " -- " + association.second + "\n")
            }
        }
        outputFile.appendText("@enduml")
        val currentDir = File(Paths.get("").toAbsolutePath().toString())
        ("java -jar plantuml.jar " + outputFile.absolutePath).runCommand(currentDir)
        return File(
            outputFile.absolutePath.subSequence(0, outputFile.absolutePath.lastIndexOf(".")).toString() + ".png"
        )
    }
}