package com.croshe.android.base.views.list;

import com.croshe.android.base.entity.dao.AppCacheEntity;
import com.croshe.android.base.entity.dao.AppCacheHelper;
import com.croshe.android.base.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/9/8 17:06.
 */
public class PageListHelper {

    private String code;
    private List allList;
    private boolean isReplace;

    public static PageListHelper getInstance(String code, List sourceData) {
        PageListHelper pageListHelper = new PageListHelper();
        pageListHelper.code = code;
        pageListHelper.allList = sourceData;
        return pageListHelper;
    }
    
    private PageListHelper(){
        
    }

    /**
     *  追加list
     * @param page
     * @param data
     * @return
     */
    public PageListHelper putList(int page, List data) {
        if (allList != null) {
            int startIndex = getStartIndex(page);
            if (startIndex >= 0) {
                int toIndex = startIndex + getPageSize(page);
                if (startIndex > allList.size()) {
                    startIndex = allList.size();
                }
                List leftList = allList.subList(0, startIndex);

                List rightList = new ArrayList<>();

                if (toIndex < allList.size()) {
                    rightList = allList.subList(toIndex, allList.size());
                }

                List newData = new ArrayList<>();
                if (leftList.size() > 0) {
                    newData.addAll(leftList);
                }
                newData.addAll(data);
                if (rightList.size() > 0) {
                    newData.addAll(rightList);
                }

                allList.clear();
                allList.addAll(newData);
                isReplace = true;
            }else{
                allList.addAll(data);
                isReplace = false;
            }
            setPageSize(page, data.size());
        }

        return this;
    }


    public int getStartIndex(int page) {
        int size=0;
        for (int i = 1; i < page; i++) {
            size+= getPageSize(i);
        }
        return size;
    }


    public int getPageSize(int page) {
        AppCacheEntity cacheEntity = AppCacheHelper.getCache(code + "PageSize" + page);
        if (cacheEntity != null) {
            return NumberUtils.formatToInt(cacheEntity.getCacheContent());
        }
        return 0;
    }

    public void setPageSize(int page, int index) {
        AppCacheHelper.setCache(code, code + "PageSize" + page, String.valueOf(index));
    }

    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean replace) {
        isReplace = replace;
    }
}
