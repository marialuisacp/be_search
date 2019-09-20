package org.deeplearning4j.examples.nlp.word2vec;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.inputsanitation.InputHomogenization;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.stopwords.StopWords;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.valueOf;

import com.biible.lucene.Term;
import com.biible.lucene.TermItemArray;

public class Word2VecUptrainingExample {

    public static final int S_SIMILAR_TERMS = 10;
    public static final int S_TERMS_TEST = 1010;
    // public static List<String> stopWords = StopWords.getStopWords();
    public static List<String> stopWords = new ArrayList<>();
    public static List<String> stopWordsPt = new ArrayList<>();
    public static List<String> vocabBible = new ArrayList<>();
    public static String language = "pt";
    public static String[] terms = new String[S_TERMS_TEST];
    public static String[][] results = new String[S_TERMS_TEST][25];
    public static int[] frequences = new int[S_TERMS_TEST];
    public static String stringResults = "";
    private static Logger log = LoggerFactory.getLogger(Word2VecUptrainingExample.class);

    /**
     * readFileTest        Function for ready files of tests
     */
    public static void setStopWordsPt() {
        String[] palavras = {"a", "à", "é", "abaixo", "acaso", "portanto", "acima", "acolá", "adiante", "adrede", "afinal", "afora", "agora", "aí", "ainda", "além", "alerta", "algo", "alguém", "algum", "alguma", "algumas", "alguns", "algures", "alhures", "ali", "aliás", "amanhã", "amiúde", "ampla", "amplas", "amplo", "amplos", "ante", "anteontem", "antes", "antigamente", "ao", "aonde", "aos", "apenas", "após", "aquela", "aquelas", "aquele", "aqueles", "aquém", "aqui", "aquilo", "as", "às", "assaz", "assim", "até", "atrás", "através", "avante", "bastante", "bem", "breve", "brevemente", "cá", "cada", "calmamente", "cedo", "certamente", "certo", "cima", "coisa", "coisas", "com", "como", "completamente", "completo", "comumente", "concomitantemente", "conseguintemente", "consequentemente", "contra", "contudo", "cor", "corajosamente", "da", "dantes", "daquele", "daqueles", "das", "de", "debaixo", "debalde", "decerto", "defronte", "dela", "delas", "dele", "deles", "demais", "demasiadamente", "demasiado", "dentro", "depois", "depressa", "desde", "dessa", "dessas", "desse", "desses", "desta", "destas", "deste", "destes", "detrás", "devagar", "deve", "devem", "devendo", "dever", "deverá", "deverão", "deveras", "deveria", "deveriam", "devia", "deviam", "diante", "diariamente", "dificilmente", "direita", "disse", "disso", "distância", "disto", "dito", "diz", "dizem", "do", "donde", "doravante", "dos", "e", "é", "efetivamente", "eis", "Eis", "ela", "elas", "ele", "eles", "em", "enfim", "enquanto", "então", "entre", "entrementes", "era", "eram", "éramos", "esquerda", "essa", "essas", "esse", "esses", "esta", "está", "estamos", "estão", "estas", "estava", "estavam", "estávamos", "este", "esteja", "estejam", "estejamos", "estes", "esteve", "estive", "estivemos", "estiver", "estivera", "estiveram", "estivéramos", "estiverem", "estivermos", "estivesse", "estivessem", "estivéssemos", "estou", "eu", "excessivamente", "excesso", "exclusivamente", "extremamente", "fazendo", "fazer", "feita", "feitas", "feito", "feitos", "felizmente", "finalmente", "foi", "fomos", "for", "fora", "foram", "fôramos", "forem", "forma", "formos", "fosse", "fossem", "fôssemos", "frente", "fui", "geral", "grande", "grandemente", "grandes", "há", "haja", "hajam", "hajamos", "hão", "havemos", "havia", "hei", "hoje", "houve", "houvemos", "houver", "houvera", "houverá", "houveram", "houvéramos", "houverão", "houverei", "houverem", "houveremos", "houveria", "houveriam", "houveríamos", "houvermos", "houvesse", "houvessem", "houvéssemos", "imediatamente", "inclusivamente", "inclusive", "incontestavelmente", "intensamente", "isso", "isto", "já", "jamais", "jeito", "la", "lá", "lado", "levemente", "lhe", "lhes", "ligeiramente", "livremente", "lo", "logo", "longe", "mais", "mal", "mas", "me", "meio", "melhor", "menos", "mesma", "mesmas", "mesmo", "mesmos", "meu", "meus", "minha", "minhas", "modo", "mui", "muita", "muitas", "muito", "muitos", "na", "nada", "não", "nas", "nem", "nenhum", "nenhuma", "nenhures", "nessa", "nessas", "nesta", "nestas", "ninguém", "no", "nomeadamente", "nos", "nós", "nossa", "nossas", "nosso", "nossos", "num", "numa", "nunca", "o", "ó", "onde", "ontem", "ora", "os", "ou", "outra", "outras", "outro", "outrora", "outros", "para", "passo", "pela", "pelas", "pelo", "pelos", "penas", "pequena", "pequenas", "pequeno", "pequenos", "per", "perante", "perto", "pior", "pode", "podendo", "poder", "poderia", "poderiam", "podia", "podiam", "pois", "por", "porém", "porque", "porquê", "porquanto", "segundo", "dizendo", "mim", "causa", "porventura", "possivelmente", "posso", "pouca", "poucas", "pouco", "poucos", "presentemente", "pressa", "primeiramente", "primeiro", "primeiros", "principalmente", "profundamente", "propositadamente", "própria", "próprias", "próprio", "próprios", "provavelmente", "pude", "quais", "qual", "quando", "quanto", "quantos", "quão", "quase", "que", "quem", "quiçá", "raramente", "realmente", "sabe", "salvo", "são", "se", "seguramente", "seja", "sejam", "sejamos", "selvaticamente", "sem", "sempre", "senão", "sendo", "ser", "será", "serão", "serei", "seremos", "seria", "seriam", "seríamos", "seu", "seus", "si", "sido", "sim", "simplesmente", "simultaneamente", "só", "sob", "sobre", "sobremaneira", "sobremodo", "sobretudo", "somente", "somos", "soslaio", "sou", "sua", "suas", "talvez", "também", "tampouco", "tanto", "tão", "tarde", "te", "tem", "tém", "têm", "temos", "tendo", "tenha", "tenham", "tenhamos", "tenho", "ter", "terá", "terão", "terei", "teremos", "teria", "teriam", "teríamos", "teu", "teus", "teve", "ti", "tido", "tinha", "tinham", "tínhamos", "tive", "tivemos", "tiver", "tivera", "tiveram", "tivéramos", "tiverem", "tivermos", "tivesse", "tivessem", "tivéssemos", "toda", "todas", "todavia", "todo", "todos", "tu", "tua", "tuas", "tudo", "última", "ultimamente", "últimas", "último", "últimos", "um", "uma", "umas", "unicamente", "uns", "vão", "vendo", "ver", "vez", "vezes", "vindo", "vir", "você", "vocês", "volta", "vos", "vós", "/", ".", ";", ",", "!", ":", "?", "’", "“", "”", "–", "'"};
        //String[] palavras = {"a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can", "cannot", "cant", "co", "computer", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herse", "him", "himse", "his", "how", "however", "hundred", "i", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itse", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myse", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thick", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves"};

        int csp = 0;
        for (csp = 0; csp < palavras.length; csp++) {
            stopWordsPt.add(palavras[csp]);
        }
    }
   
    /**
     * getSimilarity          Function for get Similarity between two elements
     *
     * @param a string for comparation
     * @param b string for comparation with 'a'
     * @return cosSim          coefficient of similarity between strings
     */
    public static double getSimilarity(String a, String b, Word2Vec vec) {
        double cosSim = vec.similarity(a, b);
        return cosSim;
    }

    /**
     * getClosestWords     Function for return x closest words of a term
     *
     * @param size size of items for return
     * @param term word for analysis
     * @return lst         list with items most similarity
     */
    public static ArrayList<String> getClosestWords(String term, int size, Word2Vec vec) {
        ArrayList<String> lst = (ArrayList<String>) vec.wordsNearest(term, size);

        return lst;
    }

    /**
     * removeStopWords     Function remove stop words e returns 'sizeTerms' terms most similar
     *
     * @param ps   position in terms
     * @param word term for return similarity
     * @param vec  vector train
     * @return words       words most similarity
     */
    public static List<String> getSimilarWordsWithoutStopWords(String word, int ps, Word2Vec vec) throws IOException {
        stopWords = stopWordsPt;
        int s_similar_terms = 1;
        List<String> words = getClosestWords(word, ps, vec);
        List<String> noStopWords = new ArrayList<>();
        for (String stpw : stopWords) {
            if (words.contains(stpw)) words.remove(stpw);
        }
//        if (words.size() >= s_similar_terms) {
//            for (int q = 0; q < s_similar_terms; q++) {
//                noStopWords.add(words.get(q));
//            }
//            return noStopWords;
//        }

        return words;
    }

    /**
     * training    Function for training of method
     */
    public static Word2Vec training(String filePath, boolean isBible) throws IOException {
        Word2Vec vec;
        
        log.info("Load & Vectorize Sentences....");
        SentenceIterator iter = new LineSentenceIterator(new File(filePath));
        iter.setPreProcessor(new SentencePreProcessor() {
            @Override
            public String preProcess(String sentence) {
                String n_sentence;
                n_sentence = new InputHomogenization(sentence, false).transform();
                return n_sentence;
            }

        });

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        InMemoryLookupCache cache = new InMemoryLookupCache();
        WeightLookupTable<VocabWord> table = new InMemoryLookupTable.Builder<VocabWord>()
            .vectorLength(100)
            .useAdaGrad(false)
            .cache(cache)
            .lr(0.025f).build();
        
        log.info("Building model....");
        vec = new Word2Vec.Builder()
            .minWordFrequency(1)
            .iterations(1)
            .epochs(1)
            .layerSize(100)
            .seed(42)
            .windowSize(5)
            .iterate(iter)
            .tokenizerFactory(t)
            .lookupTable(table)
            .vocabCache(cache)
            .batchSize(100)
            .stopWords(Arrays.asList("three"))
            .useAdaGrad(false)
            .learningRate(0.025)
            .minLearningRate(0.01)
            //.setNGrams(1)
            //.setVectorsListeners()
            //.setTokenPreprocessor("org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor")
            //.setRemoveStop(false)
            .useUnknown(true)
            .build();

        log.info("Fitting Word2Vec model....");
        log.info(filePath);
        vec.fit();
        if(isBible) {
        	WordVectorSerializer.writeWord2VecModel(vec, "models/trainBible.txt");
        	log.info("Save in models/trainBible.txt");
        } else {
        	WordVectorSerializer.writeWord2VecModel(vec, "models/trainText.txt");
        	log.info("Save in models/trainText.txt");
        }
        return vec;   
    }
        
    public static Word2Vec importModelBible() throws FileNotFoundException {
    	Word2Vec vec = WordVectorSerializer.readWord2VecModel("models/trainBible.txt");
		log.info("loaded from models/trainBible.txt");
        return vec;
    }
    
    public static Word2Vec importModelGeneric() throws FileNotFoundException {
    	Word2Vec vec = WordVectorSerializer.readWord2VecModel("models/trainText.txt");
		log.info("loaded from models/trainText.txt");
        return vec;
    }
    
    public static Word2Vec importModelWikipedia() throws FileNotFoundException {
    	
    	DataTypeUtil.setDTypeForContext(DataBuffer.Type.DOUBLE);
//    	Word2Vec vec = WordVectorSerializer.readWord2VecModel("models/trainBible.txt");
    	Word2Vec vec = WordVectorSerializer.loadFullModel("models/train_model_wikipedia.txt");
    	log.info("loaded from model wikipedia");

        return vec;
    }
    
    public static Term getMostSimilarFromArray(String term, List<String> arrayTerms, int size, Word2Vec vec) {
    	List<Double> cosSims = new ArrayList<Double>();
    	List<String> sims = new ArrayList<String>();
		Term t = new Term();
		t.termId = term;
		t.cosSims = cosSims;
		t.similares = sims;
		List<String> terms = (ArrayList<String>) arrayTerms;
    	List<TermItemArray> listItens = new ArrayList<TermItemArray>();
    	
    	for (String s: terms) {
    		 double[] wordVectorS = vec.getWordVector(s);
    		 double[] wordVectorTerm = vec.getWordVector(term);

    		 if(wordVectorS != null && wordVectorTerm != null) {
    		   Double cosSim = getSimilarity(s, term, vec);

    		   TermItemArray ti = new TermItemArray();
    		   ti.termId = term;
    		   ti.similar = s;
    		   ti.cosSim = cosSim;
    		   listItens.add(ti);
    		 }
    	}
    	
    	Collections.sort(listItens, new Comparator<TermItemArray>() {
		  @Override
		  public int compare(TermItemArray t1, TermItemArray t2) {
		    return t2.getSimilarityCOeficient().compareTo(t1.getSimilarityCOeficient());
		  }
		});
    	int countMostSimilares = 0;
    	for (TermItemArray sortedItem: listItens) {
    		if(countMostSimilares < size) {
	    		t.similares.add(sortedItem.similar);
	    		t.cosSims.add(sortedItem.cosSim);
    		}
    		countMostSimilares++;
    	}
    	 
    	return t;
    }
}
