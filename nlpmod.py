
# coding: utf-8

import nltk
from nltk import pos_tag,word_tokenize
from nltk.util import ngrams
import io
import pandas as pd
from kafka import KafkaProducer
from nltk.tag.stanford import StanfordNERTagger
from nltk.tag.stanford import StanfordPOSTagger
st_ner_tagger = StanfordNERTagger('classifiers/english.conll.4class.distsim.crf.ser.gz', 
                                  'A:/CloudComputing2016/Projecto/stanford-ner.jar')

z = "This is a sample test being conducted at Gainesville Florida to test climate change"

def getPOSTags(text) :
    tokens = word_tokenize(text)
    pos = nltk.pos_tag(tokens)
    return pos

def getNERTags(text) :
    tokens = word_tokenize(text)
    stanford_ner_tags = st_ner_tagger.tag(tokens)
    return stanford_ner_tags

def removeNonASCIICharacters(text) :
    return ''.join([i if ord(i) < 128 else ' ' for i in text])

def getBigrams(tokens) :
    bigs=ngrams(tokens,2)
    bigrams = []
    for (a,b) in bigs :
        bigrams.append(a + ' ' + b)
    return bigrams

def getBigramPOSTags(bigrams) :
    pos = nltk.pos_tag(bigrams)
    return pos

def getBigramNERTags(bigrams) :
    stanford_ner_tags = st_ner_tagger.tag(bigrams)
    return stanford_ner_tags

def getConsecutiveBigrams(tokens, text) :
    cbigrams = []
    word_tokens = word_tokenize(text)
    for x in tokens :
        if tokens.index(x) > 0 :
            w1 = tokens[tokens.index(x) - 1]
            w2 = x
            i1 = word_tokens.index(w1)
            i2 = word_tokens.index(w2)
            if(i2 - i1 == 1) :
                cbigrams.append(w1 + ' ' + w2)
    
    return cbigrams


# In[25]:

def processText(text) :
    print text
    clean_text = removeNonASCIICharacters(text)
    bigrams = getBigrams(word_tokenize(clean_text))
    
    postags = getPOSTags(clean_text)
    
    useful_pos_tags = []
    
    for (a,b) in postags :
        if(b == 'NN') :
            useful_pos_tags.append(a)
    
    pos_bigrams = getConsecutiveBigrams(useful_pos_tags, text)
    
    nertags = getNERTags(clean_text)
    bpostags = getBigramPOSTags(pos_bigrams)
    
    final_tags = []
    
    for (c,d) in bpostags :
         if(d == 'NN') :
            final_tags.append(c)
    
    for (e,f) in nertags :
        if(f == 'LOCATION' or f == 'PERSON' or f == 'ORGANIZATION') :
            final_tags.append(str(e))
    
    producer = KafkaProducer(bootstrap_servers='104.198.172.6:9092')
    producer.send('news', "".join(final_tags))
    
    print final_tags

processText(z)


