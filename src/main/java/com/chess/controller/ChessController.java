package com.chess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EnableAutoConfiguration
public class ChessController {

	@Autowired
	private ChessProcess process;
	
	@ResponseBody
	@RequestMapping("/begin")
	public String begin(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				process.process();
			}
		}).start();
		
		return "success";
	}
}
