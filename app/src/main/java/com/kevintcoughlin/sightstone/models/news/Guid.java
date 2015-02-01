package com.kevintcoughlin.sightstone.models.news;

import org.simpleframework.xml.Root;

@Root(name = "guid", strict = false)
public class Guid {
    private String content;

    private String isPermaLink;

    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    public String getIsPermaLink ()
    {
        return isPermaLink;
    }

    public void setIsPermaLink (String isPermaLink)
    {
        this.isPermaLink = isPermaLink;
    }
}

