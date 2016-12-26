package sensitiveWord;

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



public class BanedKeywordService {
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
	    public static int minMatchTYpe = 1; // ��Сƥ�����
	    public static int maxMatchType = 2; // ���ƥ�����

	    /**
	     * ���캯������ʼ�����дʿ�
	     */
	    public BanedKeywordService() {
	        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
	    }

	    /**
	     * �ж������Ƿ���������ַ�
	     * 
	     * @param txt
	     *            ����
	     * @param matchType
	     *            ƥ�����&nbsp;1����Сƥ�����2�����ƥ�����
	     * @return ����������true�����򷵻�false
	     * @version 1.0
	     */
	    public boolean isContaintSensitiveWord(String txt, int matchType) {
	        boolean flag = false;
	        for (int i = 0; i < txt.length(); i++) {
	            int matchFlag = this.CheckSensitiveWord(txt, i, matchType); // �ж��Ƿ���������ַ�
	            if (matchFlag > 0) { // ����0���ڣ�����true
	                flag = true;
	            }
	        }
	        return flag;
	    }

	    /**
	     * ��ȡ�����е����д�
	     * 
	     * @param txt
	     *            ����
	     * @param matchType
	     *            ƥ�����&nbsp;1����Сƥ�����2�����ƥ�����
	     * @return
	     * @version 1.0
	     */
	    public Set<String> getSensitiveWord(String txt, int matchType) {
	        Set<String> sensitiveWordList = new HashSet<String>();
	        List<String> list = new ArrayList<String>();
	              
	        Collections.addAll(list);//���            
	        sensitiveWordList.addAll(list);//��set���         
	        list.clear();//���list����Ȼ�´ΰ�setԪ�ؼ����list��ʱ������ԭ���Ļ�����׷��Ԫ�ص�         
	        list.addAll(sensitiveWordList);//��set��   

	        for (int i = 0; i < txt.length(); i++) {
	            int length = CheckSensitiveWord(txt, i, matchType); // �ж��Ƿ���������ַ�
	            if (length > 0) { // ����,����list��
	                sensitiveWordList.add(txt.substring(i, i + length));
	                i = i + length - 1; // ��1��ԭ������Ϊfor������
	            }
	        }

	        return sensitiveWordList;
	    }

	    /**
	     * �滻�������ַ�
	     * 
	     * @param txt
	     * @param matchType
	     * @param replaceChar
	     *            �滻�ַ���Ĭ��*
	     * @version 1.0
	     */
	    public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
	        String resultTxt = txt;
	        Set<String> set = getSensitiveWord(txt, matchType); // ��ȡ���е����д�
	        List<String> list = new ArrayList<String>();
            
	        Collections.addAll(list);//���            
	        set.addAll(list);//��set���         
	        list.clear();//���list����Ȼ�´ΰ�setԪ�ؼ����list��ʱ������ԭ���Ļ�����׷��Ԫ�ص�         
	        list.addAll(set);//��set��  
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
	     * ��ȡ�滻�ַ���
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
	     * ����������Ƿ���������ַ������������£�<br>
	     * 
	     * @param txt
	     * @param beginIndex
	     * @param matchType
	     * @return��������ڣ��򷵻����д��ַ��ĳ��ȣ������ڷ���0
	     * @version 1.0
	     */
	    @SuppressWarnings({ "rawtypes" })
	    public int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
	        boolean flag = false; // ���дʽ�����ʶλ���������д�ֻ��1λ�����
	        int matchFlag = 0; // ƥ���ʶ��Ĭ��Ϊ0
	        char word = 0;
	        Map nowMap = sensitiveWordMap;
	        for (int i = beginIndex; i < txt.length(); i++) {
	            word = txt.charAt(i);
	            nowMap = (Map) nowMap.get(word); // ��ȡָ��key
	            if (nowMap != null) { // ���ڣ����ж��Ƿ�Ϊ���һ��
	                matchFlag++; // �ҵ���Ӧkey��ƥ���ʶ+1
	                if ("1".equals(nowMap.get("isEnd"))) { // ���Ϊ���һ��ƥ�����,����ѭ��������ƥ���ʶ��
	                    flag = true; // ������־λΪtrue
	                    if (BanedKeywordService.minMatchTYpe == matchType) { // ��С����ֱ�ӷ���,���������������
	                        break;
	                    }
	                }
	            } else { // �����ڣ�ֱ�ӷ���
	                break;
	            }
	        }
	        if (matchFlag < 2 || !flag) { // ���ȱ�����ڵ���1��Ϊ��
	            matchFlag = 0;
	        }
	        return matchFlag;
	    }

	    public static void main(String[] args) {
	        BanedKeywordService filter = new BanedKeywordService();
	        String string = "�������ٷֿ��˼����ɨ����������·�ڷ�����˿����������ʥ���ڿ��ַ��￴���ӿ��������ƴ�˹�����ƿ���ʥ���ڷ�������˹������G�÷��ĸ����е�ʮ�ָ��ط���ط��ж������ط���ط���ط���ط��е�ʮ�ֹ�ط��е�ʮ�ֹ�ط��ж��й��ط���ط��е�ʮ�ָе�ʮ�ֹ�ط��㶫ʡ���ַ����������ط��е�ʮ�ֹŵ���Է��Ǹ�������ٸԵط��ж������ֿ�������뿪������������������뿪�ֿ���ʥ���ڷֿ���˹���뿪�뿪�ҵ������Ƚ�·�ڷ������뿪ϴ�Ը������߶Է��е�ʮ�ָж��������ȷݹ�ط��е�ʮ�ֹ�ط���ط��ط�GCD��ط���ٸԵط��÷��Ĺ�ط���ط���ط���ط����Է��Ǹ����ַ�����ĸ� CCP�ط���ط��ж�����͸е�ʮ�ָе�ʮ�ֹ�ķ���е�ʮ�ָ��߶Է��е�ʮ�ֹ㶫ʡGONG�� �����ɴ������е�ʮ�ָ��߶Է����߶Է��е�ʮ�ָе�ʮ�ָ����Ӵ��������� ��ٸԴ��ַ�����ַ���Է��Ǹ�����͸��Է��Ǹ��ط����� ��ٸԵط��㶫ʡ�������㶹���ɶԷ��Ǹ������ɴ����";
	        
	        Set<String> set = filter.getSensitiveWord(string, 1);
	        List<String> list = new ArrayList<String>();
            
	        Collections.addAll(list);//���            
	        set.addAll(list);//��set���         
	        list.clear();//���list����Ȼ�´ΰ�setԪ�ؼ����list��ʱ������ԭ���Ļ�����׷��Ԫ�ص�         
	        list.addAll(set);//��set��  
	        for (String word : set) {
	            filter.replaceSensitiveWord(string, 1, "***");
	            string = string.replace(word, "***");
	        }
	    }

	    
	    /**
	     * @Description: ��ʼ�����дʿ⣬�����дʼ��뵽HashMap�У�����DFA�㷨ģ��
	     * @Project��test
	     * @version 1.0
	     */
	    public class SensitiveWordInit {
	        private String ENCODING = "utf-8"; // �ַ�����
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
	                // ��ȡ���дʿ�
	                Set<String> keyWordSet = readSensitiveWordFile();
	                // �����дʿ���뵽HashMap��
	                addSensitiveWordToHashMap(keyWordSet);
	                // spring��ȡapplication��Ȼ��application.setAttribute("sensitiveWordMap",sensitiveWordMap);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            return sensitiveWordMap;
	        }

	        /**
	         * ��ȡ���дʿ⣬�����дʷ���HashSet�У�����һ��DFA�㷨ģ�ͣ�<br>
	         * �� = { isEnd = 0 �� = {<br>
	         * isEnd = 1 �� = {isEnd = 0 �� = {isEnd = 1} } �� = { isEnd = 0 �� = { isEnd =
	         * 1 } } } } �� = { isEnd = 0 �� = { isEnd = 0 �� = { isEnd = 0 �� = { isEnd = 1
	         * } } } }
	         * 
	         * @param keyWordSet
	         *            ���дʿ�
	         * @version 1.0
	         */
	        @SuppressWarnings({ "rawtypes", "unchecked" })
	        private void addSensitiveWordToHashMap(Set<String> keyWordSet) {
	            sensitiveWordMap = new HashMap(keyWordSet.size()); // ��ʼ�����д��������������ݲ���
	            String key = null;
	            Map nowMap = null;
	            Map<String, String> newWorMap = null;
	            // ����keyWordSet
	            Iterator<String> iterator = keyWordSet.iterator();
	            while (iterator.hasNext()) {
	                key = iterator.next(); // �ؼ���
	                nowMap = sensitiveWordMap;
	                for (int i = 0; i < key.length(); i++) {
	                    char keyChar = key.charAt(i); // ת����char��
	                    Object wordMap = nowMap.get(keyChar); // ��ȡ

	                    if (wordMap != null) { // ������ڸ�key��ֱ�Ӹ�ֵ
	                        nowMap = (Map) wordMap;
	                    } else { // ���������򹹽�һ��map��ͬʱ��isEnd����Ϊ0����Ϊ���������һ��
	                        newWorMap = new HashMap<String, String>();
	                        newWorMap.put("isEnd", "0"); // �������һ��
	                        nowMap.put(keyChar, newWorMap);
	                        nowMap = newWorMap;
	                    }

	                    if (i == key.length() - 1) {
	                        nowMap.put("isEnd", "1"); // ���һ��
	                    }
	                }
	            }
	        }

	        /**
	         * ��ȡ���дʿ��е����ݣ���������ӵ�set������
	         * 
	         * @return
	         * @version 1.0
	         * @throws Exception
	         */
	        @SuppressWarnings("resource")
	        private Set<String> readSensitiveWordFile() throws Exception {
	            Set<String> set = null;
	            List<String> list = new ArrayList<String>();
	              
		        Collections.addAll(list);//���            
		        set.addAll(list);//��set���         
		        list.clear();//���list����Ȼ�´ΰ�setԪ�ؼ����list��ʱ������ԭ���Ļ�����׷��Ԫ�ص�         
		        list.addAll(set);//��set��  

	            File file = new File("C:\\Users\\asuspc\\Desktop\\a.txt"); // ��ȡ�ļ�  �ⲿ�������������д��Ѿ�д��һ���ļ��洢������ֱ�ӽ��ж�ȡ������
	            InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);
	            try {
	                if (file.isFile() && file.exists()) { // �ļ����Ƿ����
	                    set = new HashSet<String>();
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String txt = null;
	                    while ((txt = bufferedReader.readLine()) != null) { // ��ȡ�ļ������ļ����ݷ��뵽set��
	                        set.add(txt);
	                    }
	                } else { // �������׳��쳣��Ϣ
	                    throw new Exception("���дʿ��ļ�������");
	                }
	            } catch (Exception e) {
	                throw e;
	            } finally {
	                read.close(); // �ر��ļ���
	            }
	            return set;
	        }
	    }
}
