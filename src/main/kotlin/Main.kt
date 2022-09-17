import java.io.File

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Please pass the following arguments:")
        println("1) Path of the folder containing the input files")
        println("1) Path of the output file")
        return
    }
    val outputFilename = args[1]
    val outputFile = File(outputFilename)
    if (!outputFile.exists()) {
        outputFile.createNewFile()
    }
    outputFile.writeText("@startuml\n")
    val inputDir = args[0]
    val classes = mutableMapOf<String, String>()
    File(inputDir).walk(FileWalkDirection.BOTTOM_UP)
        .filter { it != File(inputDir) && it.name.endsWith(".java")}
        .forEach {
        val fileContent = it.readText(Charsets.UTF_8)
        val startIdx = fileContent.indexOf("class") + "class".length
        val className = fileContent.subSequence(startIdx, fileContent.indexOf("{")).toString().trim()
        val str = "class " + className + " {\n" +
                "}\n\n"
        classes[className] = fileContent
        outputFile.appendText(str)
    }
    val associations = mutableSetOf<Pair<String, String>>()
    for (mainItem in classes) {
        for (subItem in classes) {
            if (mainItem.key != subItem.key && mainItem.value.contains(subItem.key) &&
                !associations.contains(Pair(subItem.key, mainItem.key))
            ) {
                associations.add(Pair(mainItem.key, subItem.key))
            }
        }
    }
    for (association in associations) {
        outputFile.appendText(association.first + " -- " + association.second + "\n")
    }
    outputFile.appendText("@enduml")
}