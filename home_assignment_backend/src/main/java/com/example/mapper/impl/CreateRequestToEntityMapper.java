package com.example.mapper.impl;

import com.example.mapper.Mapper;
import com.example.model.TreeNode;
import com.example.model.request.CreateTreeNodeRequest;
import com.example.repository.TreeNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreateRequestToEntityMapper implements Mapper<CreateTreeNodeRequest, TreeNode> {

    private final TreeNodeRepository treeNodeRepository;

    public TreeNode transform(CreateTreeNodeRequest dto){

        return TreeNode.builder()
                       .id(dto.getId())
                       .name(dto.getName())
                       .content(dto.getContent())
                       .parentNode(Optional.ofNullable(dto.getParentId())
                               .map(treeNodeRepository::getReferenceById)
                               .orElse(null))
                       .build();

    }

}
