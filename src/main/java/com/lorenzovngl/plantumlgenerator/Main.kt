package com.lorenzovngl.plantumlgenerator

import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {
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

    // List of packages, each package contains a list of files in that package
    val allPackages = mutableMapOf<String, List<String>>()
    // List of classes, each class is associated to its file content
    val classes = mutableMapOf<String, String>()

    // Visit the directory tree and load all files
    File(inputDir).walk(FileWalkDirection.BOTTOM_UP).forEach {
        if (it.name.endsWith(".java") || it.name.endsWith(".kt")) {
            val className = it.name.subSequence(0, it.name.indexOf(".")).toString()
            val fileContent = it.readText(Charsets.UTF_8)
            val startIdx = fileContent.indexOf("package") + "package".length
            val endIdx = fileContent.indexOf("\r\n", startIdx);
            val packageName = fileContent.subSequence(startIdx, endIdx).toString().trim()
            if (allPackages[packageName] == null) {
                allPackages[packageName] = arrayListOf()
            }
            allPackages[packageName] = allPackages[packageName]!!.plus(className)
            classes[className] = fileContent
        }
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
    if (args.size == 3 && args[2].isNotEmpty()){
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
    } else {
        packages = allPackages
        associations = allAssociations
    }

    for (thisPackage in packages){
        outputFile.appendText("package " + thisPackage.key + " {\n")
        for (thisClass in thisPackage.value) {
            outputFile.appendText("class $thisClass {\n}\n\n")
        }
        outputFile.appendText("}\n\n")
    }
    for (association in associations) {
        if (mainClass.isEmpty() || (mainClass.isNotEmpty() && (association.first == mainClass || association.second == mainClass))){
            outputFile.appendText(association.first + " -- " + association.second + "\n")
        }
    }
    outputFile.appendText("@enduml")
    val currentDir = File(Paths.get("").toAbsolutePath().toString())
    ("java -jar plantuml.jar "+ outputFile.absolutePath).runCommand(currentDir)
}
