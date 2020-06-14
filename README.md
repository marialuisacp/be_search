# Be - RI (Information Retrieval)

### Sobre o repositório

Este repositório utiliza:

* Apache Lucene
* DeepLearning4j para Word2Vec

### Setup da aplicação

Primeiro passo é a instalação do java jre e jdk.
Depois instalação do Maven.

* Importante instalar a versão 8 do Java.

```
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home
```

Se quiser verificar qual a versão o Maven está rodando basta executar:

```
mvn -v
```
Os diretórios abaixo precisam existir no repositório já com os arquivos a serem indexados pelo Lucene. O download dos arquivos pode ser feito pelo [link](https://drive.google.com/drive/folders/170MP2rcKuvjubUTEfeIfTVXFNYPlhaCm). 

* data_pt/
* data_en/

Os diretórios abaixo também precisam existir, e neles serão armazenados os índices do Lucene.

* index_en/
* index_pt/

Os textos a serem indexados pelo Word2Vec deverão estar na pasta

* texts/

### Para rodar a aplicação

Para rodar a aplicação execute:

```
mvn spring-boot:run
```

ou 

```
mvn clean package
```


