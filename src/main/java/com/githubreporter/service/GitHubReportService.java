package com.githubreporter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.githubreporter.model.ReportModels.Edge;
import com.githubreporter.model.ReportModels.GitHubGraphQLResponse;
import com.githubreporter.model.ReportModels.RepoAccess;
import com.githubreporter.model.ReportModels.RepoNode;
import com.githubreporter.model.ReportModels.UserAccessReport;

@Service
public class GitHubReportService {

    private final WebClient webClient;

    public GitHubReportService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<UserAccessReport> getOrganizationAccessReport(String orgName) {
        String query = """
            query($org: String!, $cursor: String) {
              organization(login: $org) {
                repositories(first: 100, after: $cursor) {
                  pageInfo {
                    hasNextPage
                    endCursor
                  }
                  nodes {
                    name
                    collaborators(first: 100) {
                      edges {
                        permission
                        node {
                          login
                        }
                      }
                    }
                  }
                }
              }
            }
            """;

        Map<String, List<RepoAccess>> userMapping = new HashMap<>();
        String cursor = null;
        boolean hasNextPage = true;

        while (hasNextPage) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("org", orgName);
            variables.put("cursor", cursor);

            Map<String, Object> requestBody = Map.of("query", query, "variables", variables);

            GitHubGraphQLResponse response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    // If GitHub returns a non-200 status (like 401 Unauthorized), this catches it
                    .onStatus(status -> status.isError(), clientResponse -> 
                        clientResponse.createException().flatMap(ex -> {
                            throw ex;
                        })
                    )
                    .bodyToMono(GitHubGraphQLResponse.class)
                    .block();

            // 1. Check if the response or data is missing entirely
            if (response == null || response.data() == null) {
                throw new RuntimeException("Failed to receive a valid response from GitHub API.");
            }

            // 2. Check if organization is null (This happens if the org name is wrong/fake)
            if (response.data().organization() == null) {
                throw WebClientResponseException.create(
                    HttpStatus.NOT_FOUND.value(), 
                    "Organization '" + orgName + "' not found.", 
                    null, null, null);
            }

            var repoData = response.data().organization().repositories();

            for (RepoNode repo : repoData.nodes()) {
                if (repo.collaborators() != null && repo.collaborators().edges() != null) {
                    for (Edge edge : repo.collaborators().edges()) {
                        String username = edge.node().login();
                        RepoAccess access = new RepoAccess(repo.name(), edge.permission());
                        userMapping.computeIfAbsent(username, k -> new ArrayList<>()).add(access);
                    }
                }
            }

            hasNextPage = repoData.pageInfo().hasNextPage();
            cursor = repoData.pageInfo().endCursor();
        }

        return userMapping.entrySet().stream()
                .map(entry -> new UserAccessReport(entry.getKey(), entry.getValue()))
                .toList();
    }
}