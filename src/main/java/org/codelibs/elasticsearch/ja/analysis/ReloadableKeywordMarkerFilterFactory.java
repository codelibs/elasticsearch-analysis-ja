package org.codelibs.elasticsearch.ja.analysis;

import java.nio.file.Path;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.ReloadableKeywordMarkerFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class ReloadableKeywordMarkerFilterFactory extends AbstractTokenFilterFactory {

    private final Path keywordPath;

    private final long reloadInterval;

    @Inject
    public ReloadableKeywordMarkerFilterFactory(Index index, final IndexSettingsService indexSettingsService, final Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        final String path = settings.get("keywords_path");
        if (path != null) {
            keywordPath = env.configFile().resolve(path);
        } else {
            keywordPath = null;
        }

        reloadInterval = settings.getAsTime("reload_interval", TimeValue.timeValueMinutes(1)).getMillis();
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (keywordPath == null) {
            return tokenStream;
        }
        return new ReloadableKeywordMarkerFilter(tokenStream, keywordPath, reloadInterval);
    }

}