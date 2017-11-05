//判断当前浏览器是否支持WebSocket  
var websocket = null;  
if ('WebSocket' in window) {  
    websocket = new WebSocket("ws://localhost:8080/websocket");  
}  
else {  
	layer.msg('Not support websocket');
} 

//连接发生错误的回调方法  
websocket.onerror = function () {
	layer.msg('WS ERROR~');
};

//连接成功建立的回调方法  
websocket.onopen = function (event) {  
	layer.msg('Welcome ChessAI');
}  
  
//接收到消息的回调方法  
websocket.onmessage = function (event) {
	fillMap(event.data);
}  
  
    //连接关闭的回调方法  
websocket.onclose = function () {  
	layer.msg('Bye~');
}  
  
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。  
window.onbeforeunload = function () {  
    websocket.close();  
}  

// 填充棋盘
function fillMap(info){
	info = JSON.parse(info);
	if(info.success == 1){
        layer.msg('红方胜');
    }else if(info.success == 2){
        layer.msg('黑方胜');
    }
	var map = eval(info.map);
	var chess = info.chess;
	if((chess != undefined)){
		chess = JSON.parse(chess);
	}
	var html = '';
	for (var i = 0; i < map.length; i++) {
		html += '<div>';
		for (var j = 0; j < map[i].length; j++) {
			if(map[i][j].indexOf('A1') != -1){
				html += '<div class="A1';
			}else if(map[i][j].indexOf('B1') != -1){
				html += '<div class="B1';
			}else if(map[i][j].indexOf('C1') != -1){
				html += '<div class="C1';
			}else if(map[i][j].indexOf('D1') != -1){
				html += '<div class="D1';
			}else if(map[i][j].indexOf('E1') != -1){
				html += '<div class="E1';
			}else if(map[i][j].indexOf('F1') != -1){
				html += '<div class="F1';
			}else if(map[i][j].indexOf('G1') != -1){
				html += '<div class="G1';
			}else if(map[i][j].indexOf('A2') != -1){
				html += '<div class="A2';
			}else if(map[i][j].indexOf('B2') != -1){
				html += '<div class="B2';
			}else if(map[i][j].indexOf('C2') != -1){
				html += '<div class="C2';
			}else if(map[i][j].indexOf('D2') != -1){
				html += '<div class="D2';
			}else if(map[i][j].indexOf('E2') != -1){
				html += '<div class="E2';
			}else if(map[i][j].indexOf('F2') != -1){
				html += '<div class="F2';
			}else if(map[i][j].indexOf('G2') != -1){
				html += '<div class="G2';
			}else{
				html += '<div class="empty';
			}
			if((chess == undefined)){
				html += '"></div>';
			}else{
				var nowx = chess.now.x;
				var nowy = chess.now.y;
				var nextx = chess.next.x;
				var nexty = chess.next.y;
				if((i == nowy && j == nowx) || (i == nexty && j == nextx)){
					html += '-mark"></div>';
				}else{
					html += '"></div>';
				}
			}
		}
		html += "</div>";
	}
	$("#map").html(html);
}

function begin(){
	$.get("/begin", function(){
		$("#begin").attr("disabled",true);
	});
}