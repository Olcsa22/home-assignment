package com.example.mapper.impl;

import com.example.mapper.Mapper;
import com.example.model.TreeNode;
import com.example.model.response.TreeNodeResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EntityToNodeResponseMapper implements Mapper<TreeNode, TreeNodeResponse> {

    @Override
    public TreeNodeResponse transform(TreeNode treeNode) {
        return TreeNodeResponse.builder()
                .id(treeNode.getId())
                .name(treeNode.getName())
                .content(treeNode.getContent())
                .isRoot(treeNode.isRoot())
                .parentId(
                        Optional.ofNullable(treeNode.getParentNode())
                                .map(TreeNode::getId)
                                .orElse(null)
                )
                .children(
                        Optional.ofNullable(treeNode.getChildren())
                                .orElse(List.of())
                                .stream()
                                .map(this::transform)
                                .toList()
                )
                .build();
    }

}
