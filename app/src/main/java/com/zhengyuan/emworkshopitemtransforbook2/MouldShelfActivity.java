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
import android.view.TouchDelegate;
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
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/26.
 */

public class MouldShelfActivity extends Activity implements View.OnClickListener {
    //获取操作者的工号
    private String mLoginUserId = EMProApplicationDelegate.userInfo.getUserId();
    private String shelfInfo = "";
    //ActionBar的图标和文字
    ImageButton backBtn = null;
    ImageView menuImageButton = null;
    TextView titleTextView = null;
    //显示圆形进度条
    private static Loading_view loading = null;

    //存放从服务器读取的有物件的名字二维码的信息
    String[] subItemQRInfos = new String[1000];
    int countSubItem = 0;

    //String[] submitSubItemQRInfos = new String[1000];


    //基本组件
    private Button mSubmitButton = null;
    private ImageView mMouldShelfScanfAddImageView = null;
    private ImageView mMouldShelfScanfDeleteImageView = null;
    private ListView mMouldShelfContentsItemsListView = null;
    private List<String> mMouldShelfContentsItemsList = null;//填充适配器
    private ArrayAdapter mMouldShelfContentsItemsListAdapter = null;
    private TextView mouldShelfContentsItemsTextView = null;

    private Handler getShelfSubItemsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg != null) {
                String dataResult = (String) msg.obj;
                //Toast.makeText(MouldShelfActivity.this, "=" + dataResult, Toast.LENGTH_SHORT).show();
                if (dataResult != null && dataResult.length() > 0) {
                    String[] tempSubItemQRInfos = dataResult.split("=");
                    if (tempSubItemQRInfos.length > 1000) {
                        Toast.makeText(MouldShelfActivity.this, "Error:1001 数组越界", Toast.LENGTH_SHORT).show();
                    } else {
                        if (tempSubItemQRInfos.length > 0) {
                            //有数据
                            for (int i = 0; i < tempSubItemQRInfos.length; i++) {
                                subItemQRInfos[countSubItem++] = tempSubItemQRInfos[i];
                            }
                        } else {
                            Toast.makeText(MouldShelfActivity.this, "该货架上没有子物件", Toast.LENGTH_SHORT).show();
                            countSubItem = 0;
                        }
                    }
                } else {
                    Toast.makeText(MouldShelfActivity.this, "该货架上没有子物件", Toast.LENGTH_SHORT).show();
                    countSubItem = 0;
                }
            }
            //刷新显示
            RefreshList();
            closeProgremmer();
        }
    };
    private Handler submitSubItemhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg != null) {
                String dataResult = (String) msg.obj;
                if ("true".equals(dataResult)) {
                    Toast.makeText(MouldShelfActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    mSubmitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_theme);
                    finish();//退出当前activity
                } else {
                    Toast.makeText(MouldShelfActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                }
            }
            //刷新显示
            RefreshList();
            closeProgremmer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouldshelf);
        Intent intent = getIntent();
        shelfInfo = intent.getStringExtra("data");//捕获传进来的货架信息
        if ("".equals(shelfInfo)) {
            finish();
        }
        Toast.makeText(MouldShelfActivity.this, shelfInfo, Toast.LENGTH_LONG).show();
        initView();
        initEvent();
        initGetShelfSubItems(shelfInfo, mLoginUserId);//根据传进来的货架信息从数据库读取存放的子件
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SubmiMouldShelfButtom:
                submitDialog();
                break;
            case R.id.mouldShelfScanfAddImageView:
                //添加扫码
                sweep(1);
                break;
            case R.id.mouldShelfScanfDeleteImageView:
                //删除扫码
                sweep(0);
                break;
        }
    }

    /**
     * 扫码返回结果处理
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && intent != null) {
            if (requestCode == 1) {
                //requestCode=1 添加扫码返回处理
                String info = intent.getStringExtra("result");
                info = recode(info);//扫码器返回的信息进行编码处理
                if (info != null && !"".equals(info)) {
                    int res = checkAndAddInto(info);
                    if (0 == res) {
                        Toast.makeText(MouldShelfActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        RefreshList();
                    } else if (1 == res) {
                        Toast.makeText(MouldShelfActivity.this, "该模具以录入，不需重复录入", Toast.LENGTH_SHORT).show();
                    } else if (-1 == res) {
                        Toast.makeText(MouldShelfActivity.this, "请扫描“模具”二维码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utils.showToast("扫描失败1");
                }
            } else if (requestCode == 0) {
                //requestCode=0 删除扫码返回处理
                String info = intent.getStringExtra("result");
                info = recode(info);//扫码器返回的信息进行编码处理
                if (info != null && !"".equals(info)) {
                    if (checkAndDelete(info)) {
                        Toast.makeText(MouldShelfActivity.this, "成功移除模具", Toast.LENGTH_SHORT).show();
                        RefreshList();
                    } else {
                        Toast.makeText(MouldShelfActivity.this, "该货架没有存放该模具，不需移除", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utils.showToast("扫描失败0");
                }
            }


        } else {
            Utils.showToast("未扫描");
        }
    }


    private void initView() {
        //初始化ActionBar
        titleTextView = (TextView) findViewById(R.id.title_tv);
        String tempTitleText = " 模具货架信息";
        if (mLoginUserId != null & !"".equals(mLoginUserId)) {
            tempTitleText = mLoginUserId + tempTitleText;
        }
        if (titleTextView != null) {
            titleTextView.setText(tempTitleText);
        }
        backBtn = (ImageButton) findViewById(R.id.title_back_btn);
        if (backBtn != null) {
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        menuImageButton = (ImageButton) findViewById(R.id.main_menu_bn);
        if (menuImageButton != null) {
            menuImageButton.setVisibility(View.GONE);
        }

        mSubmitButton = (Button) findViewById(R.id.SubmiMouldShelfButtom);
        mMouldShelfScanfAddImageView = (ImageView) findViewById(R.id.mouldShelfScanfAddImageView);
        mMouldShelfScanfDeleteImageView = (ImageView) findViewById(R.id.mouldShelfScanfDeleteImageView);
        mMouldShelfContentsItemsListView = (ListView) findViewById(R.id.mouldShelfContentsItemsListView);
        mouldShelfContentsItemsTextView = (TextView) findViewById(R.id.mouldShelfContentsItemsTextView);
    }

    private void initEvent() {
        mSubmitButton.setOnClickListener(this);
        mMouldShelfScanfAddImageView.setOnClickListener(this);
        mMouldShelfScanfDeleteImageView.setOnClickListener(this);
        mMouldShelfContentsItemsList = new ArrayList<String>();
        mMouldShelfContentsItemsListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mMouldShelfContentsItemsList);
        mMouldShelfContentsItemsListView.setAdapter(mMouldShelfContentsItemsListAdapter);
        //为列表添加点击事件
        mMouldShelfContentsItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                affirmNoQRDialog(position);
                return;
            }
        });
    }

    private void initGetShelfSubItems(final String shelfInfo, final String mLoginUserId) {
        showProgremmer();//对应***中的close
        new Thread() {
            @Override
            public void run() {
                DataObtainer.INSTANCE.getShelfSubItems(shelfInfo, mLoginUserId,
                        new NetworkCallbacks.SimpleDataCallback() {
                            @Override
                            public void onFinish(boolean isSuccess, String msg, Object data) {
                                Message message = getShelfSubItemsHandler.obtainMessage();
                                message.obj = data.toString();
                                getShelfSubItemsHandler.sendMessage(message);
                                /*getShelfSubItemsHandlerTest getShelfSubItemsHandlerTest=new getShelfSubItemsHandlerTest(MouldShelfActivity.this);
                                Message message = getShelfSubItemsHandlerTest.obtainMessage();
                                message.obj = data.toString();
                                getShelfSubItemsHandlerTest.sendMessage(message);*/
                            }
                        });
            }
        }.start();
    }

    private void sweep(int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.putExtra("autoEnlarged", true);
        startActivityForResult(intent, requestCode);
    }

    private String recode(String str) {
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


    private void RefreshList() {
        //数组更新后要更新list集合
        mMouldShelfContentsItemsList.clear();
        for (int i = 0; i < countSubItem; i++) {
            mMouldShelfContentsItemsList.add(subItemQRInfos[i]);
        }
        //list更新后，adapter通知listView更新
        mMouldShelfContentsItemsListAdapter.notifyDataSetChanged();
        String content = "货架已有模具<font color='#FF0000'>" + countSubItem + "</font>个";
        mouldShelfContentsItemsTextView.setText(Html.fromHtml(content));
    }

    /**
     * 显示旋转精度条
     */
    private void showProgremmer() {//显示进度条
        loading = new Loading_view(this, R.style.CustomDialog);
        loading.show();
    }

    /**
     * 关闭旋转进度条
     */
    private void closeProgremmer() {//关闭进度条
        loading.dismiss();
    }

    /**
     * @param info
     * @return boolean 0表示正常添加，1表示已经添加，不用重复添加,-1表示扫的码不正确
     */
    private int checkAndAddInto(String info) {
        //检测是否是模具
        if (info.contains("货架") && info.contains("物料代码") && info.contains("物料名称") && info.contains("批次号")) {
            //扫描的不是模具
            return -1;
        } else {
            String WLDM=info.substring(info.indexOf(":"),info.indexOf("/"));
            String PCH=info.substring(info.lastIndexOf(":"),info.lastIndexOf("/"));
            Toast.makeText(MouldShelfActivity.this,WLDM+"/"+PCH,Toast.LENGTH_SHORT).show();
            //扫描的是模具
            if (countSubItem <= 0) {//直接添加
                subItemQRInfos[countSubItem++] = info;
                return 0;
            }
            for (int i = 0; i < countSubItem; i++) {
                if (info.equals(subItemQRInfos[i])) {
                    return 1;
                }
            }
            //循环结束没有找到相同项，添加
            subItemQRInfos[countSubItem++] = info;
            return 0;
        }
    }

    private boolean checkAndDelete(String info) {
        if (countSubItem <= 0) {//没有子项不需要删除
            return false;
        }

        for (int i = 0; i < countSubItem; i++) {
            if (info.equals(subItemQRInfos[i])) {
                for (int j = i; j < countSubItem - 1; j++) {
                    subItemQRInfos[j] = subItemQRInfos[j + 1];
                }
                countSubItem--;
                return true;
            }
        }
        //没有找到相同项，不需要删除
        return false;
    }

    /**
     * 确认该货架没有该物件的提示对话框
     *
     * @param position
     */
    private void affirmNoQRDialog(final int position) {
        AlertDialog.Builder submitDialog = new AlertDialog.Builder(this);
        submitDialog.setIcon(R.mipmap.tip);
        submitDialog.setTitle("提交提示");
        submitDialog.setMessage("请确认该物件不在该货架上！");
        submitDialog.setPositiveButton("确认已不在",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //移除一项
                        RefreshDeleteContents(position);
                        RefreshList();
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

    private void RefreshDeleteContents(int position) {
        if (position >= 0 && position < countSubItem) {
            for (int i = position; i < countSubItem - 1; i++) {
                subItemQRInfos[i] = subItemQRInfos[i + 1];
            }
            countSubItem--;
        }
    }

    private void submitDialog() {
        AlertDialog.Builder submitDialog = new AlertDialog.Builder(this);
        submitDialog.setIcon(R.mipmap.tip);
        submitDialog.setTitle("提交提示");
        submitDialog.setMessage("该货架有" + countSubItem + "个模具，确认提交？");
        submitDialog.setPositiveButton("确认提交",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //提交数据
                        String result = getStringSubmitDate();
                        SubmitDate(result, shelfInfo, mLoginUserId);
                        //禁用提交按钮
                        mSubmitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_gray);
                        mSubmitButton.setEnabled(false);//在返回成功后激活按钮
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

    private String getStringSubmitDate() {
        String result = "";
        for (int i = 0; i < countSubItem; i++) {
            result += subItemQRInfos[i] + "=";
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 提交数据访问服务器
     *
     * @param dataSubItems
     * @param shelfInfo
     * @param loginUserId
     */
    private void SubmitDate(String dataSubItems, String shelfInfo, String loginUserId) {
        DataObtainer.INSTANCE.submitDAtaSubItems(dataSubItems, shelfInfo, loginUserId,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean b, String s, Object o) {
                        Message m = submitSubItemhandler.obtainMessage();
                        m.obj = o.toString();
                        submitSubItemhandler.sendMessage(m);
                    }
                }
        );
    }


    //以下为Handler的定义，可以全部提出来另建一个class/////////////////////////////////////////////////////////////////////////////////
    static class getShelfSubItemsHandlerTest extends Handler {
        //弱引用<引用外部类>
        WeakReference<Activity> mActivity;

        getShelfSubItemsHandlerTest(Activity activity) {
            //构造创建弱引用
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            //通过弱引用获取外部类.
            Activity activity = mActivity.get();
            //进行非空再操作
            if (activity != null) {
                if (msg != null) {
                    String dataResult = (String) msg.obj;
                    Toast.makeText(activity, "ceshi ====" + dataResult, Toast.LENGTH_SHORT).show();
                } else {
                }
                //closeProgremmer();
            }
        }
    }


}
