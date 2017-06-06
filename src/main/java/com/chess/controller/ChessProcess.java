package com.chess.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
@ServerEndpoint("/websocket")
public class ChessProcess {

	private Session session;

	private static CopyOnWriteArraySet<ChessProcess> webSocketSet = new CopyOnWriteArraySet<>();

	private static Map<String, Position> blackPlayer = new HashMap<String, Position>();
	private static Map<String, Position> redPlayer = new HashMap<String, Position>();
	// 记录棋盘历史
	private Map<Integer,String[][]> mapHistory = new HashMap<Integer,String[][]>();
	private static String[][] map = {
		{ "E2-2", "D2-2", "C2-2", "B2-2", "A2-0", "B2-1", "C2-1", "D2-1", "E2-1" },
		{ "0", "0", "0", "0", "0", "0", "0", "0", "0" },
		{ "0", "F2-2", "0", "0", "0", "0", "0", "F2-1", "0" },
		{ "G2-5", "0", "G2-4", "0", "G2-3", "0", "G2-2", "0", "G2-1" },
		{ "0", "0", "0", "0", "0", "0", "0", "0", "0" },
		{ "0", "0", "0", "0", "0", "0", "0", "0", "0" },
		{ "G1-5", "0", "G1-4", "0", "G1-3", "0", "G1-2", "0", "G1-1" },
		{ "0", "F1-2", "0", "0", "0", "0", "0", "F1-1", "0" },
		{ "0", "0", "0", "0", "0", "0", "0", "0", "0" },
		{ "E1-2", "D1-2", "C1-2", "B1-2", "A1-0", "B1-1", "C1-1", "D1-1", "E1-1" } };

	// 第几步
	private static int step = 0;
	
	@PostConstruct
	public void init() {
		recordChessmanInfo();
	}
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
		webSocketSet.add(this);
		Map<String,String> info = new HashMap<String, String>();
		info.put("map", JSON.toJSONString(map));
		sendMessage(JSON.toJSONString(info));
	}

	@OnClose
	public void onClose() {
		webSocketSet.remove(this);
	}

	/**
	 * 接受客户端信息
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		
	}
	
	/**
	 * 广播消息
	 */
	public void boardMessage(String message) throws IOException{
		for (ChessProcess item : webSocketSet) {
			item.sendMessage(message);
		}
	}
	
	/**
	 * 给指定客户端发送信息
	 */
	public void sendMessage (String message) throws IOException {  
        this.session.getBasicRemote().sendText(message);  
    }

	/**
	 * 记录双方棋子的信息
	 */
	public void recordChessmanInfo() {
		redPlayer.clear();
		blackPlayer.clear();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].contains("1-")) {
					redPlayer.put(map[i][j], new Position(j, i));
				} else {
					blackPlayer.put(map[i][j], new Position(j, i));
				}
			}
		}
	}
/*
	public void minMaxProcess() {
		boolean ended = false;
		int i = 0;
		while (!ended) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 计数
			i++;
			// 基数时，红方走。偶数时，黑方走
			Map<String, Position> player = i % 2 == 1 ? redPlayer : blackPlayer;
			
		}
	}
	
	public void maxMinSearch(Map<String, Position> player){
		Iterator<String> keys = player.keySet().iterator();
		// 遍历所有棋子
		for(Entry<String, Position> chessmanInfo : player.entrySet()){
			// 搜索深度
			int depth = 3;
			String code = chessmanInfo.getKey();
			Position position = chessmanInfo.getValue();
			int now = Position.evaluate(code, position);// 当前得分
			// 遍历该棋子的所有走法
			List<Position> positions = nextPositionList(code, position);
			for(Position nextPosition : positions){
				
			}
		}
		
	}
	
	public int maxSearch(int depth, Entry<String, Position> chessmanInfo){
		if(depth == 0){
			return Position.evaluate(chessmanInfo.getKey(), chessmanInfo.getValue());
		}
		depth = depth-1;
		int value = -1;
		List<Position> positions = nextPositionList(chessmanInfo.getKey(), chessmanInfo.getValue());
		for(Position nextPosition : positions){
			minSearch(depth, chessmanInfo);
		}
		return 0;
	}
	
	public int minSearch(int depth, Entry<String, Position> chessmanInfo){
		if(depth == 0){
			return Position.evaluate(chessmanInfo.getKey(), chessmanInfo.getValue());
		}
		return 0;
	}
	*/
	/**
	 * 核心步骤 - 随机走法
	 * @throws IOException 
	 */
	public void process() throws IOException {
		ChessMan chessnow = new ChessMan();
		boolean ended = false;
		while (!ended) {
			sleep(2000);
			// 计数
			step++;
			// 基数时，红方走。偶数时，黑方走
			Map<String, Position> player = step % 2 == 1 ? redPlayer : blackPlayer;
			// 随机选择一个棋子，记录起编号和位置
			String[] keys = player.keySet().toArray(new String[0]);
			chessnow.code = keys[new Random().nextInt(keys.length)];
			chessnow.now = player.get(chessnow.code);
			// 获取随机走动的一步
			ChessMan chessman = randomNextPosition(chessnow.now, chessnow.code, player);
			// 棋子移动
			move(chessman);
			// 若比赛结束，退出循环
			if(isEnd(player,chessman.next)) ended = true;
			// 广播棋盘信息 (当前棋局及最近一步走法)
			Map<String,String> info = new HashMap<String, String>();
			info.put("map", JSON.toJSONString(map));
			info.put("chess", JSON.toJSONString(chessman));
			boardMessage(JSON.toJSONString(info)); 
		}
	}
	
	/**
	 * 获取一个随机的下一步走法
	 * @param now
	 * @param code
	 * @param player
	 * @return
	 */
	public ChessMan randomNextPosition(Position now,String code,Map<String, Position> player){
		ChessMan chessman = new ChessMan();
		chessman.code = code;
		chessman.now = now;
		// 获取这个棋子可以走的地方
		List<Position> positions = nextPositionList(code,now);
		// 如果该棋子不能走，则开始遍历所有棋子，找到一个能走的棋子
		if (positions.size() == 0) {
			for (String key : player.keySet()) {
				// 更换操作棋子
				chessman.now = player.get(key);
				chessman.code = key;
				List<Position> ps = nextPositionList(key,player.get(key));
				if (ps.size() == 0) continue;
				chessman.next = ps.get(new Random().nextInt(ps.size()));
				break;
			}
		} else {
			chessman.next = positions.get(new Random().nextInt(positions.size()));
		}
		return chessman;
	}
	
	public void sleep(int Millis){
		try {
			Thread.sleep(Millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 移动
	 * @param now
	 * @param next
	 * @param chessman
	 */
	public void move(ChessMan chessman){
		
		// 如果没有下一步，则不做移动
		if(chessman.next == null) return;
		// 原来的位置要记为0
		map[chessman.now.y][chessman.now.x] = "0";
		// 棋子移动到新位置
		map[chessman.next.y][chessman.next.x] = chessman.code;
		// 同步红黑方棋子信息（可能出现某些棋子被吃的情况，所以需要同步一次）
		recordChessmanInfo();
		// 棋盘记录进历史
		String[][] oldMap = new String[10][9];
		System.arraycopy(map, 0, oldMap, 0, map.length);
		mapHistory.put(step, oldMap);
		System.out.println("============ " + step + " ============");
		printMap(map);
	}
	
	/**
	 * 游戏是否结束
	 * @param player
	 * @return
	 */
	public boolean isEnd(Map<String, Position> player, Position next){
		// 红方无棋可走 or 帅被吃
		if (player == redPlayer && (next == null || player.get("A1-0") == null)){
			System.out.println("黑方胜");
			return true;
		} else if (player == blackPlayer && (next == null || player.get("A2-0") == null)) {
			System.out.println("红方胜");
			return true;
		}
		return false;
	}

	/**
	 * 打印棋谱
	 * 
	 * @param map
	 */
	public static void printMap(String[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * 判断棋子下一步可以走的所有点
	 * 
	 * @param code
	 * @param now
	 * @return
	 */
	public List<Position> nextPositionList(String code, Position now) {
		// 目前轮哪一方落字，1=红方；2=黑方
		String turn = "1";
		String offturn = "2";
		if (code.contains("2-")) {
			turn = "2";
			offturn = "1";
		}
		List<Position> positions = new ArrayList<Position>();
		if (code.startsWith("A")) {
			// 先按规则加入所有能到的点
			positions.add(new Position(now.x + 1, now.y));
			positions.add(new Position(now.x - 1, now.y));
			positions.add(new Position(now.x, now.y + 1));
			positions.add(new Position(now.x, now.y - 1));
			// 再将超过边界的点去掉
			Iterator<Position> it = positions.iterator();
			while (it.hasNext()) {
				Position p = it.next();
				if (!((p.x >= 3 && p.x <= 5) && ((p.y >= 0 && p.y <= 2) || (p.y >= 7 && p.y <= 9)))) {
					it.remove();
					continue;
				}
				// 该位置有本方棋子，也去掉
				if (map[p.y][p.x].contains(code.substring(1, 3))) {
					it.remove();
				}
			}
		} else if (code.startsWith("B")) {
			positions.add(new Position(now.x + 1, now.y + 1));
			positions.add(new Position(now.x - 1, now.y + 1));
			positions.add(new Position(now.x - 1, now.y - 1));
			positions.add(new Position(now.x + 1, now.y - 1));
			Iterator<Position> it = positions.iterator();
			while (it.hasNext()) {
				Position p = it.next();
				if (!(p.x >= 3 && p.x <= 5)) {
					it.remove();
					continue;
				}
				if (!((p.y >= 0 && p.y <= 2) || (p.y >= 7 && p.y <= 9))) {
					it.remove();
					continue;
				}
				if (map[p.y][p.x].contains(code.substring(1, 3))) {
					it.remove();
				}
			}

		} else if (code.startsWith("C")) {
			// 防止数组越界，判断是否相心被填
			if ((now.y + 1 <= 9 && now.x + 1 <= 8) && map[now.y + 1][now.x + 1].equals("0")) {
				positions.add(new Position(now.x + 2, now.y + 2));
			}
			if ((now.y + 1 <= 9 && now.x - 1 >= 0) && map[now.y + 1][now.x - 1].equals("0")) {
				positions.add(new Position(now.x - 2, now.y + 2));
			}
			if ((now.y - 1 >= 0 && now.x - 1 >= 0) && map[now.y - 1][now.x - 1].equals("0")) {
				positions.add(new Position(now.x - 2, now.y - 2));
			}
			if ((now.y - 1 >= 0 && now.x + 1 <= 8) && map[now.y - 1][now.x + 1].equals("0")) {
				positions.add(new Position(now.x + 2, now.y - 2));
			}
			Iterator<Position> it = positions.iterator();
			while (it.hasNext()) {
				Position p = it.next();
				if (turn.equals("1") && !(p.y >= 5)) {
					it.remove();
					continue;
				}
				if (turn.equals("2") && !(p.y <= 4)) {
					it.remove();
					continue;
				}
				if (!(p.x >= 0 && p.y >= 0)) {
					it.remove();
					continue;
				}
				if (map[p.y][p.x].contains(code.substring(1, 3))) {
					it.remove();
				}
			}

		} else if (code.startsWith("D")) {
			// 防止数组越界，判断是否马是否被别腿
			if ((now.y + 2 <= 9 && now.x + 1 <= 8)
					&& map[now.y + 1][now.x].equals("0")) {
				positions.add(new Position(now.x + 1, now.y + 2));
			}
			if ((now.y + 1 <= 9 && now.x + 2 <= 8)
					&& map[now.y][now.x + 1].equals("0")) {
				positions.add(new Position(now.x + 2, now.y + 1));
			}
			if ((now.y + 2 <= 9 && now.x - 1 >= 0)
					&& map[now.y + 1][now.x].equals("0")) {
				positions.add(new Position(now.x - 1, now.y + 2));
			}
			if ((now.y + 1 <= 9 && now.x - 2 >= 0)
					&& map[now.y][now.x - 1].equals("0")) {
				positions.add(new Position(now.x - 2, now.y + 1));
			}
			if ((now.y - 2 >= 0 && now.x + 1 <= 8)
					&& map[now.y - 1][now.x].equals("0")) {
				positions.add(new Position(now.x + 1, now.y - 2));
			}
			if ((now.y - 1 >= 0 && now.x + 2 <= 8)
					&& map[now.y][now.x + 1].equals("0")) {
				positions.add(new Position(now.x + 2, now.y - 1));
			}
			if ((now.y - 2 >= 0 && now.x - 1 >= 0)
					&& map[now.y - 1][now.x].equals("0")) {
				positions.add(new Position(now.x - 1, now.y - 2));
			}
			if ((now.y - 1 >= 0 && now.x - 2 >= 0)
					&& map[now.y][now.x - 1].equals("0")) {
				positions.add(new Position(now.x - 2, now.y - 1));
			}
			Iterator<Position> it = positions.iterator();
			while (it.hasNext()) {
				Position p = it.next();
				if (!(p.x >= 0 && p.y >= 0)) {
					it.remove();
					continue;
				}
				if (map[p.y][p.x].contains(code.substring(1, 3))) {
					it.remove();
				}
			}
		} else if (code.startsWith("E")) {
			// 向左走
			for (int i = now.x - 1; i >= 0; i--) {
				// 如果被本方棋子阻挡，终止本条线路
				if (map[now.y][i].contains(turn + "-")) {
					break;
				} else if (map[now.y][i].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(i, now.y));
					break;
				} else {
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(i, now.y));
				}
			}
			// 向右走
			for (int i = now.x + 1; i <= 8; i++) {
				// 如果被本方棋子阻挡，终止本条线路
				if (map[now.y][i].contains(turn + "-")) {
					break;
				} else if (map[now.y][i].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(i, now.y));
					break;
				} else {
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(i, now.y));
				}
			}
			// 向上走
			for (int i = now.y - 1; i >= 0; i--) {
				// 如果被本方棋子阻挡，终止本条线路
				if (map[i][now.x].contains(turn + "-")) {
					break;
				} else if (map[i][now.x].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(now.x, i));
					break;
				} else {
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(now.x, i));
				}
			}
			// 向下走
			for (int i = now.y + 1; i <= 9; i++) {
				// 如果被本方棋子阻挡，终止本条线路
				if (map[i][now.x].contains(turn + "-")) {
					break;
				} else if (map[i][now.x].contains(offturn + "-")) {
					// 如果被对方棋子阻挡，该步纳入集合，并终止本条线路
					positions.add(new Position(now.x, i));
					break;
				} else {
					// 如果遇到空白，该步纳入集合，并继续本条线路
					positions.add(new Position(now.x, i));
				}
			}
		} else if (code.startsWith("F")) {
			// 炮打隔山，true=炮架前的路线，false=炮架后的路线
			boolean goon = true;
			// 向左走
			for (int i = now.x - 1; i >= 0; i--) {
				if (goon) {
					if (map[now.y][i].equals("0")) {
						positions.add(new Position(i, now.y));
					} else {
						goon = false;
						continue;
					}
				} else {
					if (map[now.y][i].contains(turn + "-")) {
						break;
					} else if (map[now.y][i].contains(offturn + "-")) {
						positions.add(new Position(i, now.y));
						break;
					} else {
						continue;
					}
				}
			}
			goon = true; // 还原goon值
			// 向右走
			for (int i = now.x + 1; i <= 8; i++) {
				if (goon) {
					if (map[now.y][i].equals("0")) {
						positions.add(new Position(i, now.y));
					} else {
						goon = false;
						continue;
					}
				} else {
					if (map[now.y][i].contains(turn + "-")) {
						break;
					} else if (map[now.y][i].contains(offturn + "-")) {
						positions.add(new Position(i, now.y));
						break;
					} else {
						continue;
					}
				}
			}
			goon = true; // 还原goon值
			// 向上走
			for (int i = now.y - 1; i >= 0; i--) {
				if (goon) {
					if (map[i][now.x].equals("0")) {
						positions.add(new Position(now.x, i));
					} else {
						goon = false;
						continue;
					}
				} else {
					if (map[i][now.x].contains(turn + "-")) {
						break;
					} else if (map[i][now.x].contains(offturn + "-")) {
						positions.add(new Position(now.x, i));
						break;
					} else {
						continue;
					}
				}
			}
			goon = true; // 还原goon值
			// 向下走
			for (int i = now.y + 1; i <= 9; i++) {
				if (goon) {
					if (map[i][now.x].equals("0")) {
						positions.add(new Position(now.x, i));
					} else {
						goon = false;
						continue;
					}
				} else {
					if (map[i][now.x].contains(turn + "-")) {
						break;
					} else if (map[i][now.x].contains(offturn + "-")) {
						positions.add(new Position(now.x, i));
						break;
					} else {
						continue;
					}
				}
			}
		} else if (code.startsWith("G")) {
			if (turn.equals("1")) {
				if (now.y < 5) {
					positions.add(new Position(now.x + 1, now.y));
					positions.add(new Position(now.x - 1, now.y));
				}
				if (now.y > 0) {
					positions.add(new Position(now.x, now.y - 1));
				}
			} else {
				if (now.y > 4) {
					positions.add(new Position(now.x + 1, now.y));
					positions.add(new Position(now.x - 1, now.y));
				}
				if (now.y < 9)
					positions.add(new Position(now.x, now.y + 1));
			}
			Iterator<Position> it = positions.iterator();
			while (it.hasNext()) {
				Position p = it.next();
				if (!(p.x >= 0 && p.x <= 8)) {
					it.remove();
					continue;
				}
				if (!(p.y >= 0 && p.y <= 9)) {
					it.remove();
					continue;
				}
				if (map[p.y][p.x].contains(code.substring(1, 3))) {
					it.remove();
				}
			}
		}
		return positions;
	}
}
