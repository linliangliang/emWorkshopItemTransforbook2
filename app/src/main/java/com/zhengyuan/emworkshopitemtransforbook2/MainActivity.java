package com.zhengyuan.emworkshopitemtransforbook2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.zxing.CaptureActivity;
import com.zhengyuan.baselib.constants.Constants;
import com.zhengyuan.baselib.constants.EMProApplicationDelegate;
import com.zhengyuan.baselib.listener.NetworkCallbacks;
import com.zhengyuan.baselib.utils.FileManagerUtil;
import com.zhengyuan.baselib.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static com.zhengyuan.emworkshopitemtransforbook2.ImageToGallery.fileName;

public class  MainActivity extends Activity implements View.OnClickListener {
    //获取操作者的工号
    String sId = EMProApplicationDelegate.userInfo.getUserId();
    String sName=EMProApplicationDelegate.userInfo.getUserName();
    String data1;
    String info = "";
    private String imageNames;
    private String data;//返回数据结果
    private ImageButton backBtn;
    private ImageView ParentQRInfo_Scan;

    private ImageView ParentQRInfo_photo;
    private ImageView Location_photo;
    private ImageView LackItem_photo;

    private EditText parentQRInfo_Eidt;
    private String flag;
    private int[] index = new int[3];
    static int IMAGE = 0;
    String[] photoPath = new String[4];
    String[] photoName = new String[4];
    public static final int REQUST_TAKE_PHOTTO_CODE2 = 2;

    private String ParentQRInfo = "";
    private String BoxScenario = "";
    private String BoxScenario1 = "";
    private String BoxScenario2 = "";
    private String YQType = "";
    private String Location = "";
    private String BoxType = "";
    private String LackItem = "";
    private Button submitButton = null;
    private String ChuXianType = "";
    private String Factory = "";
    private String FangAn = "";
    private String CarsType = "";
    String[] content;

    private EditText LackItemEdit = null;
    //暂存LackItemEdit中的数据，防止切换丢失原有数据
    private String temp_LackItemString = "";
    private Spinner BoxScenario1_spinner = null;
    private List<String> BoxScenario1_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> BoxScenario1_adapter;//创建一个数组适配器

    private Spinner BoxScenario2_spinner = null;
    private List<String> BoxScenario2_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> BoxScenario2_adapter;//创建一个数组适配器

    private Spinner YQtype_spinner = null;
    private List<String> YQtype_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> YQtype_adapter;//创建一个数组适配器

    private Spinner Location_spinner = null;
    private List<String> Location_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> Location_adapter;//创建一个数组适配器

    private Spinner Boxtype_spinner = null;
    private List<String> Boxtype_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> Boxtype_adapter;//创建一个数组适配器

    private Spinner Factory_Spinner = null;
    private List<String> Factory_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> Factory_adapter;//创建一个数组适配器

    private Spinner FangAn_Spinner = null;
    private List<String> FangAn_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> FangAn_adapter;//创建一个数组适配器

    private Spinner CarsType_Spinner = null;
    private List<String> CarsType_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> CarsType_adapter;//创建一个数组适配器

    private Spinner ChuXianType_Spinner = null;
    private List<String> ChuXianType_list = new ArrayList<String>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> ChuXianType_adapter;//创建一个数组适配器

    //最终提交的字符串
    private String res;
    String result1;//提交返回的结果
    String result2;//验证提交的结果
    String result3;
    String result5;

    Handler handler1;//通过提交返回的结果更新数据
    Handler handler2;
    Handler handler3;
    Handler handler5;
    ImageButton mainBtn;
    //////////////////////////////////////////////////////////////linliang code
    private LinearLayout belongsZhengji=null;
    boolean isFull = false;//防止重复扫描父物件，记录下拉框是否填充过，填充过第二次就不填充
    Handler getLoginRoleHandler = null;
    Handler checkHasTheQRAndIsMachineHandler;

    String loginRole = null;
    String HasTheQRAndIsMachineString = null;
    boolean checkHasTheQRBoolean = false;
    int isMachineBoolean = 0;//1表示是整机，0表示不是整机 -1表示查询失败

    //显示圆形进度条
    private Loading_view loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.title_tv);
        String titleText = "录装配卡扫父件";
        if (sId != null) {
            titleText = sId + "  " + titleText;
        }
        textView.setText(titleText);
        backBtn = findViewById(R.id.title_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //右上角的扫码器
        mainBtn = (ImageButton) findViewById(R.id.main_menu_bn);
        mainBtn.setVisibility(View.GONE);

        index[0] = 0;//记录三张图片是否拍了
        index[1] = 0;
        index[2] = 0;
        init();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMachineBoolean==0) {
                    //不是整机的数据提交
                    if ((!parentQRInfo_Eidt.getText().toString().equals("")) && index[0] == 1) {//检验提交数据的完整性

                        //禁用按钮
                        submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_gray);
                        submitButton.setEnabled(false);

                        imageNames = getImagePath();
                        //上传图片到服务器
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (photoPath[0] != null && !photoPath.equals("")) {
                                    boolean result = FileManagerUtil.uploadFileByUrl(new File(photoPath[0]),
                                            photoName[0], Constants.UPLOAD_IMAGE_URL);
                                }
                                submitData1(parentQRInfo_Eidt.getText().toString().trim(), sId, photoName[0]);
                            }
                        }).start();
                    } else{
                        //给出提示，提示空提交
                        Toast.makeText(MainActivity.this, "请先扫码并完善父物件图片", Toast.LENGTH_SHORT).show();
                    }
                } else if(isMachineBoolean==1){
                    //整机的数据提交
                    if (index[0] == 0 || index[1] == 0 || index[2] == 0) {
                        Toast.makeText(MainActivity.this, "请先完善图片,再提交!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //检验提交数据的完整性
                    data = checkInfoComplete();
                    imageNames = getImagePath();
                    //输入信息有效
                    if (!data.equals("err")) {
                        if (checkLackItem()) {
                            //提交数据 data
                            //禁用按钮
                            submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_gray);
                            submitButton.setEnabled(false);
                            //上传图片到服务器
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    for (int i = 0; i < photoName.length; i++) {
                                        if (photoPath[i] != null && !photoPath.equals("")) {
                                            boolean result = FileManagerUtil.uploadFileByUrl(new File(photoPath[i]),
                                                    photoName[i],
                                                    Constants.UPLOAD_IMAGE_URL);
                                        }
                                    }
                                    submitMachineData(data, sId, imageNames, parentQRInfo_Eidt.getText().toString());
                                }
                            }).start();
                        } else {
                            Toast.makeText(MainActivity.this, "缺件说明字数不足20字,无法提交", Toast.LENGTH_SHORT).show();
                        }
                    } else if (checkInfoComplete().equals("err")) {
                        //无效，提示
                        Toast.makeText(MainActivity.this, "请将数据填充完整!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //班组长提交非整机的二维码信息以及图片信息
        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (result1.equals("true")) {
                    Toast.makeText(MainActivity.this, "提交成功!", Toast.LENGTH_SHORT).show();
                    //清除数据
                    submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_theme);
                    submitButton.setEnabled(true);
                    //清除数据
                    clearContent();
                    clearPhoto();
                    //deleteImage();
                } else {
                    Toast.makeText(MainActivity.this, "提交失败，请联系信息中心!", Toast.LENGTH_SHORT).show();
                    submitButton.setTextColor(0xFFFFFFFF);
                    submitButton.setEnabled(true);
                    // deleteImage();
                }
            }
        };
        //对父物件二维码的回显
        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                setContent(result2);
            }
        };
        //提交整机二维码的信息 做第一次的插入操作 或者更新操作 对实时表和备份表
        handler3 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if (result3.equals("true")) {
                    Toast.makeText(MainActivity.this, "提交成功!", Toast.LENGTH_SHORT).show();
                    submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_theme);
                    submitButton.setEnabled(true);
                    //清除数据
                    clearContent();
                    clearPhoto();
                } else if (result3.equals("false")) {
                    Toast.makeText(MainActivity.this, "提交失败，请联系信息中心!", Toast.LENGTH_SHORT).show();
                    submitButton.setTextColor(0xFFFFFFFF);
                    submitButton.setEnabled(true);

                } else {
                    Toast.makeText(MainActivity.this, "系统故障，请联系信息中心!", Toast.LENGTH_SHORT).show();
                    submitButton.setTextColor(0xFFFFFFFF);
                    submitButton.setEnabled(true);
                }


            }
        };


        //组员跳转到配置卡界面
        handler5 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                //String ParentQRInfo = ((EditText)findViewById(R.id.ParentQRInfo_Eidt)).getText().toString().trim();
                super.handleMessage(msg);
                if (result5.equals("null")) {
                    alertErrDialog();
                } else {
                    Intent intent = new Intent(MainActivity.this, FittingInfo.class);
                    intent.putExtra("data", result5);
                    startActivity(intent);
                }
            }
        };


        //获取用户登录的角色（班组长/员工）
        getLoginRoleHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (loginRole != null) {
                    if ("组长".equals(loginRole)) {
                        //班组长身份
                        Toast.makeText(MainActivity.this, "组长 "+sName+"，请确认父物件信息", Toast.LENGTH_SHORT).show();
                        UIForGroupLeader();
                    } else {
                        //组员身份,界面不变
                        Toast.makeText(MainActivity.this, "组员 "+sName+"，请录装配卡信息", Toast.LENGTH_SHORT).show();
                        ParentQRInfo_Scan.setEnabled(true);
                        ParentQRInfo_photo.setVisibility(View.GONE);
                    }
                } else {
                    //获取身份失败
                    Toast.makeText(MainActivity.this, "系统获取身份失败，请联系信息中心", Toast.LENGTH_SHORT).show();
                }
                closeProgremmer();//对应getLoginRole中的showProgremmer
            }
        };

        checkHasTheQRAndIsMachineHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(!"error".equals(HasTheQRAndIsMachineString)){
                    if (HasTheQRAndIsMachineString != null ) {
                        String[] temp = HasTheQRAndIsMachineString.split(",");
                        if (temp.length == 2) {
                            //数据库实时表有无该数据
                            if ("true".equals(temp[0])) {
                                checkHasTheQRBoolean = true;
                            } else {
                                checkHasTheQRBoolean = false;
                            }
                            //扫的是否是整机
                            if ("true".equals(temp[1])) {
                                isMachineBoolean = 1;

                            }else if("false".equals(temp[1])) {
                                isMachineBoolean = 0;
                            }else{
                                isMachineBoolean = -1;
                            }

                            if ("组长".equals(loginRole)) {
                                //分角色
                                parentQRInfo_Eidt.setText(info);
                                if (isMachineBoolean==1) { //整机
                                    //显示整机明细
                                    belongsZhengji.setVisibility(View.VISIBLE);

                                    if (isFull == false) {
                                        isMachineQRInfo();
                                    }

                                    if (checkHasTheQRBoolean) {
                                        //回显
                                        backDisplayMachineInfo(info);
                                    } else {
                                        Toast.makeText(MainActivity.this, "该物件信息尚未被录入，请录入信息", Toast.LENGTH_SHORT).show();
                                    }
                                } else if(isMachineBoolean==0){
                                    //不显示整机明细
                                    belongsZhengji.setVisibility(View.GONE);

                                    Toast.makeText(MainActivity.this, "扫描的是非整机", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "登录角色不对", Toast.LENGTH_SHORT).show();
                                }
                            } else  if ("组员".equals(loginRole)){
                                //组员
                                if (checkHasTheQRBoolean) {
                                    //跳转到另一个activity
                                    Intent intent = new Intent(MainActivity.this, FittingInfo.class);
                                    intent.putExtra("data", info);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "该父物件信息未被班组长确认，请联系班组长", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "checkHasTheQRAndIsMachine has an error...系统故障请联系管理员", Toast.LENGTH_LONG).show();
                        }
                    }

                }else{
                    Toast.makeText(MainActivity.this, "二维码信息不对，请确认后重扫", Toast.LENGTH_LONG).show();
                }
                closeProgremmer();//onActivityResult中的show
            }
        };

        //扫码功能
        ParentQRInfo_Scan = findViewById(R.id.ParentQRInfo_Scan);
        ParentQRInfo_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "ParentQRInfo";
                sweep(v);
            }
        });
        ParentQRInfo_photo = findViewById(R.id.ParentQRInfo_photo);
        Location_photo = findViewById(R.id.Location_photo);
        LackItem_photo = findViewById(R.id.LackItem_photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        ParentQRInfo_photo.setOnClickListener(this);
        Location_photo.setOnClickListener(this);
        LackItem_photo.setOnClickListener(this);
        ParentQRInfo_photo.setTag(1);
        Location_photo.setTag(2);
        LackItem_photo.setTag(3);
        parentQRInfo_Eidt = findViewById(R.id.ParentQRInfo_Eidt);

        //初始化禁用所有控件
        UnableAllWidget();
        //访问服务器获取用户身份，根据登录用户身份开启控件
        getLoginRole(sId);
    }


    public void onClick(View v) {
        IMAGE = (Integer) v.getTag();
        //index[IMAGE - 1] = 1;
        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageToGallery.filePath);
        startActivityForResult(intent, REQUST_TAKE_PHOTTO_CODE2);
    }

    private void UnableAllWidget() {
        //禁用所有控件
        submitButton.setEnabled(false);
        submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_gray);

        //parentQRInfo_Eidt.setEnabled(false);//静态页面一直写死禁用
        ParentQRInfo_Scan.setEnabled(false);
        ParentQRInfo_photo.setEnabled(false);

        Boxtype_spinner.setEnabled(false);
        BoxScenario1_spinner.setEnabled(false);
        BoxScenario2_spinner.setEnabled(false);
        YQtype_spinner.setEnabled(false);
        Location_spinner.setEnabled(false);
        Factory_Spinner.setEnabled(false);
        FangAn_Spinner.setEnabled(false);
        CarsType_Spinner.setEnabled(false);
        ChuXianType_Spinner.setEnabled(false);

        LackItemEdit.setFocusable(false);
        LackItemEdit.setFocusableInTouchMode(false);

        ParentQRInfo_photo.setEnabled(false);
        Location_photo.setEnabled(false);
        LackItem_photo.setEnabled(false);


        //不显示整机明细部分
        belongsZhengji.setVisibility(View.GONE);
        //右上角拍照也禁用
        ParentQRInfo_photo.setVisibility(View.GONE);

    }

    private void EnableAllWidget() {
        //启用所有控件
        submitButton.setEnabled(true);
        submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_theme);

        ParentQRInfo_Scan.setEnabled(true);
        ParentQRInfo_photo.setEnabled(true);

        Boxtype_spinner.setEnabled(true);
        BoxScenario1_spinner.setEnabled(true);
        BoxScenario2_spinner.setEnabled(true);
        YQtype_spinner.setEnabled(true);
        Location_spinner.setEnabled(true);
        Factory_Spinner.setEnabled(true);
        FangAn_Spinner.setEnabled(true);
        CarsType_Spinner.setEnabled(true);
        ChuXianType_Spinner.setEnabled(true);

        LackItemEdit.setFocusable(true);
        LackItemEdit.setFocusableInTouchMode(true);

        ParentQRInfo_photo.setEnabled(true);
        Location_photo.setEnabled(true);
        LackItem_photo.setEnabled(true);

    }

    private void UIForGroupLeader() {
        ParentQRInfo_Scan.setEnabled(true);
        ParentQRInfo_photo.setEnabled(true);
        submitButton.setEnabled(true);
        submitButton.setBackgroundResource(R.drawable.shape_rectangle_radius_theme);
        //启用最开始禁用的拍照view
        ParentQRInfo_photo.setVisibility(View.VISIBLE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            //requestCode=0 扫码
            info = data.getStringExtra("result");
            info = recode(info);//扫码器返回的信息
            if (info != null && info != "") {
                /*先清除二维码信息框的文字*/
                parentQRInfo_Eidt.setText("");
                showProgremmer();//对应checkHasTheQRAndIsMachineHandler中的close
                checkHasTheQRAndIsMachine(info,sId);
            } else {
                Utils.showToast("未扫描");
            }
        } else if (requestCode == REQUST_TAKE_PHOTTO_CODE2 && resultCode == Activity.RESULT_OK) {
            //requestCode=2拍照
            if(data!=null){
                Bundle bundle = data.getExtras();
                    if (bundle != null) {
                    Bitmap bm = (Bitmap) bundle.get("data");

                    if (bm != null) {
                        //保存Bitmap图片到图库
                        switch (IMAGE) {
                            case 1:
                                ImageToGallery.saveImageToGallery(getApplicationContext(), bm);
                                photoPath[IMAGE - 1] = ImageToGallery.filePath;
                                photoName[IMAGE - 1] = fileName;
                                Glide.with(this).load(ImageToGallery.filePath).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                        //图片加载完成
                                        ParentQRInfo_photo.setImageBitmap(bitmap);
                                    }
                                });

                                break;
                            case 2:
                                ImageToGallery.saveImageToGallery(getApplicationContext(), bm);
                                photoPath[IMAGE - 1] = ImageToGallery.filePath;
                                photoName[IMAGE - 1] = fileName;
                                Glide.with(this).load(ImageToGallery.filePath).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                        //图片加载完成
                                        Location_photo.setImageBitmap(bitmap);
                                    }
                                });

                                break;
                            case 3:
                                ImageToGallery.saveImageToGallery(getApplicationContext(), bm);
                                photoPath[IMAGE - 1] = ImageToGallery.filePath;
                                photoName[IMAGE - 1] = fileName;
                                Glide.with(this).load(ImageToGallery.filePath).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                        //图片加载完成
                                        LackItem_photo.setImageBitmap(bitmap);
                                    }
                                });
                                break;
                            default:
                                break;
                        }

                        index[IMAGE - 1] = 1;
                    }
                }else{
                    index[IMAGE - 1] = 0;
                }
            }else{
                index[IMAGE - 1] = 0;
            }
        }
    }

    private void init() {
        submitButton = (Button) findViewById(R.id.SubmitButtom);

        Boxtype_spinner = (Spinner) findViewById(R.id.BoxType_Spinner);
        LackItemEdit = (EditText) findViewById(R.id.LackItem_EditText);
        LackItemEdit.setFocusable(false);
        LackItemEdit.setFocusableInTouchMode(false);

        BoxScenario1_spinner = (Spinner) findViewById(R.id.BoxScenario_Spinner1);//新造
        BoxScenario2_spinner = (Spinner) findViewById(R.id.BoxScenario_Spinner2);//统型
        YQtype_spinner = (Spinner) findViewById(R.id.YQType_Spinner);//充电器逆变器
        Location_spinner = (Spinner) findViewById(R.id.Location_Spinner);//位置

        Factory_Spinner = (Spinner) findViewById(R.id.Factory_Spinner);//厂
        FangAn_Spinner = (Spinner) findViewById(R.id.FangAn_Spinner);//方案，
        CarsType_Spinner = (Spinner) findViewById(R.id.CarsType_Spinner);//车型
        ChuXianType_Spinner = (Spinner) findViewById(R.id.ChuXianType_Spinner);//正反出线

        //整机明细
        belongsZhengji=(LinearLayout)findViewById(R.id.belongsZhengji);

    }

    private String checkInfoComplete() {

        ParentQRInfo = ((EditText) findViewById(R.id.ParentQRInfo_Eidt)).getText().toString().trim();

        BoxScenario1 = ((Spinner) findViewById(R.id.BoxScenario_Spinner1)).getSelectedItem().toString().trim();
        BoxScenario2 = ((Spinner) findViewById(R.id.BoxScenario_Spinner2)).getSelectedItem().toString().trim();
        YQType = ((Spinner) findViewById(R.id.YQType_Spinner)).getSelectedItem().toString().trim();
        Location = ((Spinner) findViewById(R.id.Location_Spinner)).getSelectedItem().toString().trim();
        BoxType = ((Spinner) findViewById(R.id.BoxType_Spinner)).getSelectedItem().toString().trim();
        LackItem = ((EditText) findViewById(R.id.LackItem_EditText)).getText().toString().trim();

        Factory = ((Spinner) findViewById(R.id.Factory_Spinner)).getSelectedItem().toString().trim();
        FangAn = ((Spinner) findViewById(R.id.FangAn_Spinner)).getSelectedItem().toString().trim();
        CarsType = ((Spinner) findViewById(R.id.CarsType_Spinner)).getSelectedItem().toString().trim();
        ChuXianType = ((Spinner) findViewById(R.id.ChuXianType_Spinner)).getSelectedItem().toString().trim();

        if (BoxScenario1 == null || BoxScenario1.equals("")) {
            return "err";
        } else if (BoxScenario2 == null || BoxScenario2.equals("")) {
            return "err";
        } else if (YQType == null || YQType.equals("")) {
            return "err";
        } else if (Location == null || Location.equals("")) {
            return "err";
        } else if (BoxType == null || BoxType.equals("")) {
            return "err";
        } else {
            if ((BoxType.equals("成品") || BoxType.equals("空箱体")) && BoxScenario2.equals("统型")) {
                if (ChuXianType == null || ChuXianType.equals("")) {
                    return "err";
                }
            } else if ((BoxType.equals("成品")) && BoxScenario2.equals("非统型")) {
                if (Factory == null || Factory.equals("")) {
                    return "err";
                }
                if (FangAn == null || FangAn.equals("")) {
                    return "err";
                }
                if (CarsType == null || CarsType.equals("")) {
                    return "err";
                }
            } else if (BoxType.equals("空箱体") && BoxScenario2.equals("非统型")) {
                if (Factory == null || Factory.equals("")) {
                    return "err";
                }
                if (FangAn == null || FangAn.equals("")) {
                    return "err";
                }
            } else if (BoxType.equals("待调试") && BoxScenario2.equals("统型")) {
                if (ChuXianType == null || ChuXianType.equals("")) {
                    return "err";
                }
            } else if (BoxType.equals("待调试") && BoxScenario2.equals("非统型")) {
                if (Factory == null || Factory.equals("")) {
                    return "err";
                }
                if (FangAn == null || FangAn.equals("")) {
                    return "err";
                }
                if (CarsType == null || CarsType.equals("")) {
                    return "err";
                }
            }
            if (ChuXianType.equals("")) {
                ChuXianType = null;
            }
            if (Factory.equals("")) {
                Factory = null;
            }
            if (FangAn.equals("")) {
                FangAn = null;
            }
            if (CarsType.equals("")) {
                CarsType = null;
            }

            //data1 =SkeletonQRInfo+"="+MachineQRInfo+"="+BoxScenario1+"="+BoxScenario2+"="+YQType+"="+Location+"="+BoxType+"="+LackItem+"="+ChuXianType+"="+Factory+"="+FangAn+"="+CarsType;
            data1 = ParentQRInfo + "=" + BoxScenario1 + "=" + BoxScenario2 + "=" + YQType + "=" + Location + "=" + BoxType + "=" + LackItem + "=" + ChuXianType + "=" + Factory + "=" + FangAn + "=" + CarsType;
            return data1;
        }
    }

    private boolean checkLackItem() {
        BoxType = ((Spinner) findViewById(R.id.BoxType_Spinner)).getSelectedItem().toString().trim();
        //是否是缺件品
        if (("待调试").equals(BoxType) || ("待总装").equals(BoxType)) {
            LackItem = ((EditText) findViewById(R.id.LackItem_EditText)).getText().toString().trim();
            if (LackItem.length() > 20) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    //获取图片名字
    public String getImagePath() {
        imageNames = photoName[0] + "," + photoName[1] + "," + photoName[2];
        return imageNames;
    }

    public void submitData(String s1, String s2, String s3, String s4) {
        DataObtainer.INSTANCE.sendWsiforBook(s1, s2, s3, s4,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean b, String s, Object o) {
                        if (o.equals("")) {
                            Utils.showToast("没有找到！");
                            return;
                        }
                        result1 = (String) o;
                        Message m = handler1.obtainMessage();
                        handler1.sendMessage(m);
                    }
                }

        );
    }

    public void submitData1(String ParentQRInfo, String sId, String imageNames) {
        DataObtainer.INSTANCE.submitDataForNotMachain(ParentQRInfo, sId, imageNames,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean b, String s, Object o) {
                        if (o.equals("")) {
                            Utils.showToast("没有找到！");
                            return;
                        }
                        result1 = (String) o;
                        Message m = handler1.obtainMessage();
                        handler1.sendMessage(m);
                    }
                }

        );
    }

    public void backDisplayMachineInfo(String QRInfi) {
        DataObtainer.INSTANCE.getotherInfoByMachineQRInfo(QRInfi,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        if (data.equals("") || data == null) {
                            Utils.showToast("您这是第一次录入此信息！");
                            return;
                        }
                        result2 = (String) data;
                        Message m = handler2.obtainMessage();
                        handler2.sendMessage(m);
                    }
                });
    }

    /* public void backDisplay2(String s1){
         DataObtainer.INSTANCE.getotherInfoByMachineQRInfo(s1,
                 new NetworkCallbacks.SimpleDataCallback() {
                     @Override
                     public void onFinish(boolean isSuccess, String msg, Object data) {
                         if (data.equals("")||data==null) {
                             Utils.showToast("您这是第一次录入此信息！");
                             return;
                         }
                         result2 = (String)data;
                         Message m = handler2.obtainMessage();
                         handler2.sendMessage(m);
                     }
                 });
     }*/
    //清除照片
    public void clearPhoto() {
        ParentQRInfo_photo.setImageResource(R.mipmap.takephoto);
        Location_photo.setImageResource(R.mipmap.takephoto);
        LackItem_photo.setImageResource(R.mipmap.takephoto);
        index[0] = 0;
        index[1] = 0;
        index[2] = 0;
    }

    //清除内容
    public void clearContent() {
        ((EditText) findViewById(R.id.ParentQRInfo_Eidt)).setText("");
        // ((EditText) findViewById(R.id.LackItem_EditText)).setText("");
    }

    private void setContent(String result) {
        if (!result.equals("null")) {
            content = result.split("=");
            showContents(content);
        } else {
            Toast.makeText(MainActivity.this, "您这是第一次录入数据！", Toast.LENGTH_SHORT).show();
        }
    }

    private void showContents(final String[] content) {
        if (content[7].equals("null")) {
            ChuXianType = "";
        }
        if (content[8].equals("null")) {
            Factory = "";
        }
        if (content[9].equals("null")) {
            FangAn = "";
        }
        if (content[10].equals("null")) {
            CarsType = "";
        }
        //SkeletonQRInfo_Eidt.setText(content[0]);
        parentQRInfo_Eidt.setText(content[0]);
        if ("新造".equals(content[1])) {
            BoxScenario1_spinner.setSelection(0);
        } else {
            BoxScenario1_spinner.setSelection(1);
        }

        if ("统型".equals(content[2])) {
            BoxScenario2_spinner.setSelection(0);
            Factory_Spinner.setEnabled(false);
            FangAn_Spinner.setEnabled(false);
            CarsType_Spinner.setEnabled(false);
            ChuXianType_Spinner.setEnabled(true);
            if (!"".equals(content[7])) {
                ChuXianType_Spinner.setEnabled(true);
                if ("正出线".equals(content[7])) {
                    ChuXianType_Spinner.setSelection(1);
                } else {
                    ChuXianType_Spinner.setSelection(2);
                }
            }

        } else {
            BoxScenario2_spinner.setSelection(1);
            Factory_Spinner.setEnabled(true);
            FangAn_Spinner.setEnabled(true);
            CarsType_Spinner.setEnabled(true);
            ChuXianType_Spinner.setEnabled(false);

            if ("长客厂".equals(content[8])) {
                Factory_Spinner.setSelection(1);
            } else if ("成都厂".equals(content[8])) {
                Factory_Spinner.setSelection(2);
            } else if ("广州厂".equals(content[8])) {
                Factory_Spinner.setSelection(3);
            } else if ("柳州厂".equals(content[8])) {
                Factory_Spinner.setSelection(4);
            } else if ("浦镇厂".equals(content[8])) {
                Factory_Spinner.setSelection(5);
            } else if ("四方厂".equals(content[8])) {
                Factory_Spinner.setSelection(6);
            }


            if ("浦镇方案".equals(content[9])) {
                FangAn_Spinner.setSelection(1);
            } else if ("唐山方案".equals(content[9])) {
                FangAn_Spinner.setSelection(2);
            } else if ("金轮号".equals(content[9])) {
                FangAn_Spinner.setSelection(3);
            }


            if ("YZ（硬座车）".equals(content[10])) {
                CarsType_Spinner.setSelection(1);
            } else if ("YW（硬卧车）".equals(content[10])) {
                CarsType_Spinner.setSelection(2);
            } else if ("RW（软卧车）".equals(content[10])) {
                CarsType_Spinner.setSelection(3);
            } else if ("CA（餐车）".equals(content[10])) {
                CarsType_Spinner.setSelection(4);
            } else if ("XL（行李车）".equals(content[10])) {
                CarsType_Spinner.setSelection(5);
            }

        }


        if ("充电器".equals(content[3])) {
            YQtype_spinner.setSelection(0);
        } else if ("逆变器".equals(content[3])) {
            YQtype_spinner.setSelection(1);
        }

        if ("三号楼电二".equals(content[4])) {
            Location_spinner.setSelection(0);
        } else if ("三号楼调试".equals(content[4])) {
            Location_spinner.setSelection(1);
        } else if ("一号楼一楼".equals(content[4])) {
            Location_spinner.setSelection(2);
        } else if ("一号楼三楼".equals(content[4])) {
            Location_spinner.setSelection(3);
        } else if ("老化".equals(content[4])) {
            Location_spinner.setSelection(4);
        } else if ("油漆房".equals(content[4])) {
            Location_spinner.setSelection(5);
        }


        if ("空箱体".equals(content[5])) {
            Boxtype_spinner.setSelection(0);
        } else if ("待调试".equals(content[5])) {
            Boxtype_spinner.setSelection(1);
        } else if ("成品".equals(content[5])) {
            Boxtype_spinner.setSelection(2);
        } else if ("待总装".equals(content[5])) {
            Boxtype_spinner.setSelection(3);
        } else if ("待发货".equals(content[5])) {
            Boxtype_spinner.setSelection(4);
        } else if ("发货".equals(content[5])) {
            Boxtype_spinner.setSelection(5);
        }
        LackItemEdit.setText(content[6]);
        Toast.makeText(MainActivity.this, "数据已经回显", Toast.LENGTH_SHORT).show();
    }


    //检测实时表是否已经有该数据,是否为整机
    private void checkHasTheQRAndIsMachine(String maIndfo,String loginId) {
        DataObtainer.INSTANCE.checkHasTheQRAndIsMachine(maIndfo,loginId,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        HasTheQRAndIsMachineString = (String) data;
                        Message m = checkHasTheQRAndIsMachineHandler.obtainMessage();
                        checkHasTheQRAndIsMachineHandler.sendMessage(m);
                    }
                });
    }
    /*public void checkSM2(String maIndfo){
        DataObtainer.INSTANCE.checkSaoMiao(maIndfo,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        result4= (String) data;
                        Message m = handler4.obtainMessage();
                        handler4.sendMessage(m);
                    }
                });
    }*/
   /* //检测二维码信息是否存在
    public void checkSM3(String maIndfo){
        DataObtainer.INSTANCE.checkSaoMiao2(maIndfo,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        result5= (String) data;
                        Message m = handler5.obtainMessage();
                        handler5.sendMessage(m);
                    }
                });
    }*/

    //用户的错误操作提示
    public void alertErrDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("操作错误提示!");
        builder.setMessage("您扫描的二维码不对，请按照要求进行对应操作，点击确定退出程序，重新扫描！");
        builder.setCancelable(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.create().show();
    }

    public void sweep(View view) {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.putExtra("autoEnlarged", true);
        startActivityForResult(intent, 0);
    }

    //解决乱码问题
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

    //清除本地的图片 提交后
    private void deleteImage() {
        for (int i = 0; i < photoPath.length; i++) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = getApplicationContext().getContentResolver();
            String where = MediaStore.Images.Media.DATA + "='" + photoPath[i] + "'";
            //删除图片
            mContentResolver.delete(uri, where, null);

             /*
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                   new MediaScanner(MainActivity.this,path);
           } else {
                 sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
             }*/
        }
    }

    public void getLoginRole(String sId) {
        showProgremmer();//对应getLoginRoleHandler中的close
        final String tempSid = sId;
        new Thread() {
            @Override
            public void run() {
                DataObtainer.INSTANCE.getLoginRole(tempSid,
                        new NetworkCallbacks.SimpleDataCallback() {
                            @Override
                            public void onFinish(boolean isSuccess, String msg, Object data) {
                                loginRole = (String) data;
                                Message m = getLoginRoleHandler.obtainMessage();
                                getLoginRoleHandler.sendMessage(m);
                            }
                        });
            }
        }.start();
    }

    //扫码的到确认是整机
    public void isMachineQRInfo() {
        isFull = true;
        //启用所有控件
        EnableAllWidget();
        //添加下拉列表内容
        BoxScenario1_list.add("新造");
        BoxScenario1_list.add("大修");
        BoxScenario2_list.add("统型");
        BoxScenario2_list.add("非统型");
        YQtype_list.add("充电器");
        YQtype_list.add("逆变器");
        Location_list.add("三号楼电二");
        Location_list.add("三号楼调试");
        Location_list.add("一号楼一楼");
        Location_list.add("一号楼三楼");
        Location_list.add("老化");
        Location_list.add("油漆房");
        Boxtype_list.add("空箱体");
        Boxtype_list.add("待调试");
        Boxtype_list.add("成品");
        Boxtype_list.add("待总装");
        Boxtype_list.add("待发货");
        Boxtype_list.add("发货");

        Factory_list.add("");
        Factory_list.add("长客厂");
        Factory_list.add("成都厂");
        Factory_list.add("广州厂");
        Factory_list.add("柳州厂");
        Factory_list.add("浦镇厂");
        Factory_list.add("四方厂");
        FangAn_list.add("");
        FangAn_list.add("浦镇方案");
        FangAn_list.add("唐山方案");
        FangAn_list.add("金轮号");
        CarsType_list.add("");
        CarsType_list.add("YZ（硬座车）");
        CarsType_list.add("YW（硬卧车）");
        CarsType_list.add("RW（软卧车）");
        CarsType_list.add("CA（餐车）");
        CarsType_list.add("XL（行李车）");
        ChuXianType_list.add("");
        ChuXianType_list.add("正出线");
        ChuXianType_list.add("反出线");

        //样式为原安卓里面有的android.R.layout.simple_spinner_item，让这个数组适配器装list内容。
        BoxScenario1_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, BoxScenario1_list);
        BoxScenario2_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, BoxScenario2_list);
        YQtype_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, YQtype_list);
        Location_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Location_list);
        Boxtype_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Boxtype_list);
        Factory_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Factory_list);
        FangAn_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FangAn_list);
        CarsType_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CarsType_list);
        ChuXianType_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ChuXianType_list);

        //为适配器设置下拉菜单样式。adapter.setDropDownViewResource
        BoxScenario1_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BoxScenario2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YQtype_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Location_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Boxtype_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Factory_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        FangAn_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CarsType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ChuXianType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //
        BoxScenario1_spinner.setAdapter(BoxScenario1_adapter);
        BoxScenario2_spinner.setAdapter(BoxScenario2_adapter);
        YQtype_spinner.setAdapter(YQtype_adapter);
        Location_spinner.setAdapter(Location_adapter);
        Boxtype_spinner.setAdapter(Boxtype_adapter);

        Factory_Spinner.setAdapter(Factory_adapter);
        FangAn_Spinner.setAdapter(FangAn_adapter);
        CarsType_Spinner.setAdapter(CarsType_adapter);
        ChuXianType_Spinner.setAdapter(ChuXianType_adapter);

        //禁用非统型的三个框
        Factory_Spinner.setEnabled(false);
        FangAn_Spinner.setEnabled(false);
        CarsType_Spinner.setEnabled(false);

        //箱体状态设置监听事件
        Boxtype_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //如果点击半成品
                if (Boxtype_adapter.getItem(arg2).equals("待调试") || Boxtype_adapter.getItem(arg2).equals("待总装")) {
                    //设置编辑框可以用
                    if (!temp_LackItemString.equals("")) {
                        LackItemEdit.setText(temp_LackItemString);
                    }
                    LackItemEdit.setFocusable(true);
                    LackItemEdit.setFocusableInTouchMode(true);
                } else {
                    //设置编辑框禁用
                    if (!("").equals(LackItemEdit.getText().toString())) {
                        temp_LackItemString = LackItemEdit.getText().toString().trim();
                    }
                    LackItemEdit.setText("");

                    LackItemEdit.setFocusable(false);
                    LackItemEdit.setFocusableInTouchMode(false);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        //统型、非统型
        BoxScenario2_spinner.setOnItemSelectedListener(
                new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        //统型
                        if (BoxScenario2_adapter.getItem(arg2).equals("统型")) {
                            Factory_Spinner.setEnabled(false);
                            FangAn_Spinner.setEnabled(false);
                            CarsType_Spinner.setEnabled(false);
                            ChuXianType_Spinner.setEnabled(true);

                            Factory_Spinner.setSelection(0);
                            FangAn_Spinner.setSelection(0);
                            CarsType_Spinner.setSelection(0);
                            ChuXianType_Spinner.setSelection(0);

                        } else if (BoxScenario2_adapter.getItem(arg2).equals("非统型")) {
                            //非统型
                            Factory_Spinner.setEnabled(true);
                            //Factory_Spinner.setBackgroundColor(0xFFFFFF);
                            FangAn_Spinner.setEnabled(true);
                            //Factory_Spinner.setBackgroundColor(0xFFFFFF);
                            CarsType_Spinner.setEnabled(true);
                            //CarsType_Spinner.setBackgroundColor(0xFFFFFF);
                            ChuXianType_Spinner.setEnabled(false);
                            //ChuXianType_Spinner.setBackgroundColor(0xB3B3B3);

                            Factory_Spinner.setSelection(0);
                            FangAn_Spinner.setSelection(0);
                            CarsType_Spinner.setSelection(0);
                            ChuXianType_Spinner.setSelection(0);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                }

        );

    }

    public void submitMachineData(String MachineData, String LoginId, String imageNames, String parentQRInfo) {
        DataObtainer.INSTANCE.submitMachineData(MachineData, LoginId, imageNames, parentQRInfo,
                new NetworkCallbacks.SimpleDataCallback() {
                    @Override
                    public void onFinish(boolean isSuccess, String msg, Object data) {
                        result3 = (String) data;
                        Message m = handler3.obtainMessage();
                        handler3.sendMessage(m);
                    }
                });
    }

    private void showProgremmer(){//显示进度条
        loading = new Loading_view(this,R.style.CustomDialog);
        loading.show();
    }
    private void closeProgremmer(){//关闭进度条
        loading.dismiss();
    }

}

