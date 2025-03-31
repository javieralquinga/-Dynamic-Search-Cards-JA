package com.jadeveloper.core.models;

import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DynamicSearchCardsModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String searchBasePath;

    @ValueMapValue
    private String externalApiEndpoint;

    public String getTitle() {
        return title;
    }

    public String getSearchBasePath() {
        return searchBasePath;
    }

    public String getExternalApiEndpoint() {
        return externalApiEndpoint;
    }
}
