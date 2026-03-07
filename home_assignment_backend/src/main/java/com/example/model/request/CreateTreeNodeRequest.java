package com.example.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTreeNodeRequest {

    private String name;
    private String content;
    private Long id;
    private Long parentId;

}
