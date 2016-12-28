

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BanedKeywordServiceImpl {
	 @SuppressWarnings("rawtypes")
	 
	 public interface BanedKeywordService1 {
			/**
			 * add baned keyword, it is used by backend
			 * @param word
			 * @return
			 */
			boolean addBanedKeyword(String word);
			/**
			 * remove baned keyword, it is used by backend
			 * @param word
			 * @return
			 */
			boolean removeBanedKeyword(String word);
			/**
			 * 
			 * @return
			 */
			List<String> getAllWord();
		}

	    private Map sensitiveWordMap = null;
	    public static int minMatchTYpe = 1; // 最小匹配规则
	    public static int maxMatchType = 2; // 最大匹配规则

	    /**
	     * 构造函数，初始化敏感词库
	     */
	    public BanedKeywordServiceImpl() {
	        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
	    }

	    /**
	     * 判断文字是否包含敏感字符
	     * 
	     * @param txt
	     *            文字
	     * @param matchType
	     *            匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
	     * @return 若包含返回true，否则返回false
	     * @version 1.0
	     */
	    public boolean isContaintSensitiveWord(String txt, int matchType) {
	        boolean flag = false;
	        for (int i = 0; i < txt.length(); i++) {
	            int matchFlag = this.CheckSensitiveWord(txt, i, matchType); // 判断是否包含敏感字符
	            if (matchFlag > 0) { // 大于0存在，返回true
	                flag = true;
	            }
	        }
	        return flag;
	    }

	    /**
	     * 获取文字中的敏感词
	     * 
	     * @param txt
	     *            文字
	     * @param matchType
	     *            匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
	     * @return
	     * @version 1.0
	     */
	    public Set<String> getSensitiveWord(String txt, int matchType) {
	        Set<String> sensitiveWordList = new HashSet<String>();
	        List<String> list = new ArrayList<String>();
	              
	        Collections.addAll(list);//填充            
	        sensitiveWordList.addAll(list);//给set填充         
	        list.clear();//清空list，不然下次把set元素加入此list的时候是在原来的基础上追加元素的         
	        list.addAll(sensitiveWordList);//把set的   

	        for (int i = 0; i < txt.length(); i++) {
	            int length = CheckSensitiveWord(txt, i, matchType); // 判断是否包含敏感字符
	            if (length > 0) { // 存在,加入list中
	                sensitiveWordList.add(txt.substring(i, i + length));
	                i = i + length - 1; // 减1的原因，是因为for会自增
	            }
	        }

	        return sensitiveWordList;
	    }

	    /**
	     * 替换敏感字字符
	     * 
	     * @param txt
	     * @param matchType
	     * @param replaceChar
	     *            替换字符，默认*
	     * @version 1.0
	     */
	    public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
	        String resultTxt = txt;
	        Set<String> set = getSensitiveWord(txt, matchType); // 获取所有的敏感词
	        List<String> list = new ArrayList<String>();
            
	        Collections.addAll(list);//填充            
	        set.addAll(list);//给set填充         
	        list.clear();//清空list，不然下次把set元素加入此list的时候是在原来的基础上追加元素的         
	        list.addAll(set);//把set的  
	        Iterator<String> iterator = set.iterator();
	        String word = null;
	        String replaceString = null;
	        while (iterator.hasNext()) {
	            word = iterator.next();
	            replaceString = getReplaceChars(replaceChar, word.length());
	            resultTxt = resultTxt.replaceAll(word, replaceString);
	        }

	        return resultTxt;
	    }

	    /**
	     * 获取替换字符串
	     * 
	     * @param replaceChar
	     * @param length
	     * @return
	     * @version 1.0
	     */
	    private String getReplaceChars(String replaceChar, int length) {
	        String resultReplace = replaceChar;
	        for (int i = 1; i < length; i++) {
	            resultReplace += replaceChar;
	        }

	        return resultReplace;
	    }

	    /**
	     * 检查文字中是否包含敏感字符，检查规则如下：<br>
	     * 
	     * @param txt
	     * @param beginIndex
	     * @param matchType
	     * @return，如果存在，则返回敏感词字符的长度，不存在返回0
	     * @version 1.0
	     */
	    @SuppressWarnings({ "rawtypes" })
	    public int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
	        boolean flag = false; // 敏感词结束标识位：用于敏感词只有1位的情况
	        int matchFlag = 0; // 匹配标识数默认为0
	        char word = 0;
	        Map nowMap = sensitiveWordMap;
	        for (int i = beginIndex; i < txt.length(); i++) {
	            word = txt.charAt(i);
	            nowMap = (Map) nowMap.get(word); // 获取指定key
	            if (nowMap != null) { // 存在，则判断是否为最后一个
	                matchFlag++; // 找到相应key，匹配标识+1
	                if ("1".equals(nowMap.get("isEnd"))) { // 如果为最后一个匹配规则,结束循环，返回匹配标识数
	                    flag = true; // 结束标志位为true
	                    if (BanedKeywordServiceImpl.minMatchTYpe == matchType) { // 最小规则，直接返回,最大规则还需继续查找
	                        break;
	                    }
	                }
	            } else { // 不存在，直接返回
	                break;
	            }
	        }
	        if (matchFlag < 2 || !flag) { // 长度必须大于等于1，为词
	            matchFlag = 0;
	        }
	        return matchFlag;
	    }

	    public static void main(String[] args) {
	        BanedKeywordServiceImpl filter = new BanedKeywordServiceImpl();
	        String string = "反共多少分开了家里打扫房间丽舍大街路口反抗螺丝钉警方立刻圣诞节快乐房里看电视克利夫兰科达斯科拉菲快乐圣诞节发卡量达斯科拉反G该罚的辅导感到十分个地方鬼地方感动发给地方鬼地方鬼地方鬼地方感到十分鬼地方感到十分鬼地方感动中共地方鬼地方感到十分感到十分鬼地方广东省打手犯规大塞弗鬼地方感到十分古典风格对方是个共产梵蒂冈地方感动独立分开计算的离开家连锁店接连发生的离开分快乐圣诞节分卡洛斯的离开离开家得了三等奖路口发生的离开洗脑辅导告诉对方感到十分感动发给三等份鬼地方感到十分鬼地方鬼地方地方GCD鬼地方梵蒂冈地方该罚的鬼地方鬼地方鬼地方鬼地方给对方是个打手犯规第四个 CCP地方鬼地方感动大放送感到十分感到十分光的反射感到十分告诉对方感到十分广东省GONG党 豆腐干大塞弗感到十分告诉对方告诉对方感到十分感到十分敢死队大塞弗网特 梵蒂冈打手犯规打手犯规对方是个大放送给对方是个地方暴政 梵蒂冈地方广东省反攻倒算豆腐干对方是个豆腐干大放送";
	        
	        Set<String> set = filter.getSensitiveWord(string, 1);
	        List<String> list = new ArrayList<String>();
            
	        Collections.addAll(list);//填充            
	        set.addAll(list);//给set填充         
	        list.clear();//清空list，不然下次把set元素加入此list的时候是在原来的基础上追加元素的         
	        list.addAll(set);//把set的  
	        for (String word : set) {
	            filter.replaceSensitiveWord(string, 1, "***");
	            string = string.replace(word, "***");
	        }
	    }

	    
	    /**
	     * @Description: 初始化敏感词库，将敏感词加入到HashMap中，构建DFA算法模型
	     * @Project：test
	     * @version 1.0
	     */
	    public class SensitiveWordInit {
	        private String ENCODING = "utf-8"; // 字符编码
	        @SuppressWarnings("rawtypes")
	        public HashMap sensitiveWordMap;

	        public SensitiveWordInit() {
	            super();
	        }

	        /**
	         * @version 1.0
	         */
	        @SuppressWarnings("rawtypes")
	        public Map initKeyWord() {
	            try {
	                // 读取敏感词库
	                Set<String> keyWordSet = readSensitiveWordFile();
	                // 将敏感词库加入到HashMap中
	                addSensitiveWordToHashMap(keyWordSet);
	                // spring获取application，然后application.setAttribute("sensitiveWordMap",sensitiveWordMap);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            return sensitiveWordMap;
	        }

	        /**
	         * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
	         * 中 = { isEnd = 0 国 = {<br>
	         * isEnd = 1 人 = {isEnd = 0 民 = {isEnd = 1} } 男 = { isEnd = 0 人 = { isEnd =
	         * 1 } } } } 五 = { isEnd = 0 星 = { isEnd = 0 红 = { isEnd = 0 旗 = { isEnd = 1
	         * } } } }
	         * 
	         * @param keyWordSet
	         *            敏感词库
	         * @version 1.0
	         */
	        @SuppressWarnings({ "rawtypes", "unchecked" })
	        private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
	            sensitiveWordMap = new HashMap(keyWordSet.size()); // 初始化敏感词容器，减少扩容操作
	            String key = null;
	            Map nowMap = null;
	            Map<String, String> newWorMap = null;
	            // 迭代keyWordSet
	            Iterator<String> iterator = keyWordSet.iterator();
	            while (iterator.hasNext()) {
	                key = iterator.next(); // 关键字
	                nowMap = sensitiveWordMap;
	                for (int i = 0; i < key.length(); i++) {
	                    char keyChar = key.charAt(i); // 转换成char型
	                    Object wordMap = nowMap.get(keyChar); // 获取

	                    if (wordMap != null) { // 如果存在该key，直接赋值
	                        nowMap = (Map) wordMap;
	                    } else { // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
	                        newWorMap = new HashMap<String, String>();
	                        newWorMap.put("isEnd", "0"); // 不是最后一个
	                        nowMap.put(keyChar, newWorMap);
	                        nowMap = newWorMap;
	                    }

	                    if (i == key.length() - 1) {
	                        nowMap.put("isEnd", "1"); // 最后一个
	                    }
	                }
	            }
	        }

	        /**
	         * 读取敏感词库中的内容，将内容添加到set集合中
	         * 
	         * @return
	         * @version 1.0
	         * @throws Exception
	         */
	        @SuppressWarnings("resource")
	        private Set<String> readSensitiveWordFile() throws Exception {
	            Set<String> set = null;
	            List<String> list = new ArrayList<String>();
	              
		        Collections.addAll(list);//填充            
		        set.addAll(list);//给set填充         
		        list.clear();//清空list，不然下次把set元素加入此list的时候是在原来的基础上追加元素的         
		        list.addAll(set);//把set的  

	            File file = new File("C:\\Users\\asuspc\\Desktop\\a.txt"); // 读取文件  这部分我理解的是敏感词已经写好一个文件存储起来，直接进行读取就行了
	            InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);
	            try {
	                if (file.isFile() && file.exists()) { // 文件流是否存在
	                    set = new HashSet<String>();
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String txt = null;
	                    while ((txt = bufferedReader.readLine()) != null) { // 读取文件，将文件内容放入到set中
	                        set.add(txt);
	                    }
	                } else { // 不存在抛出异常信息
	                    throw new Exception("敏感词库文件不存在");
	                }
	            } catch (Exception e) {
	                throw e;
	            } finally {
	                read.close(); // 关闭文件流
	            }
	            return set;
	        }
	    }
}
