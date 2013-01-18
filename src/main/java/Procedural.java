import com.rits.cloning.Cloner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Procedural {

    static class Story {
        static final String DASH_DIVIDER = "--------------------------\n";
        static final String STORY_POINT = "\n\n";
        static final String INDENT = "  ";
        static final String INDENT2X = "    ";
        static final String INDENT3X = "      ";
        static final String INDENT4X = "        ";

        StringBuilder story = new StringBuilder();

        void grammarStory() {
            story.append("Исходная грамматика\n")
                    .append(DASH_DIVIDER);
            grammarStoryInternal();
            story.append(STORY_POINT);
        }

        void grammarStoryInternal() {
            for(String key : GRAMMAR.keySet()) {
                for(String rule : GRAMMAR.get(key)) {
                    story.append("Правило " + ACTION_RULES.get(new Pair<String, String>(key, rule)) + ": " + key + "->" + rule + ";\n");
                }
            }
        }

        public String tell() {
            return story.toString();
        }

        void enhancedGrammarStory() {
            story.append("Пополненная грамматика\n")
                    .append(DASH_DIVIDER)
                    .append("Правило 0: " + NEW_AXIOM + "->" + AXIOM + ";\n");
            grammarStoryInternal();
            story.append(STORY_POINT);
        }

        void firstStoryIntro() {
            story.append("Вычисление функции FIRST1\n")
                    .append(DASH_DIVIDER);
        }

        void firstStoryNextIteration(int i) {
            story.append("На итерации " + i + ":\n");

            for(Map.Entry<String, List<String>> entry : FIRST.entrySet()) {
                story.append(INDENT + "Для " + entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }

        void endCuttingStory() {
            story.append(STORY_POINT);
        }

        public void q0StoryIntro() {
            story.append("Начальное состояние q0:\n")
                    .append(DASH_DIVIDER);
        }

        public void q0JustInitialized(Situation initialSituation) {
            story.append("Незамкнутое q0: " + initialSituation + "\n");
        }

        public void q0InClosure() {
            story.append("Замыкание q0: " + Q0 + "\n")
                    .append(STORY_POINT);
        }

        public void qnStoryIntro() {
            story.append("Вычисление состояний" + "\n")
                    .append(DASH_DIVIDER);
        }
    }

    static interface IAction { int getI();}

    static class Reduce implements IAction {

        int i;

        Reduce(int i) {
            this.i = i;
        }

        @Override
        public int getI() {
            return i;
        }

        @Override
        public String toString() {
            return "Reduce{" +
                    "i=" + i +
                    '}';
        }
    }

    static enum Action implements IAction { ACCEPT, TRANSFER, ERROR; private int i; @Override public int getI() {throw new RuntimeException();} }

    static class Pair<K, V> {
        K first;
        V second;

        Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
            if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = first != null ? first.hashCode() : 0;
            result = 31 * result + (second != null ? second.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

    static Situation Q0 = null;

    static interface ISituation { void closure();}

    static class NullSituation implements ISituation {
        public void closure() {}
        @Override public boolean equals(Object b) {return b instanceof NullSituation;}
        @Override public String toString() {return "0";}
    }

    static class Situation implements ISituation {
        Set<Configuration> set;

        Situation() {
            set = new HashSet<Configuration>();
        }

        public void closure() {
            // uncomment to debug : System.out.println("\n\n           Before closure: " + this);

            Set<Configuration> additional = new LinkedHashSet<Configuration>();

            while(true) {
                Set<Configuration> additional0 = new LinkedHashSet<Configuration>();
                for(Configuration config : set) {
                    String pseudoContext = "~" + config.rhs;
                    String context = pseudoContext.split("\\.").length > 1 ? pseudoContext.split("\\.")[1].substring(0, 1) : null;
                    String fullContext = context == null ? null : pseudoContext.split("\\.")[1];
                    if(context != null && ENHANCED_GRAMMAR.get(context) != null) {
                        List<String> first = FIRST1(fullContext.substring(1) + config.der);
                        List<String> rules = ENHANCED_GRAMMAR.get(context);

                        for (String first0 : first) {
                            for(String rule : rules) {
                                Configuration newc = new Configuration();
                                newc.lhs = context;
                                newc.rhs = "." + rule;
                                newc.der = first0;
                                additional0.add(newc);
                            }
                        }
                    }
                }
                additional0.removeAll(additional);
                if(additional0.isEmpty()) break;
                else additional.addAll(additional0);
                set.addAll(additional);
            }

            // uncomment to debug : System.out.println("           After closure: " + this + "\n\n");

        }

        @Override public String toString() {return set.toString();}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Situation situation = (Situation) o;

            if (set != null ? !set.equals(situation.set) : situation.set != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return set != null ? set.hashCode() : 0;
        }
    }

    static class Configuration {
        String rhs;
        String lhs;
        String der;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Configuration that = (Configuration) o;

            if (der != null ? !der.equals(that.der) : that.der != null) return false;
            if (lhs != null ? !lhs.equals(that.lhs) : that.lhs != null) return false;
            if (rhs != null ? !rhs.equals(that.rhs) : that.rhs != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = rhs != null ? rhs.hashCode() : 0;
            result = 31 * result + (lhs != null ? lhs.hashCode() : 0);
            result = 31 * result + (der != null ? der.hashCode() : 0);
            return result;
        }

        @Override public String toString() { return "(" + lhs + ", " + rhs + ", " + der + ")"; }
    }

    static List<String> DISTINCT_SYMBOLS = new ArrayList<String>();
    static List<String> VALUABLE_SYMBOLS = new ArrayList<String>();

    static HashMap<String,List<String>> GRAMMAR = new HashMap<String, List<String>>();
    static Map<Pair<String, String>, Integer> ACTION_RULES = new HashMap<Pair<String, String>, Integer>();
    static HashMap<String, List<String>> ENHANCED_GRAMMAR = new HashMap<String, List<String>>();
    static String AXIOM;
    static String NEW_AXIOM = "@";

    static HashMap<String, List<String>> FIRST = new HashMap<String, List<String>>();

    static Queue<ISituation> O = new LinkedBlockingQueue<ISituation>();
    static Queue<ISituation> C = new LinkedBlockingQueue<ISituation>();

    static Map<ISituation, Integer> SITUATION_NUMBERS = new LinkedHashMap<ISituation, Integer>();

    static Map<Pair<ISituation, String>, IAction> ACTION_TABLE = new LinkedHashMap<Pair<ISituation, String>, IAction>();

    static Story STORY = new Story();

    public static void main(String ... args) throws FileNotFoundException {
        Scanner fr = new Scanner(new File("input.txt"));

        AXIOM = fr.nextLine();

        int nRules = 1;
        while(fr.hasNextLine()) {
            String[] tokens = fr.nextLine().split("-\\>");
            List<String> rules = GRAMMAR.get(tokens[0]);
            if(rules == null) {
                rules = new ArrayList<String>();
            }
            String newRule = tokens[1].equals("$") ? "" : tokens[1];
            rules.add(newRule);

            ACTION_RULES.put(new Pair<String, String>(tokens[0], newRule), nRules++);

            if(!tokens[1].equals("$")) {
                String[] subs = tokens[1].split("");
                for(String sub : subs) {
                    if(!DISTINCT_SYMBOLS.contains(sub)) DISTINCT_SYMBOLS.add(sub);
                }
            }
            GRAMMAR.put(tokens[0], rules);
        }
        STORY.grammarStory();
        enhanceGrammar();
        STORY.enhancedGrammarStory();
        valuableSymbols();
        computeFirst();

        computeq0();

        computeqN();

        computeAction();
        // uncomment to debug : System.out.println(ACTION_TABLE);

        for(Map.Entry<Pair<ISituation, String>, IAction> entry : ACTION_TABLE.entrySet()) {
            // uncomment to debug : System.out.println("q"+SITUATION_NUMBERS.get(entry.getKey().first) + "," + entry.getKey().second + "==" + entry.getValue());
        }

        // uncomment to debug : System.out.println(FIRST1("SS"));
        System.out.println(STORY.tell());
    }

    private static void valuableSymbols() {
        VALUABLE_SYMBOLS.add("");
        for(Map.Entry<String, List<String>> entry : GRAMMAR.entrySet()) {
            List<String> values = entry.getValue();
            for(String value : values) {
                for(String character : value.split("")) {
                    if(character.equals(character.toLowerCase()) && !VALUABLE_SYMBOLS.contains(character)) {
                        VALUABLE_SYMBOLS.add(character);
                    }
                }
            }
        }
    }

    private static void computeAction() {
        for(String symbol : VALUABLE_SYMBOLS) {
            for(ISituation situation : C) {
                boolean isError = true;
                for(Configuration configuration : ((Situation)situation).set) {
                    if(configuration.der.equals(symbol)
                            && "".equals(symbol)
                            && configuration.lhs.equals(NEW_AXIOM)
                            && configuration.rhs.equals(AXIOM + ".")) {
                        isError = false;
                        ACTION_TABLE.put(new Pair<ISituation, String>(situation, symbol), Action.ACCEPT);
                        break;
                    }

                    Integer reduceRule =
                            ACTION_RULES.get(
                                    new Pair<String, String>(
                                            configuration.lhs,
                                            configuration.rhs.substring(0, configuration.rhs.length() - 1)));
                    if(configuration.der.equals(symbol)
                            && configuration.rhs.indexOf(".") == configuration.rhs.length() - 1
                            && null != reduceRule) {
                        isError = false;
                        ACTION_TABLE.put(new Pair<ISituation, String>(situation, symbol), new Reduce(reduceRule));
                        break;
                    } else if(configuration.rhs.split("\\.").length == 2
                            /*&& !"".equals(configuration.rhs.split("\\.")[0])*/
                            && null == ENHANCED_GRAMMAR.get(configuration.rhs.split("\\.")[1].substring(0, 1))
                            && FIRST1(configuration.rhs.split("\\.")[1] + configuration.der).contains(symbol)) {
                        isError = false;
                        ACTION_TABLE.put(new Pair<ISituation, String>(situation, symbol), Action.TRANSFER);
                        break;
                    }

                }
                if(isError) ACTION_TABLE.put(new Pair<ISituation, String>(situation, symbol), Action.ERROR);
            }
        }
    }

    private static void computeqN() {

        STORY.qnStoryIntro();

        int nSituation = 0;
        while (!O.isEmpty()) {
            ISituation head = O.poll();
            if(!(head instanceof NullSituation)) SITUATION_NUMBERS.put(head, nSituation++);
            C.add(head);
            List<ISituation> moved = move(head);

            for(ISituation situation : moved) {
                situation.closure();
                if(!O.contains(situation) && !C.contains(situation)) {
                    O.add(situation);
                }
            }
        }
        int i=0;
        C.remove(new NullSituation());
        for(ISituation situation : C) {
            // uncomment to debug : System.out.println("q" + i + "=" + situation);
            i++;
        }
    }

    private static List<ISituation> move(ISituation head) {
        List<ISituation> result = new ArrayList<ISituation>();
        for(String s : DISTINCT_SYMBOLS) {
            if(head instanceof NullSituation) {result.add(new NullSituation());}
            else {
                Situation situation = (Situation) head;
                ISituation news = null;
                for(Configuration configuration : situation.set) {
                    String rhs = " " + configuration.rhs + " ";
                    String tokens[] = rhs.split("\\.");
                    String token = tokens[1];
                    if(s.equals(token.substring(0, 1))) {
                        if(news == null) {news=new Situation();}
                        Configuration newc = new Configuration();
                        newc.der = configuration.der;
                        newc.lhs = configuration.lhs;
                        newc.rhs = tokens[0].trim() + token.substring(0,1) + "." + token.substring(1).trim();
                        ((Situation)news).set.add(newc);
                    }
                }
                if(news == null) {news = new NullSituation();}
                result.add(news);
            }
        }
        return result;
    }

    private static void computeq0() {
        STORY.q0StoryIntro();

        // Take a sole rule with new axiom LHS

        String rhs = ENHANCED_GRAMMAR.get(NEW_AXIOM).get(0);

        Configuration firstRuleConfiguration = new Configuration();

        firstRuleConfiguration.lhs = NEW_AXIOM;
        firstRuleConfiguration.rhs = "." + rhs;
        firstRuleConfiguration.der = "";

        Situation initialSituation = new Situation();

        initialSituation.set.add(firstRuleConfiguration);

        STORY.q0JustInitialized(initialSituation);

        initialSituation.closure();

        Q0 = initialSituation;

        Q0.closure();

        STORY.q0InClosure();

        O.add(Q0);
    }

    private static void computeFirst() {
        Cloner cloner = new Cloner();
        HashMap<String, List<String>> previous = new HashMap<String, List<String>>();

        STORY.firstStoryIntro();

        for(String key : ENHANCED_GRAMMAR.keySet()) {
            FIRST.put(key, new ArrayList<String>());
        }
        int i = 0;
        while(!previous.equals(FIRST)) {
            previous.clear();
            previous = cloner.deepClone(FIRST);
            STORY.firstStoryNextIteration(i); i++;
            // uncomment to debug : System.out.println("Next iteration for first");
            // uncomment to debug : System.out.println("Previous step result: " + previous);
            for(String key : ENHANCED_GRAMMAR.keySet()) {
                List<String> rules = ENHANCED_GRAMMAR.get(key);
                for(String rule : rules) {
                    // uncomment to debug : System.out.println("-----------------------");
                    // uncomment to debug : System.out.println("--For rule: {" + key + "->" + rule + "} (iteration " + i + ")");
                    Set intersection = intersection(rule, ENHANCED_GRAMMAR.keySet());
                    List<String> substitutions = new ArrayList<String>();
                    if(intersection.isEmpty()) {
                        substitutions.add(rule);
                    } else {
                        List<String> containingNonTerminals = new ArrayList<String>(intersection);
                        substitutions = substitute(rule, containingNonTerminals, previous);
                    }

                    // uncomment to debug : System.out.println("---New substutions for {" + key + "->" + rule + "} produce " + substitutions);

                    List<String> newFirst = cutToFirst(substitutions);
                    List<String> container = FIRST.get(key);
                    if(container == null) {
                        container = new ArrayList<String>();
                    }
                    container.addAll(newFirst);
                    container.addAll(previous.get(key) != null ? previous.get(key) : new ArrayList<String>());
                    removeDuplicates(container);
                    FIRST.put(key, container); // redundant
                }
            }
            // uncomment to debug : System.out.println("After iteration:" + FIRST);
            // uncomment to debug : System.out.println("After iteration:" + previous);
        }

        STORY.endCuttingStory();
    }

    private static Set intersection(String rule, Set<String> strings) {
        Set<String> ruleSet = new HashSet<String>(Arrays.asList(rule.split("")));
        ruleSet.retainAll(strings);

        return ruleSet;
    }

    private static void removeDuplicates(List<String> container) {
        Set<String > contSet = new HashSet<String>(container);
        container.removeAll(container);
        container.addAll(contSet);
    }

    private static List<String> cutToFirst(List<String> substitutions) {
        List<String> result = new ArrayList<String>();

        for(String substitution : substitutions) {
            if(substitution.length() == 0) {
                result.add(substitution);
            } else {
                result.add(substitution.substring(0, 1));
            }
        }
        return result;
    }

    private static List<String> substitute(String rule, List<String> containingNonTerminals, HashMap<String, List<String>> previous) {
        // uncomment to debug : System.out.println("----Inside substitution loop: " + containingNonTerminals);

        Map<String, List<String>>  copy = new HashMap<String, List<String>>(previous);
        List<String> terminalsCopy = new ArrayList<String>(containingNonTerminals);
        List<String> result = new ArrayList<String>();

        String curNT = terminalsCopy.get(0);
        terminalsCopy.remove(0);
        for(String subst : copy.get(curNT)) {
            String rule0 = rule.replaceAll(curNT, subst);
            sub0(result, copy, terminalsCopy, rule0);
        }

        return result;
    }

    private static void sub0(List<String> result, Map<String, List<String>> copy, List<String> containingNonTerminals, String rule) {
        // uncomment to debug : System.out.println("------Inner s. loop " + copy + ", res:" + result + ", t:" + containingNonTerminals + ", rule: " + rule);
        if(containingNonTerminals.isEmpty()) {
            result.add(rule);
            return;
        };

        List<String> terminalCopy = new ArrayList<String>(containingNonTerminals);
        String curNT = terminalCopy.get(0);
        terminalCopy.remove(0);

        for(String subst : copy.get(curNT)) {
            String rule0 = rule.replaceAll(curNT, subst);
            sub0(result, copy, terminalCopy, rule0);
        }


    }

    private static void enhanceGrammar() {
        ENHANCED_GRAMMAR.putAll(GRAMMAR);
        ENHANCED_GRAMMAR.put(NEW_AXIOM, new ArrayList<String>() {{add(AXIOM);}});
    }

    private static List<String> FIRST1(final String input) {

        if(input.length() <= 1) {
            if(FIRST.get(input) != null) {
                return FIRST.get(input);
            } else {
                return new ArrayList<String>() {{add(input);}};
            }
        }
        List<String> sl1 = FIRST1(input.substring(0, input.length()/2));
        List<String> sl2 = FIRST1(input.substring(input.length() / 2, input.length()));

        Set<String> result = new HashSet<String>();

        for(String string0 : sl1) {
            for(String string1 : sl2) {
                result.add((string0+string1).length() < 1 ? "" : (string0+string1).substring(0, 1));
            }
        }

        return new ArrayList<String>(result);
    }
}
