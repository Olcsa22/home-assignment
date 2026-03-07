package com.example.controller;

import com.example.model.request.CreateTreeNodeRequest;
import com.example.model.request.ReorganizeTreeNodeRequest;
import com.example.model.response.TreeNodeResponse;
import com.example.repository.TreeNodeRepository;
import com.example.validation.Constants;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TreeNodeControllerTest {
    
    private static final String DEFAULT_NODE_NAME = "Name";
    private static final String DEFAULT_NODE_CONTENT = "Content";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    TreeNodeRepository treeNodeRepository;


    @AfterEach
    void cleanUp(){
        treeNodeRepository.deleteAll();
    }

    @Test
    void create_should_create_entity() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                                                                   .id(1L)
                                                                   .content(DEFAULT_NODE_CONTENT)
                                                                   .name(DEFAULT_NODE_NAME)
                                                                   .build();
        String requestJson = mapper.writeValueAsString(createRequest);


        // Then

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(DEFAULT_NODE_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(DEFAULT_NODE_CONTENT))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.children").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.matchesFilter").doesNotExist());

    }

    @Test
    void create_should_throw_exception_for_already_existing() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(1L)
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .build();
        String requestJson = mapper.writeValueAsString(createRequest);


        // Then

        preCreateEntry(createRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NO_RESERVED_ID_MESSAGE));


    }

    @Test
    void create_should_throw_exception_for_id() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .build();
        String requestJson = mapper.writeValueAsString(createRequest);


        // Then

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.ID_IS_REQUIRED_MESSAGE));
    }

    @Test
    void create_should_throw_exception_for_content() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(1L)
                .name(DEFAULT_NODE_NAME)
                .build();
        String requestJson = mapper.writeValueAsString(createRequest);


        // Then

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NO_EMPTY_CONTENT_MESSAGE));
    }

    @Test
    void create_should_throw_exception_for_name() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(1L)
                .content(DEFAULT_NODE_CONTENT)
                .build();
        String requestJson = mapper.writeValueAsString(createRequest);


        // Then

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NO_EMPTY_NAME_MESSAGE));
    }

    @Test
    void create_should_throw_exception_for_parent_id() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(1L)
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .parentId(2L)
                .build();
        String requestJson = mapper.writeValueAsString(createRequest);


        // Then

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NOT_EXISTING_PARENT_MESSAGE));
    }

    @Test
    void delete_should_be_successful() throws Exception {
        // Given
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(1L)
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .build();



        // Then

        preCreateEntry(createRequest);

        mockMvc.perform(MockMvcRequestBuilders.delete("/nodes/delete/1"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void delete_should_throw_exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/nodes/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE));
    }

    @Test
    void list_tree_should_be_successful() throws Exception {
        // Given
        precreateBasicTree();

        TreeNodeResponse expectedResponse = buildBasicTreeResponse();
        String expectedResponseJson = mapper.writeValueAsString(List.of(expectedResponse));

        // Then

        mockMvc.perform(MockMvcRequestBuilders.get("/nodes/listTree"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(expectedResponseJson));
    }

    @Test
    void reorganize_tree_should_succeed() throws Exception {
        // Given
        precreateBasicTree();
        createRoot(4, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);

        ReorganizeTreeNodeRequest reorganizeTreeNodeRequest = ReorganizeTreeNodeRequest.builder()
                .targetId(2L)
                .newParentId(4L)
                .build();

        TreeNodeResponse rootOne = TreeNodeResponse.builder()
                .id(1L)
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .isRoot(true)
                .build();

        TreeNodeResponse rootTwo = TreeNodeResponse.builder()
                .id(4L)
                .isRoot(true)
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .build();

        TreeNodeResponse childOne = TreeNodeResponse.builder()
                .id(2L)
                .name(DEFAULT_NODE_NAME)
                .content(DEFAULT_NODE_CONTENT)
                .isRoot(false)
                .parentId(4L)
                .children(List.of())
                .build();

        TreeNodeResponse childTwo = TreeNodeResponse.builder()
                .id(3L)
                .name(DEFAULT_NODE_NAME)
                .content(DEFAULT_NODE_CONTENT)
                .isRoot(false)
                .parentId(1L)
                .children(List.of())
                .build();

        rootOne.setChildren(List.of(childTwo));
        rootTwo.setChildren(List.of(childOne));

        String requestJson = mapper.writeValueAsString(reorganizeTreeNodeRequest);
        String responseJson = mapper.writeValueAsString(List.of(rootOne, rootTwo));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/nodes/reorganize").content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(responseJson));
    }

    @Test
    void reorganize_should_throw_exception_for_Target_id() throws Exception {
        // Given
        precreateBasicTree();
        createRoot(4, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);

        ReorganizeTreeNodeRequest reorganizeTreeNodeRequest = ReorganizeTreeNodeRequest.builder()
                .targetId(7L)
                .newParentId(4L)
                .build();


        String requestJson = mapper.writeValueAsString(reorganizeTreeNodeRequest);

        // Then

        mockMvc.perform(MockMvcRequestBuilders.post("/nodes/reorganize").content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE));
    }

    @Test
    void reorganize_should_throw_exception_for_parent_id() throws Exception {
        // Given
        precreateBasicTree();
        createRoot(4, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);

        ReorganizeTreeNodeRequest reorganizeTreeNodeRequest = ReorganizeTreeNodeRequest.builder()
                .targetId(2L)
                .newParentId(7L)
                .build();


        String requestJson = mapper.writeValueAsString(reorganizeTreeNodeRequest);

        // Then

        mockMvc.perform(MockMvcRequestBuilders.post("/nodes/reorganize").content(requestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NOT_EXISTING_PARENT_MESSAGE));
    }

    @Test
    void load_content_by_id_should_succeed() throws Exception {
        // Given
        String randomContent = RandomString.make();
        createRoot(1, DEFAULT_NODE_NAME, randomContent);

        // Then

        mockMvc.perform(MockMvcRequestBuilders.get("/nodes/contentById/1"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(randomContent));
    }

    @Test
    void load_content_by_id_should_fail() throws Exception {
        // Given
        createRoot(1, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);

        // Then

        mockMvc.perform(MockMvcRequestBuilders.get("/nodes/contentById/2"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(Constants.ValidationConstants.NOT_EXISTING_ID_MESSAGE));
    }

    @Test
    void find_by_content_should_mark_results_accurately() throws Exception {
        // Given
        String customName = "A name that contains fuzz";
        String customContent = "A content that contains fuzz";
        createRoot(1, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);
        createChild(2, 1, customName, DEFAULT_NODE_CONTENT);
        createChild(3, 1, DEFAULT_NODE_NAME, customContent);

        TreeNodeResponse rootOne = TreeNodeResponse.builder()
                .id(1L)
                .content(DEFAULT_NODE_CONTENT)
                .name(DEFAULT_NODE_NAME)
                .isRoot(true)
                .matchesFilter(false)
                .build();

        TreeNodeResponse childOne = TreeNodeResponse.builder()
                .id(2L)
                .name(customName)
                .content(DEFAULT_NODE_CONTENT)
                .isRoot(false)
                .parentId(1L)
                .children(List.of())
                .matchesFilter(true)
                .build();

        TreeNodeResponse childTwo = TreeNodeResponse.builder()
                .id(3L)
                .name(DEFAULT_NODE_NAME)
                .content(customContent)
                .isRoot(false)
                .parentId(1L)
                .children(List.of())
                .matchesFilter(true)
                .build();

        rootOne.setChildren(List.of(childOne, childTwo));

        String resultJson = mapper.writeValueAsString(List.of(rootOne));

        // Then

        mockMvc.perform(MockMvcRequestBuilders.get("/nodes/byContent?content=fuzz"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(resultJson));

    }

    private void precreateBasicTree() throws Exception {
        createRoot(1, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);
        createChild(2, 1, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);
        createChild(3, 1, DEFAULT_NODE_NAME, DEFAULT_NODE_CONTENT);
    }

    private void createRoot(long id, String name, String content) throws Exception {
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(id)
                .content(content)
                .name(name)
                .build();

        preCreateEntry(createRequest);
    }

    private void createChild(long id, long parentId, String name, String content) throws Exception {
        CreateTreeNodeRequest createRequest = CreateTreeNodeRequest.builder()
                .id(id)
                .content(content)
                .name(name)
                .parentId(parentId)
                .build();

        preCreateEntry(createRequest);
    }

    private TreeNodeResponse buildBasicTreeResponse(){
        TreeNodeResponse root = TreeNodeResponse.builder()
                .id(1L)
                .name(DEFAULT_NODE_NAME)
                .content(DEFAULT_NODE_CONTENT)
                .isRoot(true)
                .build();

        TreeNodeResponse childOne = TreeNodeResponse.builder()
                                                    .id(2L)
                                                    .name(DEFAULT_NODE_NAME)
                                                    .content(DEFAULT_NODE_CONTENT)
                                                    .parentId(1L)
                                                    .children(List.of())
                                                    .isRoot(false)
                                                    .build();

        TreeNodeResponse childTwo = TreeNodeResponse.builder()
                                                    .id(3L)
                                                    .name(DEFAULT_NODE_NAME)
                                                    .content(DEFAULT_NODE_CONTENT)
                                                    .parentId(1L)
                                                    .children(List.of())
                                                    .isRoot(false)
                                                    .build();

        root.setChildren(List.of(new TreeNodeResponse[]{childOne, childTwo}));

        return root;
    }

    private void preCreateEntry(CreateTreeNodeRequest createTreeNodeRequest) throws Exception {
        String requestJson = mapper.writeValueAsString(createTreeNodeRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/nodes/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

}
