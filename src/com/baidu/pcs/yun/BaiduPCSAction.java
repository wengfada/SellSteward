package com.baidu.pcs.yun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.oauth2.BaiduOAuthViaDialog;
import com.baidu.pcs.BaiduPCSAPI;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.PCSActionInfo;
import com.baidu.pcs.PCSActionInfo.PCSFileInfoResponse;
import com.fada.sellsteward.R;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.NetUtil;
import com.umeng.analytics.MobclickAgent;

public class BaiduPCSAction {
	public SharedPreferences sp;
	public Handler handler;
	// Get access_token 
    public void login(final Context context){
    	 if(!NetUtil.hasNetwork(context))return;
    	getTokenBySpAndNet(context);
//    	Intent intent = new Intent();    				    						    				
//		intent.setClass(context, BKDataActivity.class); 				
//		context.startActivity(intent); 
    	
    }
    
	public BaiduPCSAction() {
		super();
	}
	public BaiduPCSAction(Handler handler) {
		super();
		this.handler=handler;
	}

	private void getTokenBySpAndNet(final Context context) {
		getTokenBySp(context);
    	if(null != PCSDemoInfo.access_token){
    		Intent intent = new Intent();    				    						    				
			intent.setClass(context, BKDataActivity.class); 				
			context.startActivity(intent); 
    	}else{
    		PCSDemoInfo.mbOauth = new BaiduOAuthViaDialog(PCSDemoInfo.app_key);
        	try {
        		//Start OAUTH dialog
        		PCSDemoInfo.mbOauth.startDialogAuth(context, new String[]{"basic", "netdisk"}, new BaiduOAuthViaDialog.DialogListener(){

        			//Login successful 
        			public void onComplete(Bundle values) {
        				//Get access_token
        				String  token= values.getString("access_token");
        				PCSDemoInfo.access_token = values.getString("access_token");
        				
        				Editor edit = sp.edit();
        				edit.putString("token", token);
        				edit.commit();
        				Intent intent = new Intent();    				    						    				
        				intent.setClass(context, BKDataActivity.class); 				
        				context.startActivity(intent);    				
        			}

        			// TODO: the error code need be redefined
        			@SuppressWarnings("unused")
    				public void onError(int error) {   				
        				Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
        			}

        			public void onCancel() {   				
        				Toast.makeText(context, "返回", Toast.LENGTH_SHORT).show();
        			}

        			public void onException(String arg0) {
        				Toast.makeText(context, arg0, Toast.LENGTH_SHORT).show();
        			}
        		});
        	} catch (Exception e) {
        		 MobclickAgent.reportError(context, e.getMessage());
        		e.printStackTrace();
        	}
    		
    	}
	}
	private void getTokenBySp(final Context context) {
		sp = context.getSharedPreferences("baidu_token",Context.MODE_PRIVATE );
    	String token = sp.getString("token", "");
    	if (!"".equals(token)) {
    		PCSDemoInfo.access_token=token;
		}
	}
	/**
	 * 上传文件
	 * @param context
	 * @param pathName 文件名
	 */
	public void uploadByname(final Context context,String pathName){
		PCSDemoInfo.fileTitle=pathName;
		upload(context);
	}
	//Upload files to cloud
    public void upload(final Context context){
    	 if(!NetUtil.hasNetwork(context))return;
    	PCSDemoInfo.sourceFile = "/data/data/com.fada.sellsteward/databases/SellSteward.db";
    	PCSDemoInfo.fileTitle="SellSteward";
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				
    			public void run() {
									
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		
		    		//Set access_token for pcs api
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    	    //Use pcs uploadFile API to uplaod files
					final PCSActionInfo.PCSFileInfoResponse response = api.uploadFile(PCSDemoInfo.sourceFile, PCSDemoInfo.mbRootPath+PCSDemoInfo.fileTitle+".db", new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub					
						}
		    		});
		    		
					//The interface of the thread UI
					PCSDemoInfo.uiThreadHandler.post(new Runnable(){
						
		    			public void run(){
		  
		    				if(response.error_code == 0){
//		    					//Delete temp file
//		    					File file = new File(PCSDemoInfo.sourceFile);
//		    					file.delete();
		    					handler.sendEmptyMessage(3);
	    					    //Bcak to the content activity
		    					back(context);
		    					
		    				}else{
		    					handler.sendEmptyMessage(5);
		    					int code = response.error_code;
		    					if(code==31061){
		    						Toast.makeText(context,"文件已存在", Toast.LENGTH_SHORT).show(); 
		    					}
		    					Toast.makeText(context,"错误代码："+response.error_code, Toast.LENGTH_SHORT).show(); 
		    				}
		    				
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
    	getTokenBySpAndNet(context);
    }
    /**
     * 下载文件
     * @param context
     * @param pathName
     */
    public void downloadByname(Context context,String pathName){
    	PCSDemoInfo.fileTitle="SellSteward";
    	download(context);
    }
 public void download(final Context context){
	 if(!NetUtil.hasNetwork(context))return;
    	if(null != PCSDemoInfo.access_token){
    		PCSDemoInfo.fileTitle="SellSteward";
    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    		//Get the download file storage path on cloud
		    		PCSDemoInfo.sourceFile = PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle+".db";
		    		//Set the download file storage path
		    		PCSDemoInfo.target = "/data/data/com.fada.sellsteward/databases/SellSteward.db";
		    		//Call PCS downloadFile API
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.downloadFile(PCSDemoInfo.sourceFile, PCSDemoInfo.target,  new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
								
						}		    			
		    		});
		    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				
		    				if(ret.error_code == 0){
			    				try{
			    					//The local store download files
				    				File file = new File(PCSDemoInfo.target);	
				    				FileInputStream inStream = new FileInputStream(file);
				    				int length = inStream.available();
				    				byte [] buffer = new byte[length];
				    				inStream.read(buffer);
				    				PCSDemoInfo.fileContent = EncodingUtils.getString(buffer, "UTF-8");
				    				inStream.close();
				    				handler.sendEmptyMessage(2);
			    				}catch (Exception e) {
			    					handler.sendEmptyMessage(5);
			    					e.printStackTrace();
			    					 MobclickAgent.reportError(context, e.getMessage());
			    					Toast.makeText(context, "读取文件失败！", Toast.LENGTH_SHORT).show();
								}
		    				}else{
		    					Toast.makeText(context, "下载失败！", Toast.LENGTH_SHORT).show();
		    				}	
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    	getTokenBySpAndNet(context);
    }
    /**
     * 删除文件
     * @param context
     */
 public boolean deleteDB(final Context context){
	 String dbpath = "/data/data/com.fada.sellsteward/databases/SellSteward.db";
	 File file = new File(dbpath);
	 if(file.exists()){
		 return file.delete();
	 }else{
		 return true;
	 }
 }
 
    //Delete the  file on the cloud 
    public void delete(final Context context){
    	 if(!NetUtil.hasNetwork(context))return;
    	PCSDemoInfo.fileTitle="SellSteward";
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		//Set access_token
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    		List<String> files = new ArrayList<String>();
		    		files.add(PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle + ".db");
		    		
		    		//Call delete api
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(0 == ret.error_code){
		    							    							
		    					if(PCSDemoInfo.statu == 2){
		    						//First remove the clouds files, and then refresh content list
		    						Logger.d("删除成功！");
		    						//Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
		    						handler.sendEmptyMessage(4);
		    						
		    					}else{
		    						//First remove the clouds files,and then upload the file 
		    						if(PCSDemoInfo.statu == 1){
		    							upload(context);
		    						} 						
		    					}		    					
		    				}else{
		    					handler.sendEmptyMessage(5);
		    					Toast.makeText(context, "删除失败！"+ret.message, Toast.LENGTH_SHORT).show();
		    				}
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    public void save(Context context) {
    	
    	try{
    		PCSDemoInfo.sourceFile = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
       		
       	    String saveFile = PCSDemoInfo.fileTitle+".txt";
       			        			 	 
       	    FileOutputStream outputStream= context.openFileOutput(saveFile, Context.MODE_PRIVATE);
       	 
       	    if(!PCSDemoInfo.fileContent.equals("")){
       		    //save file
           	    outputStream.write(PCSDemoInfo.fileContent.getBytes());
     					           	           	 
       	    }else{

	       		byte bytes = 0;
	       		outputStream.write(bytes);
       	    }
       	          	 
       	    outputStream.close();
       	    
       	    if(PCSDemoInfo.statu == 0){
       	    	//Upload the file to cloud 
       	    	upload(context);
       	    	
       	    }else{
       	    	//If it is edited file save, the first remove the clouds existing file, and then upload
       	    	if(PCSDemoInfo.statu == 1){
       	    		
       	    		delete(context);       	    		
       	    	}
       	    }
   	                 		       	 		    
       	  }catch (Exception e) {   
       		 MobclickAgent.reportError(context, e.getMessage());
               Toast.makeText(context, "保存出错", Toast.LENGTH_SHORT).show();  
               handler.sendEmptyMessage(5);
          }    	    		 
    }
    
    //Back to the content activity
    public void back(Context context){  
    	
//    	Intent content = new Intent();
//  	    content.setClass(context, BKDataActivity.class);	
//  	    context.startActivity(content);   		  	
    }
    //This function to display the list of contents
    public void list(final Context context){
    	
        if (null != PCSDemoInfo.access_token){
        	        	
    		Thread workThread = new Thread(new Runnable(){
    			
				public void run() {
					
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(PCSDemoInfo.access_token );
		    		
		    		//The path to  file storage on the cloud
		    		String path = PCSDemoInfo.mbRootPath;
		    		
		    		//Use list api
		    		final PCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    				    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			
		    			public void run(){		    				
		    			
		    				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();   
		    						    				

		    				if("[]" != ret.list.toString()){
		    					   			    	            
			    	            for(Iterator<PCSFileInfoResponse> i = ret.list.iterator(); i.hasNext();){
			    	            	
			    	            	HashMap<String, String> map =new HashMap<String, String>();
			    	            				    	            	
			    	            	PCSFileInfoResponse info = i.next();
			    	            	
			    	            	//Get the file name 			    	            	
			    	         	    String path = info.path;			    	         	    
			    	         	    String fileName = path.substring(PCSDemoInfo.mbRootPath.length(),path.lastIndexOf("."));
			    	         	    
			    	         	    //Get the last modified time
			    	         	    Date date = new Date(info.mTime*1000);
			    	         	    
			    	         	    //Modify the format of the time
			    	         	    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
			    	         	    String dateString = formatter.format(date);
		  			 			    			    	            	
			    	            	map.put("file_name", fileName);			    	            	
			    	            	map.put("time", dateString);
			    	            	
			    	            	//Add item to content list	
			    	            	list.add(map); 	            	
			    	            	PCSDemoInfo.fileNameList.add(fileName);							    				    	             
			    	            }			    	               
			    	        }else{
			    	        	
			    	        	//Clear content list
		    					list.clear();
		    					Toast.makeText(context, "您的文件夹为空！", Toast.LENGTH_SHORT).show();		    					
		    				}    
		    				
			    	        SimpleAdapter listAdapter =new SimpleAdapter(context, list,R.layout.activity_baidu, new String[]{"file_name","time"}, new int[]{R.id.bkName,R.id.btnDate});   
			    	        
			    	         //Set listview to display content
			    	        ((ListActivity)context).setListAdapter(listAdapter);
		    	         
			    	         Toast.makeText(context, "正在刷新", Toast.LENGTH_SHORT).show();

		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();

        } 
    }
}
