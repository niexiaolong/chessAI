$(function(){
	printMap();
	setInterval('printMap()',2000);
}); 

function printMap(){
	$.ajax({url : "/map",success : function(map){
		var startTime = new Date().getTime(); 
		var html = '';
		for (var i = 0; i < map.length; i++) {
			html += '<div>';
			for (var j = 0; j < map[i].length; j++) {
				if(map[i][j].indexOf('A1') != -1){
					html += '<div class="A1"></div>';
				}else if(map[i][j].indexOf('B1') != -1){
					html += '<div class="B1"></div>';
				}else if(map[i][j].indexOf('C1') != -1){
					html += '<div class="C1"></div>';
				}else if(map[i][j].indexOf('D1') != -1){
					html += '<div class="D1"></div>';
				}else if(map[i][j].indexOf('E1') != -1){
					html += '<div class="E1"></div>';
				}else if(map[i][j].indexOf('F1') != -1){
					html += '<div class="F1"></div>';
				}else if(map[i][j].indexOf('G1') != -1){
					html += '<div class="G1"></div>';
				}else if(map[i][j].indexOf('A2') != -1){
					html += '<div class="A2"></div>';
				}else if(map[i][j].indexOf('B2') != -1){
					html += '<div class="B2"></div>';
				}else if(map[i][j].indexOf('C2') != -1){
					html += '<div class="C2"></div>';
				}else if(map[i][j].indexOf('D2') != -1){
					html += '<div class="D2"></div>';
				}else if(map[i][j].indexOf('E2') != -1){
					html += '<div class="E2"></div>';
				}else if(map[i][j].indexOf('F2') != -1){
					html += '<div class="F2"></div>';
				}else if(map[i][j].indexOf('G2') != -1){
					html += '<div class="G2"></div>';
				}else{
					html += '<div class="empty"></div>';
				}
			}
			html += "</div>";
		}
		$("#map").html(html);
		
		var endTime = new Date().getTime(); 
		console.log("cost:"+(endTime-startTime));
	}});
}