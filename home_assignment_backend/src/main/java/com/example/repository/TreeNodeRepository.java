package com.example.repository;

import com.example.model.TreeNode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreeNodeRepository extends JpaRepository<TreeNode, Long> {

    @EntityGraph(attributePaths = {"children", "children.children"}) //avoids n+1 query, so at least first children are preloaded
    List<TreeNode> findByParentNodeIsNull();
}
