/**安徽创息软件科技有限公司版权所有 http://www.croshe.com**/
package com.croshe.android.base.utils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串操作类
 * @author Janesen
 *
 */
public class SelfStrUtils {

	/**
	 * 拼音集合
	 */
	protected static LinkedHashMap<String, Integer> spells=new LinkedHashMap<String, Integer>();
	static{
		spells.put("a", -20319);
		spells.put("ai", -20317);
		spells.put("an", -20304);
		spells.put("ang", -20295);
		spells.put("ao", -20292);
		spells.put("ba", -20283);
		spells.put("bai", -20265);
		spells.put("ban", -20257);
		spells.put("bang", -20242);
		spells.put("bao", -20230);
		spells.put("bei", -20051);
		spells.put("ben", -20036);
		spells.put("beng", -20032);
		spells.put("bi", -20026);
		spells.put("bian", -20002);
		spells.put("biao", -19990);
		spells.put("bie", -19986);
		spells.put("bin", -19982);
		spells.put("bing", -19976);
		spells.put("bo", -19805);
		spells.put("bu", -19784);
		spells.put("ca", -19775);
		spells.put("cai", -19774);
		spells.put("can", -19763);
		spells.put("cang", -19756);
		spells.put("cao", -19751);
		spells.put("ce", -19746);
		spells.put("ceng", -19741);
		spells.put("cha", -19739);
		spells.put("chai", -19728);
		spells.put("chan", -19725);
		spells.put("chang", -19715);
		spells.put("chao", -19540);
		spells.put("che", -19531);
		spells.put("chen", -19525);
		spells.put("cheng", -19515);
		spells.put("chi", -19500);
		spells.put("chong", -19484);
		spells.put("chou", -19479);
		spells.put("chu", -19467);
		spells.put("chuai", -19289);
		spells.put("chuan", -19288);
		spells.put("chuang", -19281);
		spells.put("chui", -19275);
		spells.put("chun", -19270);
		spells.put("chuo", -19263);
		spells.put("ci", -19261);
		spells.put("cong", -19249);
		spells.put("cou", -19243);
		spells.put("cu", -19242);
		spells.put("cuan", -19238);
		spells.put("cui", -19235);
		spells.put("cun", -19227);
		spells.put("cuo", -19224);
		spells.put("da", -19218);
		spells.put("dai", -19212);
		spells.put("dan", -19038);
		spells.put("dang", -19023);
		spells.put("dao", -19018);
		spells.put("de", -19006);
		spells.put("deng", -19003);
		spells.put("di", -18996);
		spells.put("dian", -18977);
		spells.put("diao", -18961);
		spells.put("die", -18952);
		spells.put("ding", -18783);
		spells.put("diu", -18774);
		spells.put("dong", -18773);
		spells.put("dou", -18763);
		spells.put("du", -18756);
		spells.put("duan", -18741);
		spells.put("dui", -18735);
		spells.put("dun", -18731);
		spells.put("duo", -18722);
		spells.put("e", -18710);
		spells.put("en", -18697);
		spells.put("er", -18696);
		spells.put("fa", -18526);
		spells.put("fan", -18518);
		spells.put("fang", -18501);
		spells.put("fei", -18490);
		spells.put("fen", -18478);
		spells.put("feng", -18463);
		spells.put("fo", -18448);
		spells.put("fou", -18447);
		spells.put("fu", -18446);
		spells.put("ga", -18239);
		spells.put("gai", -18237);
		spells.put("gan", -18231);
		spells.put("gang", -18220);
		spells.put("gao", -18211);
		spells.put("ge", -18201);
		spells.put("gei", -18184);
		spells.put("gen", -18183);
		spells.put("geng", -18181);
		spells.put("gong", -18012);
		spells.put("gou", -17997);
		spells.put("gu", -17988);
		spells.put("gua", -17970);
		spells.put("guai", -17964);
		spells.put("guan", -17961);
		spells.put("guang", -17950);
		spells.put("gui", -17947);
		spells.put("gun", -17931);
		spells.put("guo", -17928);
		spells.put("ha", -17922);
		spells.put("hai", -17759);
		spells.put("han", -17752);
		spells.put("hang", -17733);
		spells.put("hao", -17730);
		spells.put("he", -17721);
		spells.put("hei", -17703);
		spells.put("hen", -17701);
		spells.put("heng", -17697);
		spells.put("hong", -17692);
		spells.put("hou", -17683);
		spells.put("hu", -17676);
		spells.put("hua", -17496);
		spells.put("huai", -17487);
		spells.put("huan", -17482);
		spells.put("huang", -17468);
		spells.put("hui", -17454);
		spells.put("hun", -17433);
		spells.put("huo", -17427);
		spells.put("ji", -17417);
		spells.put("jia", -17202);
		spells.put("jian", -17185);
		spells.put("jiang", -16983);
		spells.put("jiao", -16970);
		spells.put("jie", -16942);
		spells.put("jin", -16915);
		spells.put("jing", -16733);
		spells.put("jiong", -16708);
		spells.put("jiu", -16706);
		spells.put("ju", -16689);
		spells.put("juan", -16664);
		spells.put("jue", -16657);
		spells.put("jun", -16647);
		spells.put("ka", -16474);
		spells.put("kai", -16470);
		spells.put("kan", -16465);
		spells.put("kang", -16459);
		spells.put("kao", -16452);
		spells.put("ke", -16448);
		spells.put("ken", -16433);
		spells.put("keng", -16429);
		spells.put("kong", -16427);
		spells.put("kou", -16423);
		spells.put("ku", -16419);
		spells.put("kua", -16412);
		spells.put("kuai", -16407);
		spells.put("kuan", -16403);
		spells.put("kuang", -16401);
		spells.put("kui", -16393);
		spells.put("kun", -16220);
		spells.put("kuo", -16216);
		spells.put("la", -16212);
		spells.put("lai", -16205);
		spells.put("lan", -16202);
		spells.put("lang", -16187);
		spells.put("lao", -16180);
		spells.put("le", -16171);
		spells.put("lei", -16169);
		spells.put("leng", -16158);
		spells.put("li", -16155);
		spells.put("lia", -15959);
		spells.put("lian", -15958);
		spells.put("liang", -15944);
		spells.put("liao", -15933);
		spells.put("lie", -15920);
		spells.put("lin", -15915);
		spells.put("ling", -15903);
		spells.put("liu", -15889);
		spells.put("long", -15878);
		spells.put("lou", -15707);
		spells.put("lu", -15701);
		spells.put("lv", -15681);
		spells.put("luan", -15667);
		spells.put("lue", -15661);
		spells.put("lun", -15659);
		spells.put("luo", -15652);
		spells.put("ma", -15640);
		spells.put("mai", -15631);
		spells.put("man", -15625);
		spells.put("mang", -15454);
		spells.put("mao", -15448);
		spells.put("me", -15436);
		spells.put("mei", -15435);
		spells.put("men", -15419);
		spells.put("meng", -15416);
		spells.put("mi", -15408);
		spells.put("mian", -15394);
		spells.put("miao", -15385);
		spells.put("mie", -15377);
		spells.put("min", -15375);
		spells.put("ming", -15369);
		spells.put("miu", -15363);
		spells.put("mo", -15362);
		spells.put("mou", -15183);
		spells.put("mu", -15180);
		spells.put("na", -15165);
		spells.put("nai", -15158);
		spells.put("nan", -15153);
		spells.put("nang", -15150);
		spells.put("nao", -15149);
		spells.put("ne", -15144);
		spells.put("nei", -15143);
		spells.put("nen", -15141);
		spells.put("neng", -15140);
		spells.put("ni", -15139);
		spells.put("nian", -15128);
		spells.put("niang", -15121);
		spells.put("niao", -15119);
		spells.put("nie", -15117);
		spells.put("nin", -15110);
		spells.put("ning", -15109);
		spells.put("niu", -14941);
		spells.put("nong", -14937);
		spells.put("nu", -14933);
		spells.put("nv", -14930);
		spells.put("nuan", -14929);
		spells.put("nue", -14928);
		spells.put("nuo", -14926);
		spells.put("o", -14922);
		spells.put("ou", -14921);
		spells.put("pa", -14914);
		spells.put("pai", -14908);
		spells.put("pan", -14902);
		spells.put("pang", -14894);
		spells.put("pao", -14889);
		spells.put("pei", -14882);
		spells.put("pen", -14873);
		spells.put("peng", -14871);
		spells.put("pi", -14857);
		spells.put("pian", -14678);
		spells.put("piao", -14674);
		spells.put("pie", -14670);
		spells.put("pin", -14668);
		spells.put("ping", -14663);
		spells.put("po", -14654);
		spells.put("pu", -14645);
		spells.put("qi", -14630);
		spells.put("qia", -14594);
		spells.put("qian", -14429);
		spells.put("qiang", -14407);
		spells.put("qiao", -14399);
		spells.put("qie", -14384);
		spells.put("qin", -14379);
		spells.put("qing", -14368);
		spells.put("qiong", -14355);
		spells.put("qiu", -14353);
		spells.put("qu", -14345);
		spells.put("quan", -14170);
		spells.put("que", -14159);
		spells.put("qun", -14151);
		spells.put("ran", -14149);
		spells.put("rang", -14145);
		spells.put("rao", -14140);
		spells.put("re", -14137);
		spells.put("ren", -14135);
		spells.put("reng", -14125);
		spells.put("ri", -14123);
		spells.put("rong", -14122);
		spells.put("rou", -14112);
		spells.put("ru", -14109);
		spells.put("ruan", -14099);
		spells.put("rui", -14097);
		spells.put("run", -14094);
		spells.put("ruo", -14092);
		spells.put("sa", -14090);
		spells.put("sai", -14087);
		spells.put("san", -14083);
		spells.put("sang", -13917);
		spells.put("sao", -13914);
		spells.put("se", -13910);
		spells.put("sen", -13907);
		spells.put("seng", -13906);
		spells.put("sha", -13905);
		spells.put("shai", -13896);
		spells.put("shan", -13894);
		spells.put("shang", -13878);
		spells.put("shao", -13870);
		spells.put("she", -13859);
		spells.put("shen", -13847);
		spells.put("sheng", -13831);
		spells.put("shi", -13658);
		spells.put("shou", -13611);
		spells.put("shu", -13601);
		spells.put("shua", -13406);
		spells.put("shuai", -13404);
		spells.put("shuan", -13400);
		spells.put("shuang", -13398);
		spells.put("shui", -13395);
		spells.put("shun", -13391);
		spells.put("shuo", -13387);
		spells.put("si", -13383);
		spells.put("song", -13367);
		spells.put("sou", -13359);
		spells.put("su", -13356);
		spells.put("suan", -13343);
		spells.put("sui", -13340);
		spells.put("sun", -13329);
		spells.put("suo", -13326);
		spells.put("ta", -13318);
		spells.put("tai", -13147);
		spells.put("tan", -13138);
		spells.put("tang", -13120);
		spells.put("tao", -13107);
		spells.put("te", -13096);
		spells.put("teng", -13095);
		spells.put("ti", -13091);
		spells.put("tian", -13076);
		spells.put("tiao", -13068);
		spells.put("tie", -13063);
		spells.put("ting", -13060);
		spells.put("tong", -12888);
		spells.put("tou", -12875);
		spells.put("tu", -12871);
		spells.put("tuan", -12860);
		spells.put("tui", -12858);
		spells.put("tun", -12852);
		spells.put("tuo", -12849);
		spells.put("wa", -12838);
		spells.put("wai", -12831);
		spells.put("wan", -12829);
		spells.put("wang", -12812);
		spells.put("wei", -12802);
		spells.put("wen", -12607);
		spells.put("weng", -12597);
		spells.put("wo", -12594);
		spells.put("wu", -12585);
		spells.put("xi", -12556);
		spells.put("xia", -12359);
		spells.put("xian", -12346);
		spells.put("xiang", -12320);
		spells.put("xiao", -12300);
		spells.put("xie", -12120);
		spells.put("xin", -12099);
		spells.put("xing", -12089);
		spells.put("xiong", -12074);
		spells.put("xiu", -12067);
		spells.put("xu", -12058);
		spells.put("xuan", -12039);
		spells.put("xue", -11867);
		spells.put("xun", -11861);
		spells.put("ya", -11847);
		spells.put("yan", -11831);
		spells.put("yang", -11798);
		spells.put("yao", -11781);
		spells.put("ye", -11604);
		spells.put("yi", -11589);
		spells.put("yin", -11536);
		spells.put("ying", -11358);
		spells.put("yo", -11340);
		spells.put("yong", -11339);
		spells.put("you", -11324);
		spells.put("yu", -11303);
		spells.put("yuan", -11097);
		spells.put("yue", -11077);
		spells.put("yun", -11067);
		spells.put("za", -11055);
		spells.put("zai", -11052);
		spells.put("zan", -11045);
		spells.put("zang", -11041);
		spells.put("zao", -11038);
		spells.put("ze", -11024);
		spells.put("zei", -11020);
		spells.put("zen", -11019);
		spells.put("zeng", -11018);
		spells.put("zha", -11014);
		spells.put("zhai", -10838);
		spells.put("zhan", -10832);
		spells.put("zhang", -10815);
		spells.put("zhao", -10800);
		spells.put("zhe", -10790);
		spells.put("zhen", -10780);
		spells.put("zheng", -10764);
		spells.put("zhi", -10587);
		spells.put("zhong", -10544);
		spells.put("zhou", -10533);
		spells.put("zhu", -10519);
		spells.put("zhua", -10331);
		spells.put("zhuai", -10329);
		spells.put("zhuan", -10328);
		spells.put("zhuang", -10322);
		spells.put("zhui", -10315);
		spells.put("zhun", -10309);
		spells.put("zhuo", -10307);
		spells.put("zi", -10296);
		spells.put("zong", -10281);
		spells.put("zou", -10274);
		spells.put("zu", -10270);
		spells.put("zuan", -10262);
		spells.put("zui", -10260);
		spells.put("zun", -10256);
		spells.put("zuo", -10254);
	}

	/**
	 * 根据ASCII码取得拼音
	 * @param ascii
	 * @return
	 */
	public static String getSpellByAscii(int ascii) {
		if (ascii > 0 && ascii < 160) { //单字符
			return String.valueOf((char) ascii);
		}
		if (ascii < -20319 || ascii > -10247) { //不知道的字符
			return null;
		}
		Set<String> keySet = spells.keySet();
		Iterator<String> it = keySet.iterator();
		String spell0 = null;
		String spell = null;
		int asciiRang0 = -20319;
		int asciiRang;
		while (it.hasNext()) {
			spell = (String) it.next();
			Object valObj = spells.get(spell);
			if (valObj instanceof Integer) {
				asciiRang = ((Integer) valObj).intValue();
				if (ascii >= asciiRang0 && ascii < asciiRang) { //区间找到
					return (spell0 == null) ? spell : spell0;
				} else {
					spell0 = spell;
					asciiRang0 = asciiRang;
				}
			}
		}
		return null;
	}

	/**
	 * 获得单个汉字的Ascii.
	 * @param cn char
	 * 汉字字符
	 * @return int
	 * 错误返回 0,否则返回ascii
	 */
	public static int getCnAscii(char cn) {
		try {
			byte[] bytes = (String.valueOf(cn)).getBytes("gb2312");
			//System.out.println(cn);
			if (bytes == null || bytes.length > 2 || bytes.length <= 0) { //错误
				return 0;
			}
			if (bytes.length == 1) { //英文字符
				return bytes[0];
			}
			if (bytes.length == 2) { //中文字符
				int hightByte = 256 + bytes[0];
				int lowByte = 256 + bytes[1];
				int ascii = (256 * hightByte + lowByte) - 256 * 256;
				return ascii;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0; //错误
	}

	/**
	 * 将中文转换成汉语拼音
	 * @param chinese
	 * @return
	 */
	public static String chineseToSpell(String chinese){ 
		char[] chars=chinese.toCharArray();
		StringBuffer sb=new StringBuffer();
		for(char cr : chars){
			int ascii=getCnAscii(cr);
			String spell=getSpellByAscii(ascii);
			if(spell!=null){
				sb.append(spell);
			}
		}
		return sb.toString();
	}


	private final static int[] li_SecPosValue =
		{
		1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472,
		3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590
		};
	private final static String[] lc_FirstLetter =
		{
		"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p",
		"q", "r", "s", "t", "w", "x", "y", "z"
		};

	/**
	 * 取得给定汉字串的首字母串,即声母串
	 * @param str 给定汉字串
	 * @return 声母串
	 */
	public static String getAllFirstLetter(String str)
	{
		if (str == null || str.trim().length() == 0)
		{
			return "";
		}

		String _str = "";
		for (int i = 0; i < str.length(); i++)
		{
			_str = _str + getFirstLetter(str.substring(i, i + 1));
		}

		return _str;
	}

	/**
	 * 取得给定汉字的首字母,即声母
	 * @param chinese 给定的汉字
	 * @return 给定汉字的声母
	 */
	public static String getFirstLetter(String chinese)
	{
		if (chinese == null || chinese.trim().length() == 0)
		{
			return "";
		}
		chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

		if (chinese.length() > 1) //判断是不是汉字
		{
			int li_SectorCode = (int) chinese.charAt(0); //汉字区码
			int li_PositionCode = (int) chinese.charAt(1); //汉字位码
			li_SectorCode = li_SectorCode - 160;
			li_PositionCode = li_PositionCode - 160;
			int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; //汉字区位码
			if (li_SecPosCode > 1600 && li_SecPosCode < 5590)
			{
				for (int i = 0; i < 23; i++)
				{
					if (li_SecPosCode >= li_SecPosValue[i] &&
							li_SecPosCode < li_SecPosValue[i + 1])
					{
						chinese = lc_FirstLetter[i];
						break;
					}
				}
			}
			else //非汉字字符,如图形符号或ASCII码
			{
				chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
				chinese = chinese.substring(0,1);
			}
		}

		return chinese;
	}

	/**
	 * 字符串编码转换
	 * @param str 要转换编码的字符串
	 * @param charsetName 原来的编码
	 * @param toCharsetName 转换后的编码
	 * @return 经过编码转换后的字符串
	 */
	@Deprecated
	private static String conversionStr(String str, String charsetName,String toCharsetName)
	{
		try
		{
			str = new String(str.getBytes(charsetName), toCharsetName);
		}
		catch (Exception ex)
		{
			//System.out.println("字符串编码转换异常：" + ex.getMessage());
		}

		return str;
	}

	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String firstCharToUpper(String str){
		if(Character.isLowerCase(str.charAt(0))){
			char[] array = str.toCharArray();
			array[0] -= 32;
			return String.valueOf(array);
		}
		return str;
	}

	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String firstCharToLower(String str){
		if(Character.isUpperCase(str.charAt(0))){
			char[] array = str.toCharArray();
			array[0] += 32;
			return String.valueOf(array);
		}
		return str;
	}

	/**
	 * 插入字符串
	 * @param string
	 * @param position
	 * @param insert
	 * @return
	 */
	public static String insertStr(String string ,int position,String insert){
		if(position<0){
			return string+insert;
		}
		String char1=string.substring(0,position);
		String char2=string.substring(position);
		return char1+insert+char2;
	}


	/**
	 * 插入字符串
	 * @param string
	 * @param split
	 * @param insert
	 * @return
	 */
	public static String insertStr(String string ,String split,String insert){
		return insertStr(string, string.indexOf(split), insert);
	}


    /**
     * 替换指定位置的字符
     * @param sourceString
     * @param start
     * @param end
     * @param replaceString
     * @return
     */
    public static String replace(String sourceString,int start, int end, String replaceString) {
        if(start>sourceString.length()){
            start=sourceString.length();
        }

        if(end>sourceString.length()){
            end=sourceString.length();
        }
        String string1 = sourceString.substring(0, start);
        String string2 = sourceString.substring(end);
        return string1+replaceString+string2;
    }


    /**
     * 替换字符
     * @param sourceString
     * @param replaceChar
     * @return
     */
    public static String replaceChar(String sourceString,char replaceChar){
        List<String> chars=new ArrayList<>();
        for (int i = 0; i < sourceString.length(); i++) {
            chars.add(String.valueOf(replaceChar));
        }
        return StringUtils.join(chars,"");
    }


    /**
     * 替换字符
     * @param sourceString
     * @param replaceChar
     * @return
     */
    public static String replaceChar(String sourceString,String replaceChar){
        List<String> chars=new ArrayList<>();
        for (int i = 0; i < sourceString.length(); i++) {
            chars.add(replaceChar);
        }
        return StringUtils.join(chars,"");
    }


    /**
	 * 格式化用户手机号码
	 * @param userPhone
	 * @return
	 */
	public static String formatePhone(String userPhone) {
		if (userPhone != null) {
			if(userPhone.length()<7){
				return userPhone.replaceAll(userPhone.substring(3), "****");
			}
			userPhone = userPhone.replaceAll(userPhone.substring(3, 7), "****");
			return userPhone;
		}
		return userPhone;
	}



	/**
	 * 格式化日期，
	 * @param dateTime
	 * @return
	 */
	public static String formatDateTime(String dateTime){
		if(dateTime==null||dateTime.equals("null"))return "";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Calendar cd=Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(dateTime));

			long minu=(System.currentTimeMillis()-cd.getTimeInMillis())/1000/60;

			if(minu==0){
				return "刚刚";
			}

			if(minu<60&&minu>0){

				return minu+"分钟前";
			}else{
				if(minu<12*60&&minu>0){//12个小时内
					return minu/60+"小时"+minu%60+"分钟前";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTime;
	}


    /**
     * 格式化日期，
     * @param dateTime
     * @return
     */
    public static String formatDate(String dateTime){
        if(dateTime==null||dateTime.equals("null"))return "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar cd=Calendar.getInstance();
            cd.setTime(sdf.parse(dateTime));
            Calendar now=Calendar.getInstance();
            if(cd.get(Calendar.YEAR)==now.get(Calendar.YEAR)){
                long subDate=cd.get(Calendar.DAY_OF_YEAR)-now.get(Calendar.DAY_OF_YEAR);
                if(subDate==0){
                    return "今天";
                }else if(subDate==-1){
                    return "昨天";
                }else if(subDate==-2){
                    return "前天";
                }else if(subDate==1){
                    return "明天";
                }else if(subDate==2){
                    return "后天";
                }
            }
            return sdf.format(sdf.parse(dateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }


    /**
     * 格式化日期，
     * @param dateTime
     * @return
     */
    public static String formatDateMinute(String dateTime){
        if(dateTime==null||dateTime.equals("null"))return "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm");
            Calendar cd=Calendar.getInstance();
            cd.setTime(sdf.parse(dateTime));
            Calendar now=Calendar.getInstance();

            if(cd.get(Calendar.YEAR)==now.get(Calendar.YEAR)){
                long subDate=cd.get(Calendar.DAY_OF_YEAR)-now.get(Calendar.DAY_OF_YEAR);
                if(subDate==0){
                    return "今天 "+sdf2.format(cd.getTime());
                }else if(subDate==-1){
                    return "昨天 "+sdf2.format(cd.getTime());
                }else if(subDate==-2){
                    return "前天 "+sdf2.format(cd.getTime());
                }else if(subDate==1){
                    return "明天 "+sdf2.format(cd.getTime());
                }else if(subDate==2){
                    return "后天 "+sdf2.format(cd.getTime());
                }
            }
            return sdf.format(sdf.parse(dateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }



    /**
	 * 将中文的转成拼音并且每个汉字拼音的首字母大写
	 * @param chinese
	 * @return
	 */
	public static String chineseToFirstSpellToUpper(String chinese){ 
		char[] chars=chinese.toCharArray();

		StringBuffer sb=new StringBuffer();
		for(char cr : chars){
			int ascii=getCnAscii(cr);
			String spell=getSpellByAscii(ascii);
			if(spell!=null){
				sb.append(firstCharToUpper(spell));
			}
		}

		return sb.toString();
	}



	/**
	 * 格式化身份证
	 * @return
	 */
	public static String formateIDCard(String idCard) {
		if (idCard != null) {
			if(idCard.length()>=10){
				String hideStr=idCard.substring(4, idCard.length()-6);
				String placeholder="";
				for (int i = 0; i < hideStr.length(); i++) {
					placeholder+="*";
				}
				idCard = idCard.replaceAll(hideStr, placeholder);
			}
		}
		return idCard;
	}

	
	/**
	 * 转换为汉字拼音，只获得首字母
	 * @param value
	 * @return
	 */
	public static String convertToFirstPinyin(String value){
		String pinyin=PinyinHelper.convertToPinyinString(value, ",", PinyinFormat.WITHOUT_TONE);
		String first="";
		String[] array=pinyin.split(",");
		for (String string : array) {
            if (string.length() >= 1) {
                first+=string.substring(0,1);
            }
		}
		return first;
	}
	
	
	/**
	 * 转换为汉字拼音
	 * @param value
	 * @return
	 */
	public static String convertToPinyin(String value){
        String pinyin= PinyinHelper.convertToPinyinString(value, "", PinyinFormat.WITHOUT_TONE);
		return pinyin;
	}
	

	

	/**
	 * 生成随机字符串
	 * @return
	 */
	public static String randomStr(){
		Random random=new Random();
		return MD5Encrypt.MD5(System.currentTimeMillis()+random.nextInt(99999)+"");
	}
	
	
	
	static final AtomicInteger ac=new AtomicInteger();
	/**
	 * 根据时间 生成唯一编号
	 * @return
	 */
	public static synchronized String makeOnlyCode(String prefix){
		if(StringUtils.isEmpty(prefix)){
			prefix="";
		}
		if(ac.get()==10){
			ac.set(0);
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");//精确到毫秒
		return prefix+sdf.format(new Date())+ac.incrementAndGet();		
	}
	
	
	
	public static String defaultValue(Object object,String defaulValue){
		if(object==null||object.toString().length()==0){
			return defaulValue;
		}
		return object.toString();
	}

    public static String formatNumber(Object number,int length){
        String patter = "";
        for (int i = 0; i < length; i++) {
            patter += "0";
        }
        return new DecimalFormat(patter).format(number);
    }


    /**
     */
    public static boolean isPhoneLegal(String str) {
        String regExp = "1(3|4|5|6|7|8)[0-9]{9}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}


