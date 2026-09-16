package edu.bytebloom.logiroute.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bytebloom_academy_binary_knights.frontend.logiroute.generated.resources.Res
import edu.logiroute.logiroute.App
import bytebloom_academy_binary_knights.frontend.logiroute.generated.resources.byteloom_logo
import org.jetbrains.compose.resources.painterResource

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "ByteBloom LogiRoute",
        resizable = true,
        icon = painterResource(Res.drawable.byteloom_logo)
    ) {
        App()
    }
}