package com.chess.controller;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 位置信息
 * @author niexiaolong
 *
 */
public class Position {

	public int x;	//横轴X坐标
	public int y;	//纵轴Y坐标
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Position newInstance(int x, int y){
		return new Position(x, y);
	}
	
	// 红方 帅&兵 在各个点的分数
	private static int[][] mapAG1 = {
			{9,  9,  9, 11, 13, 11,  9,  9,  9},
			{19, 24, 34, 42, 44, 42, 34, 24, 19},
			{19, 24, 32, 37, 37, 37, 32, 24, 19},
			{19, 23, 27, 29, 30, 29, 27, 23, 19},
			{14, 18, 20, 27, 29, 27, 20, 18, 14},
			{ 7,  0, 13,  0, 16,  0, 13,  0,  7},
			{ 7,  0,  7,  0, 15,  0,  7,  0,  7},
			{ 0,  0,  0,  1,  1,  1,  0,  0,  0},
			{ 0,  0,  0,  2,  2,  2,  0,  0,  0},
			{ 0,  0,  0, 11, 15, 11,  0,  0,  0}}; 
	
	// 红方 士&相 在各个点的分数
	private static int[][] mapBC1 = {
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0, 20,  0,  0,  0, 20,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0,  0},
			{18,  0,  0, 20, 23, 20,  0,  0, 18},
			{ 0,  0,  0,  0, 23,  0,  0,  0,  0},
			{ 0,  0, 20, 20,  0, 20, 20,  0,  0}};
	
	// 红方 马 在各个点的分数
	private static int[][] mapD1 = {
			{90, 90, 90, 96, 90, 96, 90, 90, 90},
			{90, 96,103, 97, 94, 97,103, 96, 90},
			{92, 98, 99,103, 99,103, 99, 98, 92},
			{93,108,100,107,100,107,100,108, 93},
			{90,100, 99,103,104,103, 99,100, 90},
			{90, 98,101,102,103,102,101, 98, 90},
			{92, 94, 98, 95, 98, 95, 98, 94, 92},
			{93, 92, 94, 95, 92, 95, 94, 92, 93},
			{85, 90, 92, 93, 78, 93, 92, 90, 85},
			{88, 85, 90, 88, 90, 88, 90, 85, 88}};
	
	// 红方 车 在各个点的分数
	private static int[][] mapE1 = {
			{206,208,207,213,214,213,207,208,206},
			{206,212,209,216,233,216,209,212,206},
			{206,208,207,214,216,214,207,208,206},
			{206,213,213,216,216,216,213,213,206},
			{208,211,211,214,215,214,211,211,208},
			{208,212,212,214,215,214,212,212,208},
			{204,209,204,212,214,212,204,209,204},
			{198,208,204,212,212,212,204,208,198},
			{200,208,206,212,200,212,206,208,200},
			{194,206,204,212,200,212,204,206,194}};
	
	// 红方 炮 在各个点的分数
	private static int[][] mapF1 = {
			{100,100, 96, 91, 90, 91, 96,100,100},
			{ 98, 98, 96, 92, 89, 92, 96, 98, 98},
			{ 97, 97, 96, 91, 92, 91, 96, 97, 97},
			{ 96, 99, 99, 98,100, 98, 99, 99, 96},
			{ 96, 96, 96, 96,100, 96, 96, 96, 96},
			{ 95, 96, 99, 96,100, 96, 99, 96, 95},
			{ 96, 96, 96, 96, 96, 96, 96, 96, 96},
			{ 97, 96,100, 99,101, 99,100, 96, 97},
			{ 96, 97, 98, 98, 98, 98, 98, 97, 96},
			{ 96, 96, 97, 99, 99, 99, 97, 96, 96}};
	
	// 黑方
	private static int[][] mapAG2 = new int[10][9];
	private static int[][] mapBC2 = new int[10][9];
	private static int[][] mapD2 = new int[10][9];
	private static int[][] mapE2 = new int[10][9];
	private static int[][] mapF2 = new int[10][9];
	
	// 初始化 黑方各棋子战斗力
	static{
		for(int i=0; i<10; i++){
			for(int j=0; j<9; j++){
				mapAG2[9-i][j] = mapAG1[i][j];
				mapBC2[9-i][j] = mapBC1[i][j];
				mapD2[9-i][j] = mapD1[i][j];
				mapE2[9-i][j] = mapE1[i][j];
				mapF2[9-i][j] = mapF1[i][j];
			}
		}
	}
	
	/**
	 * 指定棋谱的得分
	 * @param map
	 * @return
	 */
	public static int evaluate(Map<String, Position> player){
		int score = 0;
		for(Entry<String, Position> entry : player.entrySet()){
			String code = entry.getKey();
			Position p = entry.getValue();
			
			int[][] scoreMap = null;
			if(code.startsWith("A1") || code.startsWith("G1")){
				scoreMap = mapAG1;
			}else if(code.startsWith("B1") || code.startsWith("C1")){
				scoreMap = mapBC1;
			}else if(code.startsWith("D1")){
				scoreMap = mapD1;
			}else if(code.startsWith("E1")){
				scoreMap = mapE1;
			}else if(code.startsWith("F1")){
				scoreMap = mapF1;
			}else if(code.startsWith("A2") || code.startsWith("G2")){
				scoreMap = mapAG2;
			}else if(code.startsWith("B2") || code.startsWith("C2")){
				scoreMap = mapBC2;
			}else if(code.startsWith("D2")){
				scoreMap = mapD2;
			}else if(code.startsWith("E2")){
				scoreMap = mapE2;
			}else if(code.startsWith("F2")){
				scoreMap = mapF2;
			}
			score += scoreMap[p.y][p.x];
		}
		return score;
	}
}
