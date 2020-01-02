package com.croshe.android.base;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.croshe.android.base.entity.dao.DaoMaster;
import com.croshe.android.base.entity.dao.DaoSession;
import com.croshe.android.base.utils.BaseAppUtils;

import org.greenrobot.greendao.database.Database;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Janesen on 2017/6/24.
 */

public class BaseApplication extends MultiDexApplication {

    public static Context GlobalContext;

    /**
     * 创息应用的AppId
     */
    public static int Croshe_APP_Id = -1;

    public static Set<AConstant.BaseFunctionEnum> functions = new HashSet<>();
    static{
        functions.add(AConstant.BaseFunctionEnum.相册识别二维码);
        functions.add(AConstant.BaseFunctionEnum.图片分享);
        functions.add(AConstant.BaseFunctionEnum.浏览器网页转发);
    }

    public static DaoSession daoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext = this;
        BaseAppUtils.memoryDataInfo = getSharedPreferences("localData", 0);
        BaseApplication.initDataBase(this);
        MultiDex.install(this);
    }


    public static void initDataBase(Context context) {
        try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "croshe-db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        } catch (Exception e) {}
    }


    /**
     * 添加功能
     *
     * @param baseFunctionEnum
     */
    public void addBaseFunction(AConstant.BaseFunctionEnum... baseFunctionEnum) {
        for (AConstant.BaseFunctionEnum functionEnum : baseFunctionEnum) {
            functions.add(functionEnum);
        }
    }


    /**
     * 移除功能
     * @param baseFunctionEnum
     */
    public void removeBaseFunction(AConstant.BaseFunctionEnum... baseFunctionEnum) {
        for (AConstant.BaseFunctionEnum functionEnum : baseFunctionEnum) {
            functions.remove(functionEnum);
        }
    }

    /**
     * 检测功能
     * @param baseFunctionEnum
     * @return
     */
    public static boolean checkBaseFunction(AConstant.BaseFunctionEnum baseFunctionEnum) {
        return functions.contains(baseFunctionEnum);
    }



}
