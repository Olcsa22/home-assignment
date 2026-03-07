package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tree_node")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeNode {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_node_id")
    private TreeNode parentNode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentNode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TreeNode> children;

    @Transient
    public boolean isRoot(){
        return Objects.isNull(this.getParentNode());
    }



}
