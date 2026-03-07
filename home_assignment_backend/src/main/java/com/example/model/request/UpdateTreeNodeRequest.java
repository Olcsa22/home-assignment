package com.example.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTreeNodeRequest {

    private Long id;
    private String name;
    private String content;

}
