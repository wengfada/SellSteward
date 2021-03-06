package com.baidu.pcs.yun;

import java.util.logging.LogRecord;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.fada.sellsteward.BaseActivity;
import com.fada.sellsteward.R;
import com.fada.sellsteward.utils.Logger;
import com.fada.sellsteward.utils.MyDateUtils;
import com.fada.sellsteward.utils.NetUtil;

public class BKDataActivity extends BaseActivity {

	private BaiduPCSAction action;
	private TextView bkName;
	private TextView btnDate;
	private Handler myHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2://下载成功执行
				closeProgressDialog();
				Logger.toast(BKDataActivity.this,"还原成功,重启软件生效");
				break;
			case 3://上传成功执行
				closeProgressDialog();
				Logger.toast(BKDataActivity.this,"备份成功");
				edit.putLong("BKdate", System.currentTimeMillis());
				edit.commit();
				refreshBKdate();
				break;
			case 4://删除成功执行
				Logger.toast(BKDataActivity.this,"开始备份");
				action.upload(BKDataActivity.this);
				break;
			case 5://删除成功执行
				closeProgressDialog();
				break;

			default:
				break;
			}
		};
	};
	public void getConfirmDialogOk(int flag) {
		switch (flag) {
		case 1:
			//action.deleteDB(this);
			showProgressDialog();
			action.download(this);
			break;
		case 2:
			showProgressDialog();
			action.delete(this);
			break;

		default:
			break;
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnCentre:
			if(NetUtil.hasNetwork(this)){
				showConfirmDialog("会覆盖本地数据,确认吗?", 1);
			}
			
			break;
		case R.id.btnRight:
			if(NetUtil.hasNetwork(this)){
				showConfirmDialog("会覆盖服务器数据,确认吗?", 2);
			}
			break;
		}

	}

	@Override
	public void setMyContentView() {
		setContentView(R.layout.activity_baidu);

	}
	
	@Override
	public void setListeners() {
		btnBack.setOnClickListener(this);
		btnCentre.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		

	}
	
	@Override
	public void init() {
		title.setText("备份数据");
		action = new BaiduPCSAction(myHandler);
		bkName = (TextView) findViewById(R.id.bkName);
		btnDate = (TextView) findViewById(R.id.btnDate);
		PCSDemoInfo.uiThreadHandler = new Handler(); 
        PCSDemoInfo.statu = 2;
        refreshBKdate();

	}
	/**
	 * 刷新备份时间
	 */
    private void refreshBKdate(){ 
    	long BKDate = sp.getLong("BKdate", 0);
    	String time="";
    	if(BKDate==0){
    		time="还未备份";
    	}else{
    		 time = MyDateUtils.formatDateAndTime(BKDate);
    	}
    	btnDate.setText(time);
    	
    	
    } 
}
