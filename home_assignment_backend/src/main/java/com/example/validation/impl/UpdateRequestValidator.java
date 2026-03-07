package com.example.validation.impl;

import com.example.exception.ValidationException;
import com.example.model.request.UpdateTreeNodeRequest;
import com.example.repository.TreeNodeRepository;
import com.example.validation.Constants;
import com.example.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UpdateRequestValidator implements Validator<UpdateTreeNodeRequest> {

    private final TreeNodeRepository treeNodeRepository;

    @Override
    public void validate(UpdateTreeNodeRequest request) throws ValidationException {
        validateNonNullId(request.getId());
        validateExists(request.getId());
        validateNotEmptyName(request.getName());
        validateNotEmptyContent(request.getContent());
    }

    private void validateNonNullId(Long id) throws ValidationException {
        if (Objects.isNull(id)) {
            throw new ValidationException(Constants.ValidationConstants.ID_IS_REQUIRED_MESSAGE);
        }
    }

    private void validateExists(Long id) throws ValidationException {
        if (!treeNodeRepository.existsById(id)) {
            throw new ValidationException(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE);
        }
    }

    private void validateNotEmptyName(String name) throws ValidationException {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new ValidationException(Constants.ValidationConstants.NO_EMPTY_NAME_MESSAGE);
        }
    }

    private void validateNotEmptyContent(String content) throws ValidationException {
        if (Objects.isNull(content) || content.isBlank()) {
            throw new ValidationException(Constants.ValidationConstants.NO_EMPTY_CONTENT_MESSAGE);
        }
    }

}
