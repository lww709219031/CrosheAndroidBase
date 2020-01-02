/**安徽创息软件科技有限公司版权所有 http://www.croshe.com**/
package com.croshe.android.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

    static final AtomicInteger ac=new AtomicInteger();


    private static boolean isMatch(String regex, String orginal){
        if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(orginal);
        return isNum.matches();
    }

    /**
     * 是否是正整数
     * @param orginal
     * @return
     */
    public static boolean isPositiveInteger(String orginal) {
        return isMatch("^\\+{0,1}[1-9]\\d*", orginal);
    }

    /**
     * 是否是负整数
     * @param orginal
     * @return
     */
    public static boolean isNegativeInteger(String orginal) {
        return isMatch("^-[1-9]\\d*", orginal);
    }

    /**
     * 是否是数字，不包含小数点
     * @param orginal
     * @return
     */
    public static boolean isWholeNumber(String orginal) {
        return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
    }

    /**
     * 是否是含有小数点的正数字
     * @param orginal
     * @return
     */
    public static boolean isPositiveDecimal(String orginal){
        return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal);
    }

    /**
     * 是否是含有小数点的负数字
     * @param orginal
     * @return
     */
    public static boolean isNegativeDecimal(String orginal){
        return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal);
    }

    /**
     * 是否是含有小数点的数字
     * @param orginal
     * @return
     */
    public static boolean isDecimal(String orginal){
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    /**
     * 是否是数字
     * @param orginal
     * @return
     */
    public static boolean isRealNumber(String orginal){
        return isWholeNumber(orginal) || isDecimal(orginal);
    }



    /**
     * 字符转数字
     * @param value
     * @return
     */
    public static Number formatToNumber(final Object value){
        return formatToNumber(value,0);
    }


    /**
     * 字符转数字
     * @param value
     * @return
     */
    public static Number formatToNumber(final Object value,final Number defaultValue){
        try {
            if(value==null||value.toString().length()==0) return defaultValue;
            Number number=new Number() {
                private static final long serialVersionUID = 1L;
                String numberStr="";
                @Override
                public long longValue() {
                    try {
                        if(value!=null){
                            numberStr=value.toString().replace(" ", "");
                        }
                        if(isDecimal(numberStr)){
                            return (long) Float.parseFloat(numberStr);
                        }else{
                            return Long.parseLong(numberStr);
                        }
                    } catch (Exception e) {
                        return defaultValue.longValue();
                    }
                }

                @Override
                public int intValue() {
                    try {
                        if(value!=null){
                            numberStr=value.toString().replace(" ", "");
                        }
                        if(isDecimal(numberStr)){
                            return (int) Float.parseFloat(numberStr);
                        }else{
                            return Integer.parseInt(numberStr);
                        }
                    } catch (Exception e) {
                        return defaultValue.intValue();
                    }
                }

                @Override
                public float floatValue() {
                    try {
                        if(value!=null){
                            numberStr=value.toString().replace(" ", "");
                        }
                        return Float.parseFloat(numberStr);
                    } catch (Exception e) {
                        return defaultValue.floatValue();
                    }
                }

                @Override
                public double doubleValue() {
                    try {
                        if(value!=null){
                            numberStr=value.toString().replace(" ", "");
                        }
                        return Double.parseDouble(numberStr);
                    } catch (Exception e) {
                        return defaultValue.doubleValue();
                    }
                }
            };
            return number;
        } catch (Exception e) {
            return 0;
        }
    }


    public static int formatToInt(Object value){
        return formatToNumber(value).intValue();
    }


    public static int formatToInt(Object value,int defaultValue){
        return formatToNumber(value,defaultValue).intValue();
    }


    public static float formatToFloat(Object value){
        return formatToNumber(value).floatValue();
    }

    public static float formatToFloat(Object value,float defaultValue){
        return formatToNumber(value,defaultValue).floatValue();
    }


    public static double formatToDouble(Object value){
        return formatToNumber(value).doubleValue();
    }

    public static double formatToDouble(Object value,double defaultValue){
        return formatToNumber(value,defaultValue).doubleValue();
    }


    public static float formatToFloat(Object value,int digit){
        return formatToNumber(numberFormat(formatToFloat(value),digit)).floatValue();
    }


    public static double formatToDouble(Object value,int digit){
        return formatToNumber(numberFormat(formatToDouble(value),digit)).doubleValue();
    }

    public static long formatToLong(Object value){
        return formatToNumber(value).longValue();
    }

    public static long formatToLong(Object value,long defaultValue){
        return formatToNumber(value, defaultValue).longValue();
    }



    /**
     * 获取字符串中的所有数字
     * @param content
     * @return
     */
    public static String getAllNumbers(String content) {
        List<String> numbers = new ArrayList<String>();
        for(String n:content.replaceAll("[^0-9]", ",").split(",")){
            if (n.length()>0)
                numbers.add(n);
        }
        return StringUtils.join(numbers,"");
    }


    /**
     * 数字格式化
     * @param value
     * @return
     */
    public static String numberFormat(Object value,int digit){
        String pattern="0.";
        String zStr="";
        for (int i = 0; i < digit; i++) {
            zStr+="0";
        }
        DecimalFormat df = new DecimalFormat(pattern+zStr);
        return df.format(value);
    }



    /**
     * 数字格式化
     * @param value
     * @param pattern 规则 例如 ###.00、###,###.0
     * @return
     */
    public static String numberFormat(Object value,String pattern){
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(value);
    }

    /**
     * 比较三个数的大小
     * @param num1
     * @param num2
     * @param num3
     * @return
     */
    public static int compareNumbers(Object num1,Object num2,Object num3){
        return Math.max(Math.max(Integer.parseInt(num1.toString()), Integer.parseInt(num2.toString())),Integer.parseInt(num3.toString()));
    }



    /**
     * 计算需要显示的页码
     * @param currPage 当前页
     * @param totalPage 总页数
     * @param visibleCount 显示页数数量
     * @return
     */
    public static List<Integer> computePages(int currPage,int totalPage,int visibleCount){
        int leftCount=visibleCount/2;
        List<Integer> pages=new ArrayList<Integer>();
        if(currPage<=leftCount+1){
            if(visibleCount>totalPage){
                for (int i = 1; i <= totalPage; i++) {
                    pages.add(i);
                }
            }else{
                for (int i = 1; i <= visibleCount; i++) {
                    pages.add(i);
                }
            }
        }else{
            for (int i = currPage-leftCount; i <= currPage+leftCount; i++) {
                if(i<=totalPage){
                    pages.add(i);
                }
            }

            if(pages.size()<visibleCount){
                int span=visibleCount-pages.size();
                for (int i = 0; i < span; i++) {
                    pages.add(0, pages.get(0)-1);
                }
            }
        }
        return pages;
    }


    /**
     * 计算百分比后的数字
     */
    public static float formatToPercentage(Object value){
        return NumberUtils.formatToFloat(value)/100;
    }


    private static Number formatToNumber2(double value,int digit){
        try {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(digit);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            return nf.parse(nf.format(value));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 生成唯一标示的数字
     */
    public static int buildOnlyNumber() {
        if (ac.get() > 999999999) {
            ac.set(1);
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHH");
        int time = formatToInt(sdf.format(new Date()));
        int returnValue = time + ac.incrementAndGet();
        return returnValue;
    }



    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("0.0000");
        System.out.println(df.format(134552342.45699));
    }
}  

