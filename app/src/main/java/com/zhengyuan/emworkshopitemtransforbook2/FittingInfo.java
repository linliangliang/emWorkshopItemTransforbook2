package com.zhengyuan.emworkshopitemtransforbook2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.zxing.CaptureActivity;
import com.zhengyuan.baselib.constants.EMProApplicationDelegate;
import com.zhengyuan.baselib.listener.NetworkCallbacks;
import com.zhengyuan.baselib.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zy on 2018/8/21.
 */

public class FittingInfo extends Activity {

    private TextView unfinished_TextView = null;
    private ListView showUnfinishedListView = null;

    private String[] unfinishedContentsFromSN = new String[500];//从服务器获取未被添加的子项的名字
    private String[] unfinishedContentsFromSI = new String[500];//从服务器获取未被添加的子项的物料id
    private int unfinishedCountFromS = 0;
    private String[] submitFinishedItem = new String[500];//这次要提交的数据
    private String[] submitFinishedName = new String[500];//这次要提交的数据
    private int submitFinishedCount = 0;

    private List<String> contentsUnfinishedList = null;//填充适配器
    private ArrayAdapter unfinishedAdapter = null;

    private boolean hasSubmitData = false;//返回退出前确认已经提交数据


    private String info = "";
    private String submitSubItemResult;//提交字物件信息返回结果
    private Handler submitSubItemhandler;//提交数据的handler
    private String result3;
    private Handler handler3;
    private String ParentQRInfo;
    String sId = EMProApplicationDelegate.userInfo.getUserId();
    private ImageButton backBtn;
    private ImageView fitting_Takephoto_ImageView;
    private Button SubmitButtom = null;
    private boolean submitButtomState = false;//提交按钮可用状态


    /////////////////////////////////////////////////////////////////////////////linliang coding
    private Handler getNoFittingItemInfoHandler = null;
    private String getNoFittingItemInfoNameAndId = "";//用于显示


    //显示圆形进度条
    private Loading_view loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitting);
        if(sId==null || sId.length()<=0){
            Toast.makeText(FittingInfo.this,"获取用户名失败",Toast.LENGTH_SHORT).show();
            finish();
        }
        Intent intent = getIntent();
        ParentQRInfo = intent.getStringExtra("data");
        //扫码器
        fitting_Takephoto_ImageView = (ImageView) findViewById(R.id.fitting_Takephoto_ImageView);
        fitting_Takephoto_ImageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sweep(v);
                    }
                }
        );
        backBtn = findViewById(R.id.title_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton menu = (ImageButton) findViewById(R.id.main_menu_bn);
        menu.setVisibility(View.GONE);

        TextView textView = findViewById(R.id.title_tv);
        String titleText = "录装配卡信息";
        if (sId != null) {
            titleText = sId + "  " + titleText;
        }
        textView.setText(titleText);
        init();

        /*handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (result1.equals("null")) {
                    //没有数据返回
                    Toast.makeText(FittingInfo.this,"该物件当前尚未录入子物件信息", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FittingInfo.this,result1, Toast.LENGTH_SHORT).show();
                    contentsFromS = result1.split(",");
                    CountFromS=contentsFromS.length;
                    RefreshContents();
                    RefreshList();
                }
            }
        };*/

        submitSubItemhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (submitSubItemResult.equals("true")) {
                    Toast.makeText(FittingInfo.this, "提交成功!", Toast.LENGTH_SHORT).show();
                    SubmitButtom.setBackgroundResource(R.drawable.shape_rectangle_radius_theme);
                    finish();//退出当前activity
                } else {
                    Toast.makeText(FittingInfo.this, "系统异常，请联系信息中心!错误类型：Error:handler2.", Toast.LENGTH_SHORT).show();
                    SubmitButtom.setTextColor(0xFFFFFFFF);
                }
                SubmitButtom.setEnabled(true);
            }
        };
        handler3 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);

                if (result3.equals("null")) {
                    //说明不是骨架或者整机的二维码
                    //RefreshContentsByUser(info);
                    RefreshList();
                } else {
                    errDialog();
                }
            }
        };
        getNoFittingItemInfoHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                /*一顿操作*/
                //Toast.makeText(FittingInfo.this,getNoFittingItemInfoNameAndId,Toast.LENGTH_SHORT).show();
                String[] unfinishedContentsFromNameAndId;
                if(getNoFittingItemInfoNameAndId!="null" && getNoFittingItemInfoNameAndId!=null && getNoFittingItemInfoNameAndId.length()>0){
                    unfinishedContentsFromNameAndId = getNoFittingItemInfoNameAndId.split("=");
                    //Toast.makeText(FittingInfo.this, getNoFittingItemInfoNameAndId, Toast.LENGTH_SHORT).show();//测试输出
                    if (unfinishedContentsFromNameAndId.length > 1000) {
                        Toast.makeText(FittingInfo.this, "Error:1001 数组越界", Toast.LENGTH_SHORT).show();
                    } else {
                        if(unfinishedContentsFromNameAndId.length>0){
                            if (unfinishedContentsFromNameAndId.length % 2 == 0) {
                                for (int i = 0; i < (unfinishedContentsFromNameAndId.length / 2); i++) {
                                    unfinishedContentsFromSI[unfinishedCountFromS++] = unfinishedContentsFromNameAndId[i];
                                }
                                unfinishedCountFromS = 0;
                                for (int i = (unfinishedContentsFromNameAndId.length / 2); i < unfinishedContentsFromNameAndId.length; i++) {
                                    unfinishedContentsFromSN[unfinishedCountFromS++] = unfinishedContentsFromNameAndId[i];
                                }
                            } else {
                                Toast.makeText(FittingInfo.this, "Error:1002 读取子项数据失败", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(FittingInfo.this, "没有子物件需要确认，或没有子物件", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //刷新显示
                    RefreshList();
                    String tempTip="还有<font color='#FF0000'>" + unfinishedCountFromS + "</font>条子项信息尚未确认：";
                    unfinished_TextView.setText(Html.fromHtml(tempTip));
                }else{
                    Toast.makeText(FittingInfo.this, "没有子项需要录入", Toast.LENGTH_SHORT).show();
                }
                closeProgremmer();//getNoFittingItemInfo中的show
            }
        };
        getNoFittingItemInfo(ParentQRInfo);//获取未装配的子物品信息
    }

    private void init() {
        /*//添加用于测试的数据
        unfinishedContentsFromSN = "1,2,3,4,5,6,7,8".split(",");
        unfinishedContentsFromSI = "a,b,c,d,e,f,GZJWFH/2018-01-01/2018-8-1,QJ20180802047".split(",");
        unfinishedCountFromS = 8;
        //*/

        SubmitButtom = (Button) findViewById(R.id.SubmitFitting_Buttom);
        SubmitButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitFinishedCount <= 0) {
                    errDialog2();
                    return;
                }
                submitDialog();
            }
        });

        showUnfinishedListView = (ListView) findViewById(R.id.unfinishedFittingInfo_ListView);
        unfinished_TextView = (TextView) findViewById(R.id.unFinished_TextView);

        contentsUnfinishedList = new ArrayList<String>();
        unfinishedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contentsUnfinishedList);
        showUnfinishedListView.setAdapter(unfinishedAdapter);
        /*showUnfinishedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                affirmNoQRDialog(position);

                return false;
            }
        });*/
        showUnfinishedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                affirmNoQRDialog(position);
                return;
            }
        });

        RefreshList();//刷新列表
        String tempTip="还有<font color='#FF0000'>" + unfinishedCountFromS + "</font>条子项信息尚未确认：";
        unfinished_TextView.setText(Html.fromHtml(tempTip));
    }

    private void RefreshContentsNAndI(int position) {
        //更改contens数组中的数据，更新count
        //如果存在则不变，如果不存在则添加
        if (position >= 0 && position < unfinishedCountFromS) {
            for (int i = position; i < unfinishedCountFromS - 1; i++) {
                unfinishedContentsFromSN[i] = unfinishedContentsFromSN[i + 1];
                unfinishedContentsFromSI[i] = unfinishedContentsFromSI[i + 1];
            }
            unfinishedCountFromS--;
        }
    }

    /*private void RefreshContentsByUser(String date){
        //更改contens数组中的数据，更新count
        //如果存在则不变，如果不存在则添加

            if(hasThis(date)){
                //有直接跳过
                //可以提示刷新成功
                Toast.makeText(this,"该子物件信息已扫过",Toast.LENGTH_SHORT).show();
            }else{
                //没有则添加
                finishedContents[finishedCount]=date;
                finishedCount++;
                Toast.makeText(this,"该子物件信息添加成功",Toast.LENGTH_SHORT).show();
            }
    }*/


    private void RefreshList() {
        //数组更新后要更新list集合
        contentsUnfinishedList.clear();
        for (int i = 0; i < unfinishedCountFromS; i++) {
            contentsUnfinishedList.add(unfinishedContentsFromSN[i]);
        }
        //list更新后，adapter通知listView更新
        unfinishedAdapter.notifyDataSetChanged();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) { // 扫码返回了
            info = data.getStringExtra("result");
            info = recode(info);
            if (info != null && info != "") {
                //检测二维码信息是否正确,正确则删除一项显示
                checkSM3(info);
               /* if(flag){
                    RefreshContentsByUser(info);
                    RefreshList();
                }else {
                    errDialog();
                  }*/
            } else {
                Utils.showToast("未扫描");
            }
        }
    }


    public void sweep(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.putExtra("autoEnlarged", true);
        startActivityForResult(intent, 0);
    }

    public String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                Log.i("1234      ISO8859-1", formart);
            } else {
                formart = str;
                Log.i("1234      stringExtra", str);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }

    private void submitDialog() {
        AlertDialog.Builder submitDialog = new AlertDialog.Builder(this);
        submitDialog.setIcon(R.mipmap.tip);
        submitDialog.setTitle("提交提示");
        submitDialog.setMessage("您刚刚为该父项录入了" + submitFinishedCount + "条子项记录，确认提交？");
        submitDialog.setPositiveButton("确认提交",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //提交数据
                        String res = getStringSubmitDate();
                        SubmitDate(res, ParentQRInfo);
                        //禁用提交按钮
                        SubmitButtom.setBackgroundResource(R.drawable.shape_rectangle_radius_gray);
                        SubmitButtom.setEnabled(false);
                        submitButtomState = false;
                    }
                });
        submitDialog.setNegativeButton("继续录入加子项",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //取消不做操作
                    }
                });
        AlertDialog dialog = submitDialog.create();
        dialog.show();
    }

    /**/
    private void affirmNoQRDialog(final int position) {
        AlertDialog.Builder submitDialog = new AlertDialog.Builder(this);
        submitDialog.setIcon(R.mipmap.tip);
        submitDialog.setTitle("提交提示");
        submitDialog.setMessage("请确认该子物件已经装配！");
        submitDialog.setPositiveButton("确认装配且无二维码",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //要提交的数据添加一项
                        submitFinishedItem[submitFinishedCount] = unfinishedContentsFromSI[position];
                        submitFinishedName[submitFinishedCount++] = unfinishedContentsFromSN[position];
                        //移除一项
                        RefreshContentsNAndI(position);
                        RefreshList();
                        String tempTip="还有<font color='#FF0000'>" + unfinishedCountFromS + "</font>条子项信息尚未确认：";
                        unfinished_TextView.setText(Html.fromHtml(tempTip));
                    }
                });
        submitDialog.setNegativeButton("返回扫码",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //取消不做操作
                    }
                });
        AlertDialog dialog = submitDialog.create();
        dialog.show();
    }

/*
    private String getSubmitDate(){
        String result="";
        for(int i=0;i<finishedCount;i++){
            result+=finishedContents[i];
            result+=",";
        }
        result=result.substring(0,result.length()-1);
        return result;
    }
*/

    private void SubmitDate(String subItems, String parentQRInfo) {
        //Toast.makeText(FittingInfo.this,subItems,Toast.LENGTH_SHORT).show();//测试显示
        //访问服务器提交
        DataObtainer.INSTANCE.submitMaInfoForSubItems(subItems, parentQRInfo,sId,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean b, String s, Object o) {
                        submitSubItemResult = (String) o;
                        Message m = submitSubItemhandler.obtainMessage();
                        submitSubItemhandler.sendMessage(m);
                    }
                }
        );
    }

    /**
     * @param maIndfo 扫描二维码的信息
     */
    public void checkSM3(String maIndfo) {
        int position = -1;//position从零开始
        for (int i = 0; i < unfinishedCountFromS; i++) {//通过第二个数组，物料代码判断
            if (maIndfo.contains(unfinishedContentsFromSI[i])) {
                position = i;
            }
        }
        if (position >= 0 && position <= unfinishedCountFromS) {
            //是未添加的子项
            //提交的数组增加一条
            /*submitFinishedItem[submitFinishedCount] = unfinishedContentsFromSI[position];
            submitFinishedName[submitFinishedCount++] = unfinishedContentsFromSN[position];*/

            submitFinishedItem[submitFinishedCount] =maIndfo;
            submitFinishedName[submitFinishedCount++]=maIndfo;

            //为子项，需要删除
            RefreshContentsNAndI(position);
            RefreshList();
            String tempTip="还有<font color='#FF0000'>" + unfinishedCountFromS + "</font>条子项信息尚未确认：";
            unfinished_TextView.setText(Html.fromHtml(tempTip));
            Toast.makeText(FittingInfo.this,"子物件'"+maIndfo+"'添加成功",Toast.LENGTH_SHORT).show();
        } else {
            //不是未被添加的信息：1已经确认。2不是子物件
            int tempPosition = -1;
            for (int i = 0; i < submitFinishedCount; i++) {//通过第二个数组，物料代码判断
                if (maIndfo.contains(submitFinishedItem[i])) {
                    tempPosition = i;
                }
            }
            if (tempPosition >= 0 && tempPosition < submitFinishedCount) {
                Toast.makeText(FittingInfo.this, "该子物件刚刚已被确认，但还未提交到服务器！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(FittingInfo.this, "不是其子物件，或之前已被添加成功！", Toast.LENGTH_LONG).show();
            }

        }

        /*DataObtainer.INSTANCE.checkSaoMiao2(maIndfo,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        result3= (String) data;
                        Message m =handler3.obtainMessage();
                        handler3.sendMessage(m);

                    }
                });*/
    }

    private void errDialog() {
        AlertDialog.Builder errDialog = new AlertDialog.Builder(this);
        errDialog.setIcon(R.mipmap.tip);
        errDialog.setTitle("扫码错误提示！");
        errDialog.setMessage("不能扫描骨架和整机二维码，请扫码其子物件二维码！");
        errDialog.setPositiveButton("知道了！",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = errDialog.create();
        dialog.show();
    }

    private void errDialog2() {
        AlertDialog.Builder errDialog = new AlertDialog.Builder(this);
        errDialog.setIcon(R.mipmap.tip);
        errDialog.setTitle("操作错误提示");
        errDialog.setMessage("尚未添加子项，无法提交！");
        errDialog.setPositiveButton("知道了！",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog dialog = errDialog.create();
        dialog.show();
    }

    private boolean getNoFittingItemInfo(String ParentQRInfo) {
        showProgremmer();//对应getNoFittingItemInfoHandler中的closeProgremmer

        DataObtainer.INSTANCE.getNoFittingItemInfo(ParentQRInfo, sId,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        getNoFittingItemInfoNameAndId = (String) data;
                        Message m = getNoFittingItemInfoHandler.obtainMessage();
                        getNoFittingItemInfoHandler.sendMessage(m);
                    }
                });

        return false;
    }

    private void showProgremmer() {//显示进度条
        loading = new Loading_view(this, R.style.CustomDialog);
        loading.show();
    }

    private void closeProgremmer() {//关闭进度条
        loading.dismiss();
    }

    private String getStringSubmitDate() {
        String result = "";
        if (submitFinishedCount > 0) {
            //submitFinishedItem
            for (int i = 0; i < submitFinishedCount; i++) {
                result += submitFinishedItem[i] + "=";
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
