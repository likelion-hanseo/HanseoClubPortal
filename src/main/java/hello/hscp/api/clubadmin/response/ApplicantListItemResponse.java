// src/main/java/hello/hscp/api/clubadmin/response/ApplicantListItemResponse.java
package hello.hscp.api.clubadmin.response;

public record ApplicantListItemResponse(
        Long applicationId,
        String name,
        String studentNo
) {}
