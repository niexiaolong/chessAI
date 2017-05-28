package com.chess.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class ChessController {

	private static String[][] map = {
			{"E2-2","D2-2","C2-2","B2-2","A2-0","B2-1","C2-1","D2-1","E2-1"},
			{"0","0","0","0","0","0","0","0","0"},
			{"0","F2-2","0","0","0","0","0","F2-1","0"},
			{"G2-5","0","G2-4","0","G2-3","0","G2-2","0","G2-1"},
			{"0","0","0","0","0","0","0","0","0"},
			{"0","0","0","0","0","0","0","0","0"},
			{"G1-5","0","G1-4","0","G1-3","0","G1-2","0","G1-1"},
			{"0","F1-2","0","0","0","0","0","F1-1","0"},
			{"0","0","0","0","0","0","0","0","0"},
			{"E1-2","D1-2","C1-2","B1-2","A1-0","B1-1","C1-1","D1-1","E1-1"}
	};
	
	private static Map<String,Position> blackPlayer = new HashMap<String,Position>();
	private static Map<String,Position> redPlayer = new HashMap<String,Position>();
	
	@PostConstruct
	public void init(){
		recordChessmanInfo();
	}
	
	/**
	 * 记录双方棋子的信息
	 */
	public static void recordChessmanInfo(){
		redPlayer.clear();
		blackPlayer.clear();
		for(int i=0;i<map.length;i++){
			for(int j=0;j<map[0].length;j++){
				if(map[i][j].contains("1-")){
					redPlayer.put(map[i][j], new Position(j, i));
				}else{
					blackPlayer.put(map[i][j], new Position(j, i));
				}
			}
		}
	}
	
	@RequestMapping("/")
	public String home(Model model){
		new Thread(new Runnable() {
			
			public void run() {
				// 当前棋子代码及棋子位置
				String chessman = null;
				Position nowPosition = null;
				// 下一步要走的地方
				Position nextPosition = null;
				try {
					boolean ended = false;
					int i=0;
					while(!ended){
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						long t1 = System.currentTimeMillis();
						
						// 计数
						i++;
						// 基数时，红方走。偶数时，黑方走
						Map<String, Position> player = null;
						if(i % 2 == 1){
							player = redPlayer;
						}else{
							player = blackPlayer;
						}
						/*
						// 当前棋子代码及棋子位置
						String chessman = null;
						Position nowPosition = null;
						// 下一步要走的地方
						Position nextPosition = null;
						*/
						// 随机选择一个棋子，记录起编号和位置
						String[] keys = player.keySet().toArray(new String[0]);
						chessman = keys[new Random().nextInt(keys.length)];
						nowPosition = player.get(chessman);
						// 获取这个棋子可以走的地方
						List<Position> positions = nextPositionList(chessman, player.get(chessman));
						// 如果该棋子不能走，则开始遍历所有棋子，找到一个能走的棋子
						if(positions.size() == 0){
							for(String key : keys){
								// 更换操作棋子
								nowPosition = player.get(key);
								chessman = key;
								List<Position> ps = nextPositionList(key, player.get(key));
								if(ps.size() == 0) continue;
								nextPosition = ps.get(new Random().nextInt(ps.size()));
								break;
							}
							// 穷举之后还未空，说明无子可走
							if(nextPosition == null){
								ended = true;
								if(player == redPlayer){
									System.out.println("黑方胜");
								}else{
									System.out.println("红方胜");
								}
							}
						}else{
							nextPosition = positions.get(new Random().nextInt(positions.size()));
						}
						// 原来的位置要记为0
						map[nowPosition.y][nowPosition.x] = "0";
						// 棋子移动到新位置
						map[nextPosition.y][nextPosition.x] = chessman;
						// 同步红黑方棋子信息
						//TODO 这里会把上面的~清掉，想在不做第二次循环的情况下，将这些内容传给前端。待方案实现
						recordChessmanInfo();
						// 发现将被吃了，则游戏结束
						if(player == redPlayer && player.get("A1-0") == null){
							ended = true;
							System.out.println("黑方胜");
						}else if(player == blackPlayer && player.get("A2-0") == null){
							ended = true;
							System.out.println("红方胜");
						}
						long t2 = System.currentTimeMillis();
						System.out.println("cost:"+(t2-t1));
					}
				} catch (Exception e) {
					System.out.println("chessman -> " + chessman);
					System.out.println("now -> " + nowPosition.x + "," + nowPosition.y);
					e.printStackTrace();
				}
			}
		}).start();
		
		model.addAttribute("data", map);
		return "chess";
	}
	
	@RequestMapping("/map")
	@ResponseBody
	public String[][] mapInfo(){
		return map;
	}
	
	/**
	 * 打印棋谱
	 * @param map
	 */
	public static void printMap(String[][] map){
		for(int i=0;i<map.length;i++){
			for(int j=0;j<map[0].length;j++){
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	/**
	 * 判断棋子下一步可以走的所有点
	 * @param code
	 * @param now
	 * @return
	 */
	public static List<Position> nextPositionList(String code, Position now) {	
		// 目前轮哪一方落字，1=红方；2=黑方
		String turn = "1";
		String offturn = "2";
		if(code.contains("2-")){
			turn = "2";
			offturn = "1";
		}
		List<Position> positions = new ArrayList<Position>();
		if(code.startsWith("A")){
			// 先按规则加入所有能到的点
			positions.add(new Position(now.x+1 ,now.y));
			positions.add(new Position(now.x-1 ,now.y));
			positions.add(new Position(now.x ,now.y+1));
			positions.add(new Position(now.x ,now.y-1));
			// 再将超过边界的点去掉
			Iterator<Position> it = positions.iterator();
			while(it.hasNext()){
				Position p = it.next();
				if(!((p.x >=3 && p.x <=5) && ((p.y >=0 && p.y <= 2) || (p.y >=7 && p.y <=9)))){
					it.remove();
					continue;
				}
				// 该位置有本方棋子，也去掉
				if(map[p.y][p.x].contains(code.substring(1,3))){
					it.remove();
				}
			}
		} else if(code.startsWith("B")){
			positions.add(new Position(now.x+1 ,now.y+1));
			positions.add(new Position(now.x-1 ,now.y+1));
			positions.add(new Position(now.x-1 ,now.y-1));
			positions.add(new Position(now.x+1 ,now.y-1));
			Iterator<Position> it = positions.iterator();
			while(it.hasNext()){
				Position p = it.next();
				if(!(p.x >=3 && p.x <=5)){
					it.remove();
					continue;
				}
				if(!((p.y >=0 && p.y <= 2) || (p.y >=7 && p.y <=9))){
					it.remove();
					continue;
				}
				if(map[p.y][p.x].contains(code.substring(1,3))){
					it.remove();
				}
			}
			
		} else if(code.startsWith("C")){
			// 防止数组越界，判断是否相心被填
			if((now.y+1 <= 9 && now.x+1 <= 8) && map[now.y+1][now.x+1].equals("0")){
				positions.add(new Position(now.x+2 ,now.y+2));
			}
			if((now.y+1 <= 9 && now.x-1 >= 0) && map[now.y+1][now.x-1].equals("0")){
				positions.add(new Position(now.x-2 ,now.y+2));
			}
			if((now.y-1 >= 0 && now.x-1 >= 0) && map[now.y-1][now.x-1].equals("0")){
				positions.add(new Position(now.x-2 ,now.y-2));
			}
			if((now.y-1 >= 0 && now.x+1 <= 8) && map[now.y-1][now.x+1].equals("0")){
				positions.add(new Position(now.x+2 ,now.y-2));
			}
			Iterator<Position> it = positions.iterator();
			while(it.hasNext()){
				Position p = it.next();
				if(turn.equals("1") && !(p.y >= 5) ){
					it.remove();
				}
				if(turn.equals("2") && !(p.y <= 4) ){
					it.remove();
				}
				if(!(p.x >= 0 && p.y >= 0)){
					it.remove();
					continue;
				}
				if(map[p.y][p.x].contains(code.substring(1,3))){
					it.remove();
				}
			}
			
		} else if(code.startsWith("D")){
			// 防止数组越界，判断是否马是否被别腿
			if((now.y+2 <= 9 && now.x+1 <= 8) && map[now.y+1][now.x].equals("0")){
				positions.add(new Position(now.x+1 ,now.y+2));
			}
			if((now.y+1 <= 9 && now.x+2 <= 8) && map[now.y][now.x+1].equals("0")){
				positions.add(new Position(now.x+2 ,now.y+1));
			}
			if((now.y+2 <= 9 && now.x-1 >= 0) && map[now.y+1][now.x].equals("0")){
				positions.add(new Position(now.x-1 ,now.y+2));
			}
			if((now.y+1 <= 9 && now.x-2 >= 0) && map[now.y][now.x-1].equals("0")){
				positions.add(new Position(now.x-2 ,now.y+1));
			}
			if((now.y-2 >= 0 && now.x+1 <= 8) && map[now.y-1][now.x].equals("0")){
				positions.add(new Position(now.x+1 ,now.y-2));
			}
			if((now.y-1 >= 0 && now.x+2 <= 8) && map[now.y][now.x+1].equals("0")){
				positions.add(new Position(now.x+2 ,now.y-1));
			}
			if((now.y-2 >= 0 && now.x-1 >= 0) && map[now.y-1][now.x].equals("0")){
				positions.add(new Position(now.x-1 ,now.y-2));
			}
			if((now.y-1 >= 0 && now.x-2 >= 0) && map[now.y][now.x-1].equals("0")){
				positions.add(new Position(now.x-2 ,now.y-1));
			}
			Iterator<Position> it = positions.iterator();
			while(it.hasNext()){
				Position p = it.next();
				if(!(p.x >= 0 && p.y >= 0)){
					it.remove();
					continue;
				}
				if(map[p.y][p.x].contains(code.substring(1,3))){
					it.remove();
				}
			}
		} else if(code.startsWith("E")){
			// 向左走
			for(int i=now.x-1;i>=0;i--){
				// 如果被本方棋子阻挡，终止本条线路
				if(map[now.y][i].contains(turn + "-")) {
					break;
				}else if(map[now.y][i].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(i ,now.y));
					break;
				}else{
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(i ,now.y));
				}
			}
			// 向右走
			for(int i=now.x+1;i<=8;i++){
				// 如果被本方棋子阻挡，终止本条线路
				if(map[now.y][i].contains(turn + "-")) {
					break;
				}else if(map[now.y][i].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(i ,now.y));
					break;
				}else{
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(i ,now.y));
				}
			}
			// 向上走
			for(int i=now.y-1;i>=0;i--){
				// 如果被本方棋子阻挡，终止本条线路
				if(map[i][now.x].contains(turn + "-")) {
					break;
				}else if(map[i][now.x].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(now.x ,i));
					break;
				}else{
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(now.x ,i));
				}
			}	
			// 向下走
			for(int i=now.y+1;i<=9;i++){
				// 如果被本方棋子阻挡，终止本条线路
				if(map[i][now.x].contains(turn + "-")) {
					break;
				}else if(map[i][now.x].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(now.x ,i));
					break;
				}else{
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(now.x ,i));
				}
			}
		} else if(code.startsWith("F")){
			// 炮打隔山，true=炮架前的路线，false=炮架后的路线
			boolean goon = true;
			// 向左走
			for(int i=now.x-1;i>=0;i--){
				if(goon){
					if(map[now.y][i].equals("0")) {
						positions.add(new Position(i ,now.y));
					}else {
						goon = false;
						continue;
					}
				}else{
					if(map[now.y][i].contains(turn + "-")) {
						break;
					}else if(map[now.y][i].contains(offturn + "-")) {
						positions.add(new Position(i ,now.y));
						break;
					}else {
						continue;
					}
				}
			}
			goon = true; // 还原goon值
			// 向右走
			for(int i=now.x+1;i<=8;i++){
				if(goon){
					if(map[now.y][i].equals("0")) {
						positions.add(new Position(i ,now.y));
					}else {
						goon = false;
						continue;
					}
				}else{
					if(map[now.y][i].contains(turn + "-")) {
						break;
					}else if(map[now.y][i].contains(offturn + "-")) {
						positions.add(new Position(i ,now.y));
						break;
					}else {
						continue;
					}
				}
			}
			goon = true; // 还原goon值
			// 向上走
			for(int i=now.y-1;i>=0;i--){
				if(goon){
					if(map[i][now.x].equals("0")) {
						positions.add(new Position(now.x ,i));
					}else {
						goon = false;
						continue;
					}
				}else{
					if(map[i][now.x].contains(turn + "-")) {
						break;
					}else if(map[i][now.x].contains(offturn + "-")) {
						positions.add(new Position(now.x ,i));
						break;
					}else {
						continue;
					}
				}
			}	
			goon = true; // 还原goon值
			// 向下走
			for(int i=now.y+1;i<=9;i++){
				if(goon){
					if(map[i][now.x].equals("0")) {
						positions.add(new Position(now.x ,i));
					}else {
						goon = false;
						continue;
					}
				}else{
					if(map[i][now.x].contains(turn + "-")) {
						break;
					}else if(map[i][now.x].contains(offturn + "-")) {
						positions.add(new Position(now.x ,i));
						break;
					}else {
						continue;
					}
				}
			}
		} else if(code.startsWith("G")){
			if(turn.equals("1")){
				if(now.y < 5){
					positions.add(new Position(now.x+1 ,now.y));
					positions.add(new Position(now.x-1 ,now.y));
				} 
				if(now.y > 0){
					positions.add(new Position(now.x ,now.y-1));
				}
			}else{
				if(now.y > 4){
					positions.add(new Position(now.x+1 ,now.y));
					positions.add(new Position(now.x-1 ,now.y));
				}
				if(now.y < 9)
				positions.add(new Position(now.x ,now.y+1));
			}
			Iterator<Position> it = positions.iterator();
			while(it.hasNext()){
				Position p = it.next();
				if(!(p.x >= 0 && p.x <= 8)){
					it.remove();
					continue;
				}
				if(!(p.y >= 0 && p.y <= 9)){
					it.remove();
					continue;
				}
				if(map[p.y][p.x].contains(code.substring(1,3))){
					it.remove();
				}
			}
		}
		return positions;
	}
}
