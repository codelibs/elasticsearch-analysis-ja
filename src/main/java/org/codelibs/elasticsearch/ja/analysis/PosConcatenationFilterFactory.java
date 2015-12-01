package org.codelibs.elasticsearch.ja.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.codelibs.analysis.ja.PosConcatenationFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class PosConcatenationFilterFactory extends AbstractTokenFilterFactory {

    private Set<String> posTags;

    @Inject
    public PosConcatenationFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, Environment env) {
        super(index, indexSettingsService.getSettings(), name, settings);

        String posPath = settings.get("tags_path");

        if (posPath != null) {
            File posFile = env.configFile().resolve(posPath).toFile();
            try (InputStream is = new FileInputStream(posFile)) {
                posTags.addAll(WordlistLoader.getLines(is, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not load " + posFile.getAbsolutePath(), e);
            }
        } else {
            String tags = settings.get("tags");
            if (tags != null) {
                for (String tag : tags.split(",")) {
                    posTags.add(tag.trim());
                }
            } else {
                posTags = new HashSet<>();
            }
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new PosConcatenationFilter(tokenStream, posTags);
    }
}
