package org.codelibs.elasticsearch.ja;

import org.codelibs.elasticsearch.ja.analysis.ReloadableKuromojiTokenizerFactory;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class JaPlugin extends AbstractPlugin {
    @Override
    public String name() {
        return "JaPlugin";
    }

    @Override
    public String description() {
        return "This plugin provides analysis library for Japanese.";
    }

    public void onModule(AnalysisModule module) {
        module.addTokenizer("reloadable_kuromoji_tokenizer",
                ReloadableKuromojiTokenizerFactory.class);
    }

}
