
# coding: utf-8


import re
import numpy as np
import pandas as pd

from sklearn.naive_bayes import MultinomialNB
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.preprocessing import LabelEncoder


# In[2]:

news = pd.read_csv("test_train_articles.csv")


# In[3]:

def removeNonASCIICharacters(text) :
    return ''.join([i if ord(i) < 128 else ' ' for i in text])

def normalize_text(s):
    s = s.lower()
    
    # remove punctuation that is not word-internal (e.g., hyphens, apostrophes)
    s = re.sub('\s\W',' ',s)
    s = re.sub('\W\s',' ',s)
    
    # make sure we didn't introduce any double spaces
    s = re.sub('\s+',' ',s)
    
    return removeNonASCIICharacters(s)

news['TITLE'] = [normalize_text(s) for s in news['TITLE']]


# In[13]:

#Make vectors from the titel 
vectorizer = CountVectorizer()
x = vectorizer.fit_transform(news['TITLE'])

#
encoder = LabelEncoder()
y = encoder.fit_transform(news['CATEGORY'])

# split the data into training and testing sets - 80% training and 20% testing
x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.2)

# evaluate the size
print(x_train.shape)
print(y_train.shape)
print(x_test.shape)
print(y_test.shape)


# In[14]:

nb = MultinomialNB()
nb.fit(x_train, y_train)


# In[15]:

nb.score(x_test, y_test)


# In[ ]:



