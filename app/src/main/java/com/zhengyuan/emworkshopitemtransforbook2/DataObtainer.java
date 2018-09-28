package com.zhengyuan.emworkshopitemtransforbook2;

import android.net.ConnectivityManager;

import com.zhengyuan.baselib.constants.Constants;
import com.zhengyuan.baselib.listener.NetworkCallbacks;
import com.zhengyuan.baselib.utils.xml.Element;
import com.zhengyuan.baselib.xmpp.ChatUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import com.zhengyuan.baselib.listener.NetworkCallbacks.SimpleDataCallback;

/**
 * Created by zy on 2018/7/25.
 */

public enum DataObtainer {
    INSTANCE;
    private final String LOG_TAG = "DataObtainer";
        private final String TAG = "EMWorkshopItemTransforBook2";

    public void sendWsiforBook(String data, String sname, String url, String kujia, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestWsiforBook");
        element.addProperty("info", data);
        element.addProperty("number", sname);
        element.addProperty("uri", url);
        element.addProperty("kujia", kujia);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnWsiforBook",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void getotherInfoBySkeletonQR(String SkeletonQRInfo, final SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestOtherInfoBySkeletonQR");
        element.addProperty("SkeletonQRInfo", SkeletonQRInfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnOtherInfoBySkeletonQR", new NetworkCallbacks.MessageListenerThinner() {
            public void processMessage(Element element, Message message, Chat chat) {
                boolean isSuccess = element.getBody() != null && !element.getBody().equals("");
                callback.onFinish(isSuccess, "", element.getProperty("messages"));

            }
        });
    }

    //根据整机二维码获取信息
    public void getotherInfoByMachineQRInfo(String MachineQRInfo, final SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestOtherInfoByMachineQRInfo2");
        element.addProperty("MachineQRInfo", MachineQRInfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnOtherInfoByMachineQRInfo2", new NetworkCallbacks.MessageListenerThinner() {
            public void processMessage(Element element, Message message, Chat chat) {
                boolean isSuccess = element.getBody() != null && !element.getBody().equals("");
                callback.onFinish(isSuccess, "", element.getProperty("messages"));

            }
        });
    }

    public void checkSaoMiao(String mainfo, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestCheckSaoMa");
        element.addProperty("info", mainfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnCheckSaoMa",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    //检测标题处的扫码信息
    public void checkSaoMiao2(String mainfo, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestCheckSaoMa2");
        element.addProperty("info", mainfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnCheckSaoMa2",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void MaInfoDisplay(String Idinfo, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestMaInfoDisplay");
        element.addProperty("info", Idinfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnMaInfoDisplay",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void getImagesById(String s1, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestGetImagesById");
        element.addProperty("info", s1);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnGetImagesById",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////林亮code

    public void getLoginRole(String sId, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestGetLoginRole");
        element.addProperty("info", sId);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnGetLoginRole",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    //检测二维码是否是整机(功能被checkHasTheQRAndIsMachine取代了)
    public void checkIsMachain(String mainfo, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestCheckIsMachain");
        element.addProperty("info", mainfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnCheckIsMachain",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void checkHasTheQRAndIsMachine(String mainfo, String loginId, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestCheckHasTheQRAndIsMachine");
        //element.addProperty("type", "requestTestOpenQuery");
        element.addProperty("info", mainfo);
        element.addProperty("loginId", loginId);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnCheckHasTheQRAndIsMachine",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void submitDataForNotMachain(String ParentQRInfo, String sId, String imageNames,
                                        final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestSubmitDataForNotMachain");
        element.addProperty("info", ParentQRInfo);
        element.addProperty("loginId", sId);
        element.addProperty("imageNames", imageNames);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnSubmitDataForNotMachain",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void submitMachineData(String MachineData, String LoginId, String imageNames, String parentQRInfo, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestSubmitMachineData");
        element.addProperty("info", MachineData);
        element.addProperty("loginId", LoginId);
        element.addProperty("imageNames", imageNames);
        element.addProperty("parentQRInfo", parentQRInfo);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnSubmitMachineData",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    public void getNoFittingItemInfo(String parentQRInfo, String loginId, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestGetNoFittingItemInfo");
        element.addProperty("parentQRInfo", parentQRInfo);
        element.addProperty("loginId", loginId);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnGetNoFittingItemInfo",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("itemsNameAndId"));
                    }
                });
    }

    /**
     * @param subItems
     * @param parentQRInfo
     * @param loginId
     * @param callback
     */
    public void submitMaInfoForSubItems(String subItems, String parentQRInfo, String loginId, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestSubmitMaInfoForSubItems");
        element.addProperty("subItems", subItems);
        element.addProperty("parentQRInfo", parentQRInfo);
        element.addProperty("loginId", loginId);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnSubmitMaInfoForSubItems",
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }

    /**
     * 获取货架上已有物件的信息
     * @param shelfInfo
     * @param mLoginUserId
     * @param callback
     */
    public void getShelfSubItems(String shelfInfo, String mLoginUserId, final NetworkCallbacks.SimpleDataCallback callback) {
        Element element = new Element("mybody");
        element.addProperty("type", "requestGetShelfSubItems" + TAG);
        element.addProperty("shelfInfo", shelfInfo);
        element.addProperty("loginUserId", mLoginUserId);
        ChatUtils.INSTANCE.sendMessage(Constants.CHAT_TO_USER, element.toString(), "returnGetShelfSubItems" + TAG,
                new NetworkCallbacks.MessageListenerThinner() {
                    @Override
                    public void processMessage(Element element, Message message, Chat chat) {
                        boolean isSuccess = element.getBody() != null &&
                                !element.getBody().equals("");
                        callback.onFinish(isSuccess, "", element.getProperty("result"));
                    }
                });
    }
}
