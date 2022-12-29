package com.lorenzovngl.plantumlgenerator

import java.io.File
import java.util.concurrent.TimeUnit


// From https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code/41495542#41495542
fun String.runCommand(workingDir: File) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}

// From https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code/41495542#41495542
fun String.runCommand(workingDir: File, redirectOutput: File, redirectError: File) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(redirectOutput)
        .redirectError(redirectError)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}