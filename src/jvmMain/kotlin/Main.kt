import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import layout.currentDetailView
import layout.requestItemLayout
import model.DioRequest
import org.apache.log4j.Logger
import org.smartboot.socket.transport.AioQuickServer
import socket.*

class MySimpleLog

@Composable
@Preview
fun App() {
    val scroll = rememberScrollState()
    ///当前选中的请求
    val currentRequest: MutableState<DioRequest?> = remember { mutableStateOf(null) }

    ///请求列表
    val requestList = remember { mutableStateListOf<DioRequest>() }



    LaunchedEffect(Unit) {
        EventCenter.instance.reg(DioRequestPost { dioRequest ->
            requestList.add(dioRequest)
        })
    }

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth(0.4f)) {
                Scaffold(
                    topBar = {
                        TopAppBar {
                            Text("请求列表")
                        }
                    },

                ) {
                    LazyColumn{
                        items(requestList.size) { index ->
                            val rq = requestList[index]
                            requestItemLayout(rq) { selectRequest ->
                                currentRequest.value = selectRequest
                            }
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scroll)
                    )
                }


            }
            Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                currentDetailView(request = currentRequest.value)
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }

    startServer()

}


//开启socket服务
fun startServer() {
    val stringProtocol = StringProtocol()
    val socketProcess = SocketServerProcessorImpl()
    val server = AioQuickServer(9000, stringProtocol, socketProcess)
    server.setBannerEnabled(false)
    server.setReadBufferSize(10000000)
    server.start()
}


inline fun <reified T> logInstance(): Logger {
    return Logger.getLogger(T::class.java)
}

val simpleLog = logInstance<MySimpleLog>()
