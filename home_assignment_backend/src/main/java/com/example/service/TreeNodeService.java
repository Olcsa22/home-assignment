package com.example.service;

import com.example.exception.ValidationException;
import com.example.model.response.SingleValueResponse;
import com.example.model.response.TreeNodeResponse;
import com.example.model.request.CreateTreeNodeRequest;
import com.example.model.request.ReorganizeTreeNodeRequest;
import com.example.model.request.UpdateTreeNodeRequest;

import java.util.List;

public interface TreeNodeService {

    TreeNodeResponse create(CreateTreeNodeRequest treeNodeDTO) throws ValidationException;
    TreeNodeResponse update(UpdateTreeNodeRequest treeNodeDTO) throws ValidationException;
    void delete(Long delete) throws ValidationException;
    List<TreeNodeResponse> listTree();
    List<TreeNodeResponse> reorganize(ReorganizeTreeNodeRequest reorganizeTreeNodeRequest) throws ValidationException;
    SingleValueResponse loadContentById(Long id) throws ValidationException;
    List<TreeNodeResponse> findByContent(String content);

}
