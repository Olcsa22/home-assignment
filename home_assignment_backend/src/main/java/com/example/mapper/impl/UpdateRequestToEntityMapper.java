package com.example.mapper.impl;

import com.example.mapper.Mapper;
import com.example.model.TreeNode;
import com.example.model.request.UpdateTreeNodeRequest;
import com.example.repository.TreeNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateRequestToEntityMapper implements Mapper<UpdateTreeNodeRequest, TreeNode> {

    private final TreeNodeRepository treeNodeRepository;

    @Override
    public TreeNode transform(UpdateTreeNodeRequest dto) {
        TreeNode entity = treeNodeRepository.getReferenceById(dto.getId());
        entity.setName(dto.getName());
        entity.setContent(dto.getContent());
        return entity;
    }

}
