Elasticsearch Analysis Ja
=======================

## Overview

Elasticsearch Analysis Ja Plugin provides Tokenizer/CharFilter/TokenFilter for Japanese.

## Version

| Version   | elasticsearch |
|:---------:|:-------------:|
| master    | 1.4.X         |

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-analysis-ja/issues "issue").
(Japanese forum is [here](https://github.com/codelibs/codelibs-ja-forum "here").)

## Installation

TBD...

    $ $ES_HOME/bin/plugin --install org.codelibs/elasticsearch-analysis-ja/1.4.0

## References

### ReloadableKuromojiTokenizer

ReloadableKuromojiTokenizer reloads a user-dictionary file when it's updated.
To use this tokenizer, replace "kuromoji\_tokenizer" with "reloadable\_kuromoji\_tokenizer" at the "type" property as below.

    curl -XPUT 'http://localhost:9200/kuromoji_sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "reloadable_kuromoji_tokenizer",
                            "mode" : "extended",
                            "discard_punctuation" : "false",
                            "user_dictionary" : "userdict_ja.txt"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict"
                        }
                    }
                }
            }
        }
    }'

Note that you might lose documents in a result when updating a dictionary file because of changing terms.
