package com.iisquare.elasticsearch.wltea.web;

public class IndexController extends ControllerBase {

	public Object indexAction() throws Exception {
		System.out.println("IndexController.indexAction");
		return displayText("IndexController.indexAction");
	}

	public Object listAction() throws Exception {
		System.out.println("IndexController.listAction");
		return displayText("IndexController.listAction");
	}

}
