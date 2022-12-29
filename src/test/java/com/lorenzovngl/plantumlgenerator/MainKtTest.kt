package com.lorenzovngl.plantumlgenerator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit


class MainKtTest {
    @Test
    fun printVersion() {
        val testOutputDir = File(Paths.get("output").toAbsolutePath().toString())
        val testOutput = File(testOutputDir, "test_output.txt")
        val testError = File(testOutputDir, "test_error.txt")
        val workingDir = File(Paths.get("out${File.separator}artifacts${File.separator}PlantUMLGenerator_jar").toAbsolutePath().toString())
        val command = "java -jar PlantUMLGenerator.jar -v"
        command.runCommand(workingDir,testOutput, testError)
        val bufferedReader: BufferedReader = File(testOutput.absolutePath).bufferedReader()
        val output = bufferedReader.use { it.readText() }
        val expectedOutput = "PlantUMLGenerator 1.0 by Lorenzo Vainigli\r\n" +
                "MIT License\r\n" +
                "https://github.com/lorenzovngl/plantuml-generator\r\n"
        assertEquals(output, expectedOutput)
    }
}