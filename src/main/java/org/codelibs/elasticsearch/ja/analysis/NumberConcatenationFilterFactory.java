package org.codelibs.elasticsearch.ja.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.codelibs.analysis.ja.NumberConcatenationFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class NumberConcatenationFilterFactory extends
        AbstractTokenFilterFactory {

    private CharArraySet suffixWords;

    @Inject
    public NumberConcatenationFilterFactory(Index index,
            IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, Environment env) {
        super(index, indexSettingsService.getSettings(), name, settings);

        String suffixWordsPath = settings.get("suffix_words_path");

        if (suffixWordsPath != null) {
            File suffixWordsFile = env.configFile().resolve(suffixWordsPath).toFile();
            try (Reader reader = IOUtils.getDecodingReader(new FileInputStream(suffixWordsFile),
                    StandardCharsets.UTF_8)) {
                suffixWords = WordlistLoader.getWordSet(reader);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Could not load " + suffixWordsFile.getAbsolutePath(),
                        e);
            }
        } else {
            suffixWords = new CharArraySet(0, false);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new NumberConcatenationFilter(tokenStream, suffixWords);
    }
}
