package com.example.service.impl;

import com.example.exception.ValidationException;
import com.example.mapper.Mapper;
import com.example.model.TreeNode;
import com.example.model.response.SingleValueResponse;
import com.example.model.response.TreeNodeResponse;
import com.example.model.request.CreateTreeNodeRequest;
import com.example.model.request.ReorganizeTreeNodeRequest;
import com.example.model.request.UpdateTreeNodeRequest;
import com.example.repository.TreeNodeRepository;
import com.example.service.TreeNodeService;
import com.example.validation.Constants;
import com.example.validation.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TreeNodeServiceImpl implements TreeNodeService {
    private static final String CONTENT_JSON_PROPERTY_NAME = "content";

    private final Validator<CreateTreeNodeRequest> createRequestValidator;
    private final Validator<UpdateTreeNodeRequest> updateRequestValidator;
    private final Validator<ReorganizeTreeNodeRequest> reorganizeRequestValidator;

    private final Mapper<CreateTreeNodeRequest, TreeNode> createRequestMapper;
    private final Mapper<UpdateTreeNodeRequest, TreeNode> updateRequestMapper;
    private final Mapper<TreeNode, TreeNodeResponse> entityMapper;

    private final ObjectMapper mapper;


    private final TreeNodeRepository treeNodeRepository;


    @Override
    public TreeNodeResponse create(CreateTreeNodeRequest request) throws ValidationException {
        createRequestValidator.validate(request);
        TreeNode entity = createRequestMapper.transform(request);
        entity = treeNodeRepository.save(entity);
        return entityMapper.transform(entity);
    }

    @Override
    public TreeNodeResponse update(UpdateTreeNodeRequest request) throws ValidationException {
        updateRequestValidator.validate(request);
        TreeNode entity = updateRequestMapper.transform(request);
        entity = treeNodeRepository.save(entity);
        return entityMapper.transform(entity);
    }

    @Override
    public void delete(Long id) throws ValidationException {
        if(!treeNodeRepository.existsById(id)){
            throw new ValidationException(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE);
        } //I think creating a separate validator for this would be overkill / over-engineering.

        treeNodeRepository.deleteById(id);
    }

    @Override
    public List<TreeNodeResponse> listTree() {
        //Instead of creating a findByParentNodeIsNull in the repository and mapping that, I load all the entries.
        //Otherwise because of the children mapping, I would run into the n+1 problem.
        //Though this would only work unless the table is not too large.
        List<TreeNode> roots = this.getFullTree();
        return roots.stream().map(entityMapper::transform).toList();
    }

    @Override
    public List<TreeNodeResponse> reorganize(ReorganizeTreeNodeRequest request) throws ValidationException {
        reorganizeRequestValidator.validate(request);

        TreeNode target = treeNodeRepository.getReferenceById(request.getTargetId());
        TreeNode newParent = Optional.ofNullable(request.getNewParentId())
                .map(treeNodeRepository::getReferenceById)
                .orElse(null);

        target.setParentNode(newParent);

        treeNodeRepository.save(target);
        if (newParent != null) {
            treeNodeRepository.save(newParent);
        }

        return listTree();
    }

    @Override
    public SingleValueResponse loadContentById(Long id) throws ValidationException {
        if(!treeNodeRepository.existsById(id)){
            throw new ValidationException(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE);
        }
        String content = treeNodeRepository.getReferenceById(id).getContent();
        return new SingleValueResponse(content);
    }

    public List<TreeNodeResponse> findByContent(String content){
        List<TreeNode> roots = this.getFullTree();

        return roots.stream()
                    .map(rootNode -> filterForContent(rootNode, content))
                    .filter(Objects::nonNull)
                    .toList();
    }

    private List<TreeNode> getFullTree(){
        List<TreeNode> allNodes = treeNodeRepository.findAll();
        List<TreeNode> roots = constructTree(allNodes);
        return roots;
    }

    private List<TreeNode> constructTree(List<TreeNode> allNodes){
        List<TreeNode> roots = new ArrayList<>();
        Map<Long, TreeNode> lookupMap = allNodes.stream()
                .collect(Collectors.toMap(TreeNode::getId, node -> node, (existing, replacement) -> existing));

        allNodes.forEach(node -> {
            if(Objects.isNull(node.getParentNode())){
                roots.add(node);
            } else {
                TreeNode parent = lookupMap.get(node.getParentNode().getId());
                parent.getChildren().add(node);
            }
        });

        return roots;
    }

    private TreeNodeResponse filterForContent(TreeNode node, String content){

        boolean containsText = node.getContent().toLowerCase().contains(content.toLowerCase()) || node.getName().toLowerCase().contains(content.toLowerCase());

        List<TreeNodeResponse> filteredChildren = node.getChildren()
                                                      .stream()
                                                      .filter(Objects::nonNull)
                                                      .map(child -> filterForContent(child, content))
                                                      .toList();


        TreeNodeResponse response = entityMapper.transform(node);
        response.setChildren(filteredChildren);
        response.setMatchesFilter(containsText);
        return response;
    }



}
