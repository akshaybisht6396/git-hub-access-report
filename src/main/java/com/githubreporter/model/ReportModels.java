package com.githubreporter.model;

import java.util.List;

public class ReportModels {
    public record UserAccessReport(String username, List<RepoAccess> repositories) {}
    public record RepoAccess(String repoName, String permission) {}

    public record GitHubGraphQLResponse(Data data) {}
    public record Data(Organization organization) {}
    public record Organization(Repositories repositories) {}
    public record Repositories(List<RepoNode> nodes, PageInfo pageInfo) {}
    public record RepoNode(String name, Collaborators collaborators) {}
    public record Collaborators(List<Edge> edges) {}
    public record Edge(String permission, UserNode node) {}
    public record UserNode(String login) {}
    public record PageInfo(boolean hasNextPage, String endCursor) {}
    public record ErrorResponse(String message, int status, long timestamp) {}
}
