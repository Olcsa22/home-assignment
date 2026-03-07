package com.example.validation.impl;

import com.example.exception.ValidationException;
import com.example.model.TreeNode;
import com.example.model.request.ReorganizeTreeNodeRequest;
import com.example.repository.TreeNodeRepository;
import com.example.validation.Constants;
import com.example.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReorganizeRequestValidator implements Validator<ReorganizeTreeNodeRequest> {

    private final TreeNodeRepository treeNodeRepository;

    @Override
    public void validate(ReorganizeTreeNodeRequest reorganizeTreeNodeRequest) throws ValidationException {
        validateTargetExists(reorganizeTreeNodeRequest.getTargetId());
        if(Objects.nonNull(reorganizeTreeNodeRequest.getNewParentId())){
            validateParentExists(reorganizeTreeNodeRequest.getNewParentId());
            validateNoCircularReference(reorganizeTreeNodeRequest.getTargetId(), reorganizeTreeNodeRequest.getNewParentId());
        }
    }

    private void validateTargetExists(Long targetId) throws ValidationException {
        if(nodeDoesNotExist(targetId)){
            throw new ValidationException(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE);
        }
    }

    private void validateParentExists(Long parentId) throws ValidationException {
        if(nodeDoesNotExist(parentId)){
            throw new ValidationException(Constants.ValidationConstants.NOT_EXISTING_PARENT_MESSAGE);
        }
    }

    private void validateNoCircularReference(Long targetId, Long newParentId) throws ValidationException {
        // Check if newParentId is the same as targetId
        if (targetId.equals(newParentId)) {
            throw new ValidationException(Constants.ValidationConstants.CIRCULAR_REFERENCE_MESSAGE);
        }
        
        // Check if newParentId is a descendant of targetId
        Set<Long> descendantIds = collectDescendantIds(targetId);
        if (descendantIds.contains(newParentId)) {
            throw new ValidationException(Constants.ValidationConstants.CIRCULAR_REFERENCE_MESSAGE);
        }
    }

    private Set<Long> collectDescendantIds(Long nodeId) {
        Set<Long> descendantIds = new HashSet<>();
        TreeNode node = treeNodeRepository.getReferenceById(nodeId);
        collectDescendantsRecursively(node, descendantIds);
        return descendantIds;
    }

    private void collectDescendantsRecursively(TreeNode node, Set<Long> descendantIds) {
        if (node.getChildren() != null) {
            for (TreeNode child : node.getChildren()) {
                descendantIds.add(child.getId());
                collectDescendantsRecursively(child, descendantIds);
            }
        }
    }

    private boolean nodeDoesNotExist(Long id){
        return !treeNodeRepository.existsById(id);
    }


}
