package com.example.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreeNodeResponse {

    private String name;
    private String content;
    private Long id;
    private Long parentId;
    private List<TreeNodeResponse> children;
    private Boolean isRoot;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean matchesFilter;


}
