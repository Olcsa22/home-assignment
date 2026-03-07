package com.example.validation.impl;

import com.example.exception.ValidationException;
import com.example.model.request.CreateTreeNodeRequest;
import com.example.repository.TreeNodeRepository;
import com.example.validation.Constants;
import com.example.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CreateRequestValidator implements Validator<CreateTreeNodeRequest> {

    private final TreeNodeRepository treeNodeRepository;

    @Override
    public void validate(CreateTreeNodeRequest request) throws ValidationException {
        validateNonNullId(request.getId());
        validateDoesNotExist(request.getId());
        validateExistingParentNode(request.getParentId());
        validateNotEmptyContent(request.getContent());
        validateNotEmptyName(request.getName());
    }

    private void validateNonNullId(Long id) throws ValidationException {
        if(Objects.isNull(id)){
            throw new ValidationException(Constants.ValidationConstants.ID_IS_REQUIRED_MESSAGE);
        }
    }

    private void validateDoesNotExist(Long id) throws ValidationException {
        if(alreadyExists(id)){
            throw new ValidationException(Constants.ValidationConstants.NO_RESERVED_ID_MESSAGE);
        }
    }

    private void validateExistingParentNode(Long parentId) throws ValidationException {
        if(Objects.nonNull(parentId) && !alreadyExists(parentId)){
            throw new ValidationException(Constants.ValidationConstants.NOT_EXISTING_PARENT_MESSAGE);
        }
    }

    private void validateNotEmptyName(String name) throws ValidationException {
        if(Objects.isNull(name) || name.isBlank()){
            throw new ValidationException(Constants.ValidationConstants.NO_EMPTY_NAME_MESSAGE);
        }
    }

    private void validateNotEmptyContent(String content) throws ValidationException {
        if(Objects.isNull(content) || content.isBlank()){
            throw new ValidationException(Constants.ValidationConstants.NO_EMPTY_CONTENT_MESSAGE);
        }
    }

    private boolean alreadyExists(long id){
        return treeNodeRepository.existsById(id);
    }

}
