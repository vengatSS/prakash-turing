package com.framework.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetRepoData {
    @JsonProperty("RepoUrl")
    private String repo;
    @JsonProperty("RepoName")
    private String repoName;
    @JsonProperty("Language")
    private String language;

    public String getRepo(){return repo;}
    public void setRepo(String repo){this.repo = repo;}

    public String getRepoName(){return repoName;}
    public void setRepoName(String reponame){this.repoName = reponame;}

    public String getLanguage(){return language;}
    public void setLanguage(String language){this.language = language;}

}
