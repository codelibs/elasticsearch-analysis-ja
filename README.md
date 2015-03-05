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

### IterationMarkCharFilter (Char Filter)

IterationMarkCharFilter normalizes an iteration mark charcter. 
For example, this char filter replaces "学問のすゝめ" with "学問のすすめ".
The property name is "iteration_mark".

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "kuromoji_tokenizer",
                            "mode" : "extended"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "char_filter":["iteration_mark"]
                        }
                    }
                }
            }
        }
    }'

### ProlongedSoundMarkCharFilter (Char Filter)

ProlongedSoundMarkCharFilter replaces the following prolonged sound mark charcters with '\u30fc' (KATAKANA-HIRAGANA SOUND MARK).

| Unicode | Name |
|:-----:|:-----|
| U002D | HYPHEN-MINUS |
| UFF0D | FULLWIDTH HYPHEN-MINUS |
| U2010 | HYPHEN |
| U2011 | NON-BREAKING HYPHEN |
| U2012 | FIGURE DASH |
| U2013 | EN DASH |
| U2014 | EM DASH |
| U2015 | HORIZONTAL BAR |
| U207B | SUPERSCRIPT MINUS |
| U208B | SUBSCRIPT MINUS |
| U30FC | KATAKANA-HIRAGANA SOUND MARK |

This char filter name is "prolonged_sound_mark" as below.

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "kuromoji_tokenizer",
                            "mode" : "extended"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "char_filter":["prolonged_sound_mark"]
                        }
                    }
                }
            }
        }
    }'

### KanjiNumberFilter (TokenFilter)

KanjiNumberFilter relaces Kanji number character(ex. "一") with a number character(ex. "1").
This token filter name is "".

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "kuromoji_tokenizer",
                            "mode" : "extended"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "filter":["kanji_number"]
                        }
                    }
                }
            }
        }
    }'

### ReloadableKuromojiTokenizer (Tokenizer)

ReloadableKuromojiTokenizer reloads a user-dictionary file when it's updated.
To use this tokenizer, replace "kuromoji\_tokenizer" with "reloadable\_kuromoji" at the "type" property as below.

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "reloadable_kuromoji",
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
