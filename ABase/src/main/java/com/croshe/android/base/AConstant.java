package com.croshe.android.base;

/**
 * 常量配置类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/21 16:40.
 */
public class AConstant {


    public static final String EXTRA_TITLE = "EXTRA_TITLE";


    public static final String EXTRA_DO_ACTION = "EXTRA_DO_ACTION";

    public static final String EXTRA_DO_DATA = "EXTRA_DATA";

    public static final String EXTRA_DO_MAP_DATA = "EXTRA_DO_MAP_DATA";


    /**
     * 弹框Alert
     */
    public static final String ACTION_ALERT = "ACTION_ALERT";


    /**
     * 显示等待框
     */
    public static final String ACTION_SHOW_PROGRESS = "ACTION_SHOW_PROGRESS";


    /**
     * 关闭等待框
     */
    public static final String ACTION_HIDE_PROGRESS = "ACTION_HIDE_PROGRESS";


    /**
     * 推送的动作：聊天消息
     */
    public static final String ACTION_CHAT_MESSAGE = "ACTION_CHAT_MESSAGE";


    /**
     * 推送的动作：从群组中移除
     */
    public static final String ACTION_REMOVE_GROUP = "ACTION_REMOVE_GROUP";


    /**
     * 推送的动作：解散群组
     */
    public static final String ACTION_DISSOLVE_GROUP = "ACTION_DISSOLVE_GROUP";


    /**
     * 设置极光推送的标签
     */
    public static final String ACTION_SET_TAG = "ACTION_SET_TAG";


    /**
     * 移除极光推送的标签
     */
    public static final String ACTION_REMOVE_TAG = "ACTION_REMOVE_TAG";


    /**
     * 选择支付方式
     */
    public static final String ACTION_CHOOSE_PAY = "ACTION_CHOOSE_PAY";


    /**
     * 刷新动态未读的消息
     */
    public static final String ACTION_REFRESH_DYNAMIC_UN_READ = "ACTION_REFRESH_DYNAMIC_UN_READ";

    /**
     * 刷新新的朋友未读的消息
     */
    public static final String ACTION_REFRESH_CONTACT_UN_READ = "ACTION_REFRESH_CONTACT_UN_READ";



    /**
     * 检测action
     *
     * @param action
     * @param enumClass
     * @return
     */
    public static boolean checkAction(String action, Class enumClass) {
        if (enumClass == null) return false;
        return action.equals(enumClass.getSimpleName());
    }


    public enum BaseEnum {

    }

    /**
     * 基本功能枚举
     */
    public enum BaseFunctionEnum {
        浏览器网页转发,
        相册识别二维码,
        图片分享
    }


    /**
     * 浏览器activity的action
     */
    public enum CrosheBrowserActivity {
        /**
         * 网页路径 String
         */
        EXTRA_URL
    }


    /**
     * 查看图片activity的action
     */
    public enum CrosheShowImageActivity {
        /**
         * 图片路径集合，String[]
         */
        EXTRA_IMAGES_PATH,

        EXTRA_IMAGES_DETAIL,

        /**
         * 缩率图片路径集合，String[]
         */
        EXTRA_THUMB_IMAGES_PATH,

        /**
         * 默认显示的第一张路径，String
         */
        EXTRA_FIRST_PATH,

        /**
         * 默认显示第一张索引，int
         */
        EXTRA_FIRST_INDEX,

        /**
         * 图片地址的前缀，String
         */
        EXTRA_SCHEME,

        /**
         * 图片的文件名集合 String[]
         */
        EXTRA_FILE_NAME
    }


    /**
     * 录音相册
     */
    public enum CrosheVoiceAlbumActivity {
        /**
         * 选择的上线
         */
        EXTRA_MAX_SELECT,
        /**
         * 音频的时长 int
         */
        RESULT_VOICE_DURATION,


        /**
         * 音频的路径 String
         */
        RESULT_VOICE_PATH,


        /**
         * 音频的描述 String
         */
        RESULT_VOICE_DETAILS,

        /**
         * 音频的时长 ArrayList<Integer>
         */
        RESULT_VOICE_DURATIONS,


        /**
         * 音频的路径 String[]
         */
        RESULT_VOICE_PATHS,
    }

    /**
     * 剪切图片activity的action
     */
    public enum CrosheCropImageActivity {
        /**
         * 图片路径，String
         */
        EXTRA_IMAGE_PATH,

        /**
         * 剪裁宽度，int
         */
        EXTRA_IMAGE_WIDTH,

        /**
         * 剪裁高度，int
         */
        EXTRA_IMAGE_HEIGHT,

        /**
         * 图片保存质量，int(1-100)
         */
        EXTRA_IMAGE_QUALITY,

        /**
         * 是否自由剪裁，boolean
         */
        EXTRA_CROP_FREE,

        /**
         * 剪裁后的返回结果 String
         */
        RESULT_IMAGE_PATH
    }


    /**
     * 相册activity
     */
    public enum CrosheAlbumActivity {
        /**
         * 图片选择的上限 int
         */
        EXTRA_MAX_SELECT,

        /**
         * 是否包含视频 boolean
         */
        EXTRA_VIDEO,

        /**
         * 只拍照
         */
        EXTRA_JUST_CAMERA,

        /**
         * 返回的图片路径 String[]
         */
        RESULT_IMAGES_PATH,

        /**
         * 返回单个图片  String
         */
        RESULT_SINGLE_IMAGES_PATH,

        /**
         * 返回的视频路径 String[]
         */
        RESULT_VIDEO_PATH,

        /**
         * 返回单个视频  String
         */
        RESULT_SINGLE_VIDEO_PATH
    }


    /**
     * 视频录制
     */
    public enum CrosheRecordVideoActivity {
        /**
         * 视频录制保存的路径 String
         */
        EXTRA_RECORD_VIDEO_PATH,

        /**
         * 视频录制文字最大大小 int
         */
        EXTRA_RECORD_MAX_SIZE,

        /**
         * 视频录制最长时间 int
         */
        EXTRA_RECORD_MAX_TIME,

        /**
         * 录制返回的路径
         */
        RESULT_VIDEO_PATH,

        /**
         * 录制的时长 毫秒
         */
        RESULT_VIDEO_DURATION,

        /**
         * 视频第一帧的图片路径
         */
        RESULT_PHOTO_PATH,
    }

    /**
     * 本地通讯录activity
     */

    public enum CrosheLocalContactActivity {

    }


    /**
     * 好友详情activity
     */
    public enum CrosheUserInfoActivity {
        /**
         * 好友编号,String
         */
        EXTRA_USER_CODE
    }


    /**
     * 群聊天界面
     */
    public enum CrosheChatGroupInfoActivity {
        /**
         * 群编号
         */
        EXTRA_GROUP_CODE
    }


    /**
     * 群详情界面
     */
    public enum CrosheChatGroupDetailsActivity {
        /**
         * 群编号
         */
        EXTRA_GROUP_CODE
    }


    /**
     * 打开群创建
     */
    public enum CrosheChatGroupCreateActivity {
        /**
         * 群组标签
         */
        EXTRA_TAG
    }

    /**
     * 单聊天界面
     */
    public enum CrosheChatContactActivity {
        /**
         * 好友编号,String
         */
        EXTRA_USER_CODE
    }


    /**
     * 会话activity
     */
    public enum CrosheChatActivity {
        /**
         * 会话Id，支持单人，多人、群聊,String
         */
        EXTRA_CONVERSATION_ID,

        /**
         * 会话类型 0 单聊 1群聊 2聊天室 ，int
         */
        EXTRA_CHAT_TYPE
    }


    /**
     * 转发activity
     */
    public enum CrosheForwardActivity {
        /**
         * 待转发的消息,
         */
        EXTRA_MESSAGE,

        /**
         * 多个消息
         */
        EXTRA_MULTI_MESSAGE
    }


    /**
     * 打开加好友界面
     */
    public enum CrosheContactAddActivity {
        /**
         * 用户编号
         */
        EXTRA_USER_CODE,

        /**
         * 是否默认单向好友
         */
        EXTRA_JUST_CONTACT
    }


    /**
     * 地图中查看位置
     */
    public enum CrosheLocationInMapActivity {
        /**
         * 纬度 double
         */
        EXTRA_POI_LAT,

        /**
         * 纬度 double
         */
        EXTRA_POI_LNG,

        /**
         * 位置 String
         */
        EXTRA_POI_ADDR

    }


    /**
     * 在地图中选择位置
     */
    public enum CrosheSelectLocationInMap {

        /**
         * 是否允许拖拽改变位置
         */
        EXTRA_CHANGE_ADDRESS,
        /**
         * 回调的位置 com.amap.api.services.core.PoiItem
         */
        RESULT_POI_ITEM,

        /**
         * 返回位置的Json字符串 String
         */
        RESULT_POI_ENTITY,
        /**
         * 返回位置
         */
        RESULT_ADDRESS,
        /**
         * 纬度 latitude double
         */
        RESULT_LAT,

        /**
         * 经度longitude double
         */
        RESULT_LNG
    }


    /**
     * 下载记录
     */
    public enum CrosheDownListActivity {

    }


    /**
     * 添加好友
     */
    public enum AddContactActivity {

    }

    public enum CrosheContactNewActivity{

    }

    /**
     * 扫一扫
     */
    public enum CrosheScannerActivity {

        /**
         * 自动检测扫码结果，默认为false
         */
        EXTRA_AUTO_CHECK_RESUlT,

        /**
         * 扫一扫返回的结果 String
         */
        RETURN_SCANNER
    }

    /**
     * 群成员界面
     */
    public enum CrosheChatGroupUsersActivity {
        /**
         * 群组编号
         */
        EXTRA_GROUP_CODE,
        /**
         * 不允许操作的角色，int[]
         */
        EXTRA_READ_ONLY_ROLE,
        /**
         * 是否进行选择操作 boolean
         */
        EXTRA_CHECK,
        /**
         * 返回选中的用户编号 String[]
         */
        RESULT_USER_CODE,
        /**
         * 返回选中的用户昵称String[]
         */
        RESULT_USER_NAME
    }


    /**
     * 我的好友
     */
    public enum CrosheContactActivity {

        /**
         * 群组编号，设置后代表排除已加入该群组的好友
         */
        EXTRA_CGROUP_CODE,

        /**
         * 默认选中的好友
         */
        EXTRA_CHECKED,

        /**
         * 单选操作
         */
        EXTRA_SINGLE_CHECK,

        /**
         * 多选操作
         */
        EXTRA_MULTI_CHECK,

        /**
         * 返回已选择的好友编号 String[]
         */
        RESULT_CHECKED_USER_CODE,

        /**
         * 返回已选择的好友昵称 String[]
         */
        RESULT_CHECKED_USER_NAME,

        /**
         * 返回已选择的好友头像 String[]
         */
        RESULT_CHECKED_USER_HEAD,
        /**
         * 返回已选择的好友实体json信息 ChatContactEntity[]
         */
        RESULT_CHECKED_USER
    }



    /**
     * 选择我的好友
     */
    public enum CrosheSelectContactActivity {

        /**
         * 群组编号，设置后代表排除已加入该群组的好友 String
         */
        EXTRA_CGROUP_CODE,

        /**
         * 默认选中的好友编号 String[]
         */
        EXTRA_CHECKED,

        /**
         * 是否是单选
         */
        EXTRA_SINGLE_CHECK,

        /**
         * 返回已选择的好友编号 String[]
         */
        RESULT_CHECKED_USER_CODE,

        /**
         * 返回已选择的好友昵称 String[]
         */
        RESULT_CHECKED_USER_NAME,

        /**
         * 返回已选择的好友头像 String[]
         */
        RESULT_CHECKED_USER_HEAD,

        /**
         * 返回已选择的好友实体json信息 JSON[]
         */
        RESULT_CHECKED_USER
    }


    /**
     * 选择群组
     */
    public enum CrosheSelectChatGroupActivity {
        /**
         * 默认选中的群组 String
         */
        EXTRA_CHECKED,

        /**
         * 返回已选择的群组编号 String[]
         */
        RESULT_CHECKED_GROUP_CODE,


        /**
         * 返回已选择的群组昵称 String[]
         */
        RESULT_CHECKED_GROUP_NAME,

        /**
         * 返回已选择的群组头像 String[]
         */
        RESULT_CHECKED_GROUP_HEAD
    }


    /**
     * 群组列表
     */
    public enum CrosheChatGroupActivity {
        /**
         * 群组标签
         */
        EXTRA_TAG
    }


    /**
     * 获得收藏
     */
    public enum CrosheCollectionActivity {
        /**
         * 会话的Id String
         */
        EXTRA_CONVERSATION_ID
    }


    /**
     * 投诉聊天
     */
    public enum CrosheComplainChatActivity {
        /**
         * 会话编号 String
         */
        EXTRA_CONVERSATION_ID
    }


    /**
     * 生成并查看二维码
     */
    public enum CrosheShowQRCodeActivity {

        /**
         * 二维码内容
         */
        EXTRA_QR_CONTENT,

        /**
         * 二维码logo
         */
        EXTRA_QR_LOGO,

        /**
         * 二维码提示标题
         */
        EXTRA_QR_TITLE,

        /**
         * 二维码提示子标题
         */
        EXTRA_QR_SUBTITLE
    }


    /**
     * 分享
     */
    public enum CrosheShareActivity {
        /**
         * 分享的数据  ShareEntity
         */
        EXTRA_SHARE_DATA
    }


    /**
     * 红包
     */
    public enum CrosheRedActivity {
        /**
         * 发红包类型  0 好友 1 群
         */
        EXTRA_TYPE,
        /**
         * 群用户统计
         */
        EXTRA_USER_COUNT,
        /**
         * 返回的红包信息 RedEntity
         */
        RESULT_DATA,

        /**
         * 发红包的动作
         */
        ACTION_SEND_RED

    }

    /**
     * 红包领取
     */
    public enum CrosheRedTake{

        /**
         * 红包Id
         */
        EXTRA_RED_ID,

        /**
         * 是否是多人红包
         */
        EXTRA_MULTI_RED,

        /**
         * 是否隐藏领红包界面的头部信息
         */
        EXTRA_HIDE_HEAD,

        /**
         * 红包领取的监听，OnCrosheRedTakeListener
         */
        EXTRA_RED_TAKE_LISTENER
    }

    /**
     * 红包详情
     */
    public enum CrosheRedDetailsActivity {
        /**
         * 红包的ID
         */
        EXTRA_RED_ID,
        /**
         * 红包的实体数据 RedEntity
         */
        EXTRA_DATA

    }

    /**
     * 红包记录
     */
    public enum CrosheRedRecordActivity {
    }



    /**
     * 新消息提醒
     */
    public enum CrosheMessageNoticeSetActivity {

    }

    /**
     * 勿扰模式
     */
    public enum CrosheMessageSilenceActivity {

    }

    /**
     * 聊天设置
     */
    public enum CrosheMessageChatSetActivity {

    }


    /**
     * 本地通讯录
     */
    public enum CrosheLocalConactActivity{

    }


    /**
     * 黑名单列表
     */
    public enum CrosheBlackActivity{

    }


    /**
     * 发布动态
     */
    public enum CrosheAddDynamicActivity{

        /**
         * 发布成功回调的数据
         */
        RESULT_DYNAMIC
    }


    /**
     * 动态列表
     */
    public enum CrosheDynamicListActivity{

    }


    /**
     * 动态详情
     */
    public enum CrosheDynamicDetailsActivity{

        /**
         * 动态Id
         */
        EXTRA_DYNAMIC_ID
    }


    /**
     * 用户发布的动态
     */
    public enum CrosheUserDynamicActivity{
        /**
         * 用户Id
         */
        EXTRA_USER_ID
    }


    /**
     * 动态消息
     */
    public enum CrosheDynamicNotifyActivity{

    }


    /**
     * 发起转账
     */
    public enum CrosheForwardMoneyActivity{
        /**
         * 目标用户code
         */
        EXTRA_USER_CODE,

        /**
         * 返回的转账信息
         */
        RESULT_FORWARD,
        /**
         * 监听对象 OnCrosheForwardMoneyListener
         */
        EXTRA_FORWARD_LISTENER
    }


    /**
     * 查看转账或领取转账
     */
    public enum CrosheForwardMoneyDetailsActivity{
        EXTRA_FORWARD_ID,
        /**
         * 监听对象 OnCrosheForwardMoneyListener
         */
        EXTRA_FORWARD_LISTENER
    }

    /**
     * 语音通话
     */
    public enum CrosheCallVoiceActivity{
        EXTRA_USER_CODE,
        EXTRA_USER_NAME,
        EXTRA_USER_HEAD_IMG,
        EXTRA_IS_IN_COMING_CALL
    }

    /**
     * 视频通话
     */
    public enum CrosheCallVideoActivity{
        EXTRA_USER_CODE,
        EXTRA_USER_NAME,
        EXTRA_USER_HEAD_IMG,
        EXTRA_IS_IN_COMING_CALL
    }


    /**
     * 余额提现
     */
    public enum CrosheTakeMoneyActivity{

        /**
         * 默认选择的索引
         */
        EXTRA_SELECT_INDEX
    }

    /**
     * 余额充值
     */
    public enum CroshePayInMoneyActivity{
        /**
         * 默认选择的索引
         */
        EXTRA_SELECT_INDEX
    }


    /**
     * 富文本编辑器
     */
    public enum CrosheRichEditorActivity{
        /**
         * 输入提醒
         */
        EXTRA_PLACE_HOLDER,
        /**
         * 默认内容
         */
        EXTRA_DEFAULT_CONTENT,
        /**
         * 返回的网页内容
         */
        RESULT_CONTENT
    }

}

