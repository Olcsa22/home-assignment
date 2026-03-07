package com.example.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReorganizeTreeNodeRequest {
    private Long targetId;
    private Long newParentId;
}
