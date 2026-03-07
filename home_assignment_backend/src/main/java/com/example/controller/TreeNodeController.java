package com.example.controller;

import com.example.exception.ValidationException;
import com.example.model.response.SingleValueResponse;
import com.example.model.response.TreeNodeResponse;
import com.example.model.request.CreateTreeNodeRequest;
import com.example.model.request.ReorganizeTreeNodeRequest;
import com.example.model.request.UpdateTreeNodeRequest;
import com.example.service.TreeNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.util.List;

@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor
public class TreeNodeController {

    private final TreeNodeService treeNodeService;


    @PutMapping("/create")
    public TreeNodeResponse create(@RequestBody CreateTreeNodeRequest dto) throws ValidationException {
        return treeNodeService.create(dto);
    }

    @PatchMapping("/update")
    public TreeNodeResponse update(@RequestBody UpdateTreeNodeRequest dto) throws ValidationException {
        return treeNodeService.update(dto);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) throws ValidationException {
        treeNodeService.delete(id);
    }

    @GetMapping("/listTree")
    public List<TreeNodeResponse> listTree(){
        return treeNodeService.listTree();
    }

    @PostMapping("/reorganize")
    public List<TreeNodeResponse> reorganizeTree(@RequestBody ReorganizeTreeNodeRequest reorganizeTreeNodeRequest) throws ValidationException {
        return treeNodeService.reorganize(reorganizeTreeNodeRequest);
    }

    @GetMapping("/contentById/{id}")
    public SingleValueResponse loadContentById(@PathVariable Long id) throws ValidationException {
        return treeNodeService.loadContentById(id);
    }

    @GetMapping("/byContent")
    public List<TreeNodeResponse> findByContent(@RequestParam String content){
        return treeNodeService.findByContent(content);
    }

}
