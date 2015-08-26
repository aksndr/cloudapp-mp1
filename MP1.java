import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {

        Map<String, Integer> map = new HashMap<String, Integer>();

        Integer idx = 0;
        List<String> stopWordsList = Arrays.asList(stopWordsArray);

        List<Integer> indexes = Arrays.asList(getIndexes());
        List<String> lines = Files.readAllLines(FileSystems.getDefault().getPath(this.inputFileName), StandardCharsets.UTF_8);
        for (String line : lines) {
            if (indexes.contains(idx)) {
                //System.out.println(idx + " : " + line);
                StringTokenizer st = new StringTokenizer(line, delimiters);
                while (st.hasMoreElements()) {
                    String word = st.nextToken();
                    if (word.isEmpty() || stopWordsList.contains(word)) continue;
                    if (map.containsKey(word)) {
                        Integer count = map.get(word);
                        map.put(word, ++count);
                    } else {
                        map.put(word, 1);
                    }
                }
            }
            idx++;
        }
        return getTop20RatedWordsArray(map);
    }

    private String[] getTop20RatedWordsArray(Map<String, Integer> map) {
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        sorted_map.putAll(map);

        List<String> list = Collections.list(Collections.enumeration(sorted_map.keySet())).subList(0, 20);
        return list.toArray(new String[20]);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("MP1 <User ID>");
        } else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item : topItems) {
                System.out.println(item);
            }
        }
    }

    class ValueComparator implements Comparator<String> {

        Map<String, Integer> map;

        public ValueComparator(Map<String, Integer> base) {
            this.map = base;
        }

        public int compare(String a, String b) {
            Integer ai = map.get(a);
            Integer bi = map.get(b);
            if (ai >= bi) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
