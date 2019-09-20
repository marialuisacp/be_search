package com.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.examples.nlp.word2vec.TSNEDataVisualization;
import org.deeplearning4j.examples.nlp.word2vec.TermDimension;

import org.deeplearning4j.models.word2vec.Word2Vec;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.biible.lucene.Term;
import com.biible.lucene.LuceneTester;

@RestController
public class DataController {
	
	LuceneTester testerPt;
	LuceneTester testerEn;
	Word2VecApi vecApiBible;
	Word2VecApi vecApiExternalText;
	Word2VecApi vecApiWikipedia;
	
	public DataController() throws IOException{
		try {
			testerEn =  new LuceneTester("en");
			testerEn.createIndex();
			
			testerPt = new LuceneTester("pt");
			testerPt.createIndex();
			
			boolean trainable = false;
			vecApiBible = new Word2VecApi(trainable, false);
			vecApiExternalText = new Word2VecApi(trainable, true);
			vecApiWikipedia = new Word2VecApi();
	     } catch (IOException e) {
	        e.printStackTrace();
	     }
	} 
        
	@CrossOrigin(origins = "*")
    @RequestMapping("/search")	
    public Data search(
    		@RequestParam(value="name", defaultValue="Deus") String name, 
    		@RequestParam(value="expansion", defaultValue="") ArrayList<String> expansion, 
    		@RequestParam(value="synonymous", defaultValue="") ArrayList<String> synonymous,
    		@RequestParam(value="lang", defaultValue="pt") String lang) {
		
		Data information;
		if(lang.equals("en")) {
			information = new Data(1, name, expansion, synonymous, testerEn);
		} else {
			information = new Data(1, name, expansion, synonymous, testerPt);
		}
    	return information;
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarity")	
    public List<Double> w2vGetSimilarity(
    		@RequestParam(value="term1", defaultValue="ele") String term1, 
    		@RequestParam(value="term2", defaultValue="ela") String term2) throws IOException
	{
		List<String> a1 = vecApiWikipedia.getSimilarWords(term1, 1);
		List<String> a2 = vecApiWikipedia.getSimilarWords(term2, 1);
		List<Double> list = new ArrayList<Double>();

		if(a1.size() > 0 && a2.size() > 0) {
			double cosSim = vecApiWikipedia.getSimilarity(term1, term2);	
			list.add(cosSim);
		}
		return list;
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarwordsarray")	
	public List<Term> getSimilaresByArray(@RequestParam (value="terms", defaultValue="ele,ela") List<String> terms,
			@RequestParam(value="size", defaultValue="10") int size,
			@RequestParam(value="cos", defaultValue="false") boolean hasCosSim) throws IOException
	{
		List<Term> arrayTerms = new ArrayList<Term>();
		
		for (int i = 0; i < terms.size(); i++) {
			List<Double> cosSims = new ArrayList<Double>();
			Term t = new Term();
			t.termId = terms.get(i);
			t.similares = vecApiWikipedia.getSimilarWords(terms.get(i), size);
			
			if(hasCosSim) {
				for (int j = 0; j < t.similares.size(); j++) {
					double cosSimValue = vecApiWikipedia.getSimilarity(t.termId, t.similares.get(j));
					cosSims.add(cosSimValue);
				}
				t.cosSims = cosSims;
			}
			arrayTerms.add(t);
		}
		return arrayTerms;
	}
	
	@CrossOrigin(origins = "*")
    // @RequestMapping("/similarityfromArray")	
	@RequestMapping(value = "/similarityfromArray", method = RequestMethod.POST)
	public Term getSimilaresFromArray(@RequestBody RequestSimilarityArray body) throws IOException
	{
		List<String> arrayTerms = new ArrayList<String>();
		
		for (int i = 0; i < body.terms.size(); i++) {
			String t = new String();
			t = body.terms.get(i);
			
			arrayTerms.add(t);
		}
		return vecApiWikipedia.getMostSimilarWords(body.term, arrayTerms, body.size);

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/similarityfromArray")	
	public Term getSimilaresFromArrayGet(
		//@RequestParam(value="terms", defaultValue="ele,ela") List<String> terms,
		@RequestParam(value="term", defaultValue="ele") String term,
		@RequestParam(value="size", defaultValue="10") int size,
		@RequestParam(value="cos", defaultValue="false") boolean hasCosSim) throws IOException
	{
		String[] concordanceList = {"abandono","aborto","abraço","abundância","abuso","adolescentes","adoração","adoradores","adultério","adversidade","aflição","agnóstico","agradecimento","água","águas","águia","aIDS","ajuda","alegria","aliança","alma","altar","amabilidade","amigo","amigos","amizade","amor","amuletos","anátema","angústia","ânimo","aniversário","anjos","ansiedade","anticristo","aparência","apoio","apostasia","aprendizado","armagedon","arrebatamento","arrependimento","árvore","astrologia","ateísmo","autoridade","avivamento","azeite","babilônia","batalha","batismo","bebida","beleza","bênção","bençãos","benignidade","bíblia","boca","bom","bom dia","bondade","brechas","calma","caminho","caminhos","caridade","casa","casais","casal","casamento","céu","chamado de Deus","choro","chuva","cidade","circuncisão","ciúmes","clamor","cobiça","compaixão","compromisso","comunhão","confiança","confissão","conforto","conhecimento","conquista","consagração","consciência","conselho","consolação","consolar","consolo","contentamento","coração","coragem","ficar corajoso","correção","crença","crer","criação","criacionismo","criança","crianças","cristais","cruz","cuidado","cura","dança","dar","lidar com a Decepção","decisao","dedicação","defeitos","demônios","vencer a Depressão","descanso","desculpas","deserto","desespero","desgosto","desistir","desobediência","desonestidade","despedida","deus","deuses","devoção","diabo","dinheiro","discernimento","disciplina","discipulado","dívidas","divórcio","dízimo","dizimos","doação","doença","dons","dor","doutrina","drogas","dúvida","economia","edificação","egoísmo","elias","emprego","empréstimos","encorajamento","enfermidade","ensinar","ensino","entendimento","entusiasmo","escolhido","escolhidos","espada","espera","esperança","espinhos","espiritismo","espírito","espiritualismo","esposa","esposas","eternidade","evangelho","evangelismo","evangelização","evangelizar","exaltação","excelência","exortação","expectativas","falecimento","falsidade","família","favoritismo","hebreus 11:1","feitiçaria","felicidade","festa","fidelidade","filemom 1:1","filho","filhos","filosofia","flores","fogo","fome","força","formatura","fornicação","fortalecimento","fortaleza","fracasso","fraqueza","frutos","fugir","futuro","fuxico","galardão","generosidade","gentileza","geração","gestão","gideão","globalismo","glória","glorificação","governo","graça","graças","grandeza","gratidão","gravidez","guerra","guerreiro","hábitos","harmonia","herança","heresia","hipocrisia","história","homem","homossexualidade","homossexualismo","homossexual","honestidade","honra","hospitalidade","humildade","humilhação","ide","identidade","idolatria","ídolos","idosos","igreja","igualdade","imagens","imoralidade","importância","impossível","impostos","impressões","incenso","incentivo","incesto","indiferença","inferno","infidelidade","ingratidão","inimigo","inimigos","iniquidade","injustiça","insegurança","inspiração","insultos","integridade","inteligência","intercessão","intimidação","inveja","ira","irmão","irmãos","israel","jacó","jejum","jeová","jerusalem","jesus","jovens","juízo","julgamento","julgar","juramentos","justiça","justificação","justo","juventude","lágrimas","lázaro","lealdade","leão","legalismo","lei","liberdade","libertação","liderança","língua","livramento","longanimidade","loucura","louvar","louvor","lúcifer","luto","luxúria","luz","mãe","mães","mal","maldiçao","mandamentos","mansidão","mãos","mar","maria","marido","maridos","masturbação","maturidade","mediador","meditação","medo","mente","mentir","mentira","mexericos","milagre","milagres","misericórdia","missa","missão","missões","mocidade","moisés","monstros","morte","mortos","motivação","mudança","mulher","mulheres","multiplicação","mundano","mundo","murmuração","música","naamã","nabucodonosor","nação","nações","namorados","namoro","nascimento","natal","natureza","necessidades","noé","noite","noiva","noivado","nomes","obedecer","obediência","obra","obras","obreiros","ocultismo","ocupação","ódio","oferta","ofertas","olhos","oportunidades","oração","orar","organização","orgulho","orientação","otimismo","ouro","ousadia","ouvidos","ovelhas","paciência","pai","pais","palavra","pão","parabéns","parentes","páscoa","passado","pastor","paz","pecado","pecador","pecadores","pedra","pensamentos","pentecostes","perdão","perfeito","perseguição","perseverança","pés","pêsames","piercing","planos","pobreza","poder","política","porta","povo","pregação","preguiça","preocupação","presença","primicias","promessa","promessas","propósito","prosperidade","prostituição","proteção","provação","purificacao","quaresma","quebrantamento","querubins","racismo","raiva","rebeldia","recomeço","recompensa","recompensas","reconciliação","redenção","reflexão","refúgio","regeneração","reino","rejeição","relacionamentos","religião","renovação","renovo","repreensão","representantes","reputação","respeito","ressurreição","restauração","restituição","revelação","riqueza","riquezas","risco","rocha","roubar","roubo","sábado","sabedoria","sábio","sacerdote","sacrifício","sacrifícios","sal","salomão","salvação","sangue","sansão","santa","santidade","santificação","santo","santuário","satanás","saudade","saúde","segurança","semente","separação","serpente","servir","sexo","silêncio","simplicidade","sinais","sinceridade","soberania","soberba","socorro","sofrimento","sol","solidão","sonhos","sono","sorriso","sorte","stress","submissão","sucesso","suicídio","superação","superstição","súplica","talentos","tatuagem","temor","tempestade","tempo","tentação","tesouros","testemunhar","testemunho","timóteo","tolo","trabalho","traição","tranqüilidade","transformação","trevas","tribulação","trindade","tristeza","unção","união","unidade","vaidade","varão","vaso","velhice","vencedor","vencer","vento","ventre","verdade","vergonha","vestes","véu","vida","vigiar","vingança","vinho","violência","visão","vitória","viúva","vontade","zaqueu","zelo"};
		List<String> arrayTerms = new ArrayList<String>();
		
		for (int i = 0; i < concordanceList.length; i++) {
			String t = new String();
			t = concordanceList[i];
			
			arrayTerms.add(t);
		}
		return vecApiWikipedia.getMostSimilarWords(term, arrayTerms, size);
	}
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarity_bible")	
    public List<Double> w2vGetSimilarityBible(
    		@RequestParam(value="term1", defaultValue="jesus") String term1, 
    		@RequestParam(value="term2", defaultValue="cristo") String term2) throws IOException
	{
		List<String> a1 = vecApiBible.getSimilarWords(term1, 1);
		List<String> a2 = vecApiBible.getSimilarWords(term2, 1);
		List<Double> list = new ArrayList<Double>();

		if(a1.size() > 0 && a2.size() > 0) {
			double cosSim = vecApiBible.getSimilarity(term1, term2);	
			list.add(cosSim);
		}
		return list;
    }
		
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarwords_bible")	
	public List<Term> getSimilaresByArrayBible(@RequestParam (value="terms", defaultValue="ele,ela") List<String> terms,
			@RequestParam(value="size", defaultValue="10") int size,
			@RequestParam(value="cos", defaultValue="false") boolean hasCosSim) throws IOException
	{
		List<Term> arrayTerms = new ArrayList<Term>();
		
		for (int i = 0; i < terms.size(); i++) {
			List<Double> cosSims = new ArrayList<Double>();
			Term t = new Term();
			t.termId = terms.get(i);
			t.similares = vecApiBible.getSimilarWords(terms.get(i), size);
			
			if(hasCosSim) {
				for (int j = 0; j < t.similares.size(); j++) {
					double cosSimValue = vecApiBible.getSimilarity(t.termId, t.similares.get(j));
					cosSims.add(cosSimValue);
				}
				t.cosSims = cosSims;
			}
			arrayTerms.add(t);
		}
		return arrayTerms;
	}
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarity_text")	
    public List<Double> w2vGetSimilarityText(
    		@RequestParam(value="term1", defaultValue="ele") String term1, 
    		@RequestParam(value="term2", defaultValue="ela") String term2) throws IOException
	{
		List<String> a1 = vecApiExternalText.getSimilarWords(term1, 1);
		List<String> a2 = vecApiExternalText.getSimilarWords(term2, 1);
		List<Double> list = new ArrayList<Double>();

		if(a1.size() > 0 && a2.size() > 0) {
			double cosSim = vecApiExternalText.getSimilarity(term1, term2);	
			list.add(cosSim);
		}
		return list;
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarwordsarray_text")	
	public List<Term> getSimilaresByArrayText(@RequestParam (value="terms", defaultValue="ele,ela") List<String> terms,
			@RequestParam(value="size", defaultValue="10") int size,
			@RequestParam(value="cos", defaultValue="false") boolean hasCosSim) throws IOException
	{
		List<Term> arrayTerms = new ArrayList<Term>();
		
		for (int i = 0; i < terms.size(); i++) {
			List<Double> cosSims = new ArrayList<Double>();
			Term t = new Term();
			t.termId = terms.get(i);
			t.similares = vecApiExternalText.getSimilarWords(terms.get(i), size);
			
			if(hasCosSim) {
				for (int j = 0; j < t.similares.size(); j++) {
					double cosSimValue = vecApiExternalText.getSimilarity(t.termId, t.similares.get(j));
					cosSims.add(cosSimValue);
				}
				t.cosSims = cosSims;
			}
			arrayTerms.add(t);
		}
		return arrayTerms;
	}
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/similarwords_text")	
	public List<String> w2vSimilarWordsText(
    		@RequestParam(value="term", defaultValue="ele") String term,
    		@RequestParam(value="size", defaultValue="10") int size) throws IOException
	{	
		return vecApiExternalText.getSimilarWords(term, size);
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/tsne")	
    public ArrayList<TermDimension> getTSNE(
    		@RequestParam(value="words", defaultValue="") ArrayList<String> words
    	) throws IOException {
		Word2Vec vec = vecApiWikipedia.getModel();
		return TSNEDataVisualization.getTSNE(words, vec);
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping("/vector")	
    public double[] getVector(
    		@RequestParam(value="word", defaultValue="ele") String term
    	) throws IOException {
		double[] wordVector = vecApiWikipedia.getVectorByTerm(term);
		System.out.println(wordVector);
		return wordVector;
    }
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method=RequestMethod.POST)
    public void singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
        }
        try {
        	String localPath = System.getProperty("user.dir");    
        	String filePath = localPath + "/texts/dataText.txt";
        	
        	Files.deleteIfExists(Paths.get(filePath));
        	File emptyFile = new File(filePath);
        	emptyFile.createNewFile();
            System.out.println("File size: " + emptyFile.length());
        	byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath);
            Files.write(path, bytes);
            System.out.println("Salvou o arquivo em /texts/dataText.txt. Upload feito com sucesso.");

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            vecApiExternalText = new Word2VecApi(true, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@RequestMapping(value="/",method = RequestMethod.GET)
    public String homepage(){
        return "index";
    }
}

