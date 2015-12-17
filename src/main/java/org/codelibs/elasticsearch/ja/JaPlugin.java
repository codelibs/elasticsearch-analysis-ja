package org.codelibs.elasticsearch.ja;

import org.codelibs.elasticsearch.ja.analysis.CharTypeFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.IterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.KanjiNumberFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.NumberConcatenationFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.PatternConcatenationFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.PosConcatenationFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.ProlongedSoundMarkCharFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.ReloadableKuromojiTokenizerFactory;
import org.codelibs.elasticsearch.ja.analysis.StopTokenPrefixFilterFactory;
import org.codelibs.elasticsearch.ja.analysis.StopTokenSuffixFilterFactory;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;

public class JaPlugin extends Plugin {
    @Override
    public String name() {
        return "analysis-ja";
    }

    @Override
    public String description() {
        return "This plugin provides analysis library for Japanese.";
    }

    public void onModule(AnalysisModule module) {
        module.addCharFilter("iteration_mark",
                IterationMarkCharFilterFactory.class);
        module.addCharFilter("prolonged_sound_mark",
                ProlongedSoundMarkCharFilterFactory.class);

        module.addTokenizer("reloadable_kuromoji_tokenizer",
                ReloadableKuromojiTokenizerFactory.class);
        module.addTokenizer("reloadable_kuromoji",
                ReloadableKuromojiTokenizerFactory.class);

        module.addTokenFilter("kanji_number", KanjiNumberFilterFactory.class);
        module.addTokenFilter("kuromoji_pos_concat",
                PosConcatenationFilterFactory.class);
        module.addTokenFilter("char_type", CharTypeFilterFactory.class);
        module.addTokenFilter("number_concat",
                NumberConcatenationFilterFactory.class);
        module.addTokenFilter("pattern_concat",
                PatternConcatenationFilterFactory.class);
        module.addTokenFilter("stop_prefix",
                StopTokenPrefixFilterFactory.class);
        module.addTokenFilter("stop_suffix",
                StopTokenSuffixFilterFactory.class);
    }

}
