

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
	    public static int minMatchTYpe = 1; // min match rule
	    public static int maxMatchType = 2; // max match rule

	    /**
	     * 
	     */
	    public BanedKeywordServiceImpl() {
	        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
	    }

	    /**
	     * if exists sensitive word or not
	     * 
	     * @param txt
	     *           
	     * @param matchType
	     *            
	     * @return exist then true，not exist then false
	     * @version 1.0
	     */
	    public boolean isContaintSensitiveWord(String txt, int matchType) {
	        boolean flag = false;
	        for (int i = 0; i < txt.length(); i++) {
	            int matchFlag = this.CheckSensitiveWord(txt, i, matchType); 
	            if (matchFlag > 0) { 
	                flag = true;
	            }
	        }
	        return flag;
	    }

	    /**
	     * get sensitive word 
	     * 
	     * @param txt
	     *           
	     * @param matchType
	     *            
	     * @return
	     * @version 1.0
	     */
	    public Set<String> getSensitiveWord(String txt, int matchType) {
	        Set<String> sensitiveWordList = new HashSet<String>();
	        List<String> list = new ArrayList<String>();
	              
	        Collections.addAll(list);            
	        sensitiveWordList.addAll(list);         
	        list.clear();
		list.addAll(sensitiveWordList);

	        for (int i = 0; i < txt.length(); i++) {
	            int length = CheckSensitiveWord(txt, i, matchType); //
	            if (length > 0) { //
	                sensitiveWordList.add(txt.substring(i, i + length));
	                i = i + length - 1; // 
	            }
	        }

	        return sensitiveWordList;
	    }

	    /**
	     * 
	     * 
	     * @param txt
	     * @param matchType
	     * @param replaceChar
	     *            
	     * @version 1.0
	     */
	    public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
	        String resultTxt = txt;
	        Set<String> set = getSensitiveWord(txt, matchType); // 
	        List<String> list = new ArrayList<String>();
            
	        Collections.addAll(list);
	        set.addAll(list);         
	        list.clear();//         
	        list.addAll(set);//
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
	     * 
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
	     * 
	     * 
	     * @param txt
	     * @param beginIndex
	     * @param matchType
	     * @return
	     * @version 1.0
	     */
	    @SuppressWarnings({ "rawtypes" })
	    public int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
	        boolean flag = false; // 
	        int matchFlag = 0; // 
	        char word = 0;
	        Map nowMap = sensitiveWordMap;
	        for (int i = beginIndex; i < txt.length(); i++) {
	            word = txt.charAt(i);
	            nowMap = (Map) nowMap.get(word); // 
	            if (nowMap != null) { // 
	                matchFlag++; //
	                if ("1".equals(nowMap.get("isEnd"))) { // 
	                    flag = true; // 
	                    if (BanedKeywordServiceImpl.minMatchTYpe == matchType) { // 
	                        break;
	                    }
	                }
	            } else { // 
	                break;
	            }
	        }
	        if (matchFlag < 2 || !flag) { // 
	            matchFlag = 0;
	        }
	        return matchFlag;
	    }

	    public static void main(String[] args) {
	        BanedKeywordServiceImpl filter = new BanedKeywordServiceImpl();
	        String string = "aaaaaaaaaa";
	        
	        Set<String> set = filter.getSensitiveWord(string, 1);
	        List<String> list = new ArrayList<String>();
            
	        Collections.addAll(list);//            
	        set.addAll(list);        
	        list.clear();//        
	        list.addAll(set);//
	        for (String word : set) {
	            filter.replaceSensitiveWord(string, 1, "***");
	            string = string.replace(word, "***");
	        }
	    }

	    
	    /**
	     * @Description: 
	     * @Project：test
	     * @version 1.0
	     */
	    public class SensitiveWordInit {
	        private String ENCODING = "utf-8"; //
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
	                // 
	                Set<String> keyWordSet = readSensitiveWordFile();
	                // 
	                addSensitiveWordToHashMap(keyWordSet);
	                //
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            return sensitiveWordMap;
	        }

	        /**
	         * 
	         * @param keyWordSet
	         * @version 1.0
	         */
	        @SuppressWarnings({ "rawtypes", "unchecked" })
	        private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
	            sensitiveWordMap = new HashMap(keyWordSet.size()); // 
	            String key = null;
	            Map nowMap = null;
	            Map<String, String> newWorMap = null;
	            // 
	            Iterator<String> iterator = keyWordSet.iterator();
	            while (iterator.hasNext()) {
	                key = iterator.next(); // 
	                nowMap = sensitiveWordMap;
	                for (int i = 0; i < key.length(); i++) {
	                    char keyChar = key.charAt(i); // 
	                    Object wordMap = nowMap.get(keyChar); //

	                    if (wordMap != null) { // 
	                        nowMap = (Map) wordMap;
	                    } else { // 
	                        newWorMap = new HashMap<String, String>();
	                        newWorMap.put("isEnd", "0"); // 
	                        nowMap.put(keyChar, newWorMap);
	                        nowMap = newWorMap;
	                    }

	                    if (i == key.length() - 1) {
	                        nowMap.put("isEnd", "1"); // 
	                    }
	                }
	            }
	        }

	        /**
	         * 
	         * 
	         * @return
	         * @version 1.0
	         * @throws Exception
	         */
	        @SuppressWarnings("resource")
	        private Set<String> readSensitiveWordFile() throws Exception {
	            Set<String> set = null;
	            List<String> list = new ArrayList<String>();
	              
		        Collections.addAll(list);//
		        set.addAll(list);//
		        list.clear();//         
		        list.addAll(set);//

	            File file = new File("C:\\Users\\asuspc\\Desktop\\a.txt"); // 
	            InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);
	            try {
	                if (file.isFile() && file.exists()) { // 
	                    set = new HashSet<String>();
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String txt = null;
	                    while ((txt = bufferedReader.readLine()) != null) { //
	                        set.add(txt);
	                    }
	                } else { // 
	                    throw new Exception("it does not exist");
	                }
	            } catch (Exception e) {
	                throw e;
	            } finally {
	                read.close(); // 
	            }
	            return set;
	        }
	    }
}
