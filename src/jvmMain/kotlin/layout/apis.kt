package layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import model.DioRequest
import java.net.URI

@Composable
fun requestItemLayout(
    request: DioRequest,
    doSelect: (DioRequest)-> Unit
) {
    val uri = URI.create(request.url)
    Box(modifier = Modifier.fillMaxWidth().clickable {
        doSelect(request)
    }){
        Text(uri.path, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        Divider()
    }

}

///请求详情
@Composable
fun currentDetailView(request: DioRequest?) {
    if(request == null){
        return Text("请选择一个请求查看")
    }
    val uri = URI.create(request.url)

    var bodyString = ""

    bodyString = if(request.body is String && JSON.isValid(request.body)){
        JSON.toJSONString(JSON.parseObject(request.body),SerializerFeature.PrettyFormat)
    }else{
        request.body.toString()
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn {
           item {
               keyValueWidget("Url", "${uri.scheme}://${uri.host}${uri.path}")
               keyValueWidget("Method", request.methed)
               keyValueWidget("Time", "${request.timesatamp} ms")
               keyValueWidget("Status Code", "${request.statusCode}")
               keyValueWidget("Query", uri.query ?: "空")
               keyValueWidget("Header","", mapVal = request.headers)
               Divider()
               keyValueWidget(
                   "Respose",
                   bodyString
               )
           }
        }
    }
}


@Composable
fun keyValueWidget(title: String ,value : String,mapVal: Map<String, Any> = emptyMap()) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(title,modifier=Modifier.fillMaxWidth(0.24f), color = Color.Gray)
            Box(Modifier.width(12.dp))
            Column {
                if(mapVal.isNotEmpty()){
                    mapView(mapVal)
                }else{
                    Text(value.ifEmpty { "空" }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        Divider()

    }
}

@Composable
fun mapView(mapVal: Map<String,Any>){
    Box(Modifier.fillMaxWidth()) {

        Column {
            mapVal.keys.map { key ->
                Row(Modifier.background(Color.White).padding(4.dp)) {
                    Text(key, modifier = Modifier.fillMaxWidth(0.3f))
                    Text("${mapVal[key]}", modifier = Modifier.fillMaxWidth())
                }
            }
        }

    }
}
