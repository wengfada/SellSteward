package com.fada.sellsteward.service;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.fada.sellsteward.domain.InWares;

public class MyApp extends Application  {
	private List<Activity> list=new LinkedList<Activity>();
	public static MyApp app;
	public Object obj;
	public boolean isRefresh;
	public HashMap<String, InWares> goSellMap;
	public void addActivity(Activity activity){
		list.add(activity);
	}
	public void exit(){
		for(Activity activity:list){
			activity.finish();
		}
		System.exit(0);;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		app=this;
		goSellMap = new HashMap<String,InWares>();
		// 注册crashHandler全局异常捕获
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}
}
