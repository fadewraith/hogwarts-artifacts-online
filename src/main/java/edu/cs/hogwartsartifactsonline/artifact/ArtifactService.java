package edu.cs.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cs.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import edu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.cs.hogwartsartifactsonline.client.ai.chat.ChatClient;
import edu.cs.hogwartsartifactsonline.client.ai.chat.dto.ChatRequest;
import edu.cs.hogwartsartifactsonline.client.ai.chat.dto.ChatResponse;
import edu.cs.hogwartsartifactsonline.client.ai.chat.dto.Message;
import edu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;
    private final ChatClient chatClient;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker, ChatClient chatClient) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
        this.chatClient = chatClient;
    }

    @Observed(name = "artifact", contextualName = "findByIdService")
    public Artifact findById(String artifactId) {
        return this.artifactRepository.findById(artifactId).orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }

    @Timed("findAllArtifactsService.time")
    public List<Artifact> findAll() {
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact artifact) {

        return this.artifactRepository.findById(artifactId)
                .map(oldArtifact -> {
                    oldArtifact.setName(artifact.getName());
                    oldArtifact.setDescription(artifact.getDescription());
                    oldArtifact.setImageUrl(artifact.getImageUrl());
                    return this.artifactRepository.save(oldArtifact);
                })
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));


    }

    public void delete(String artifactId) {
        this.artifactRepository.findById(artifactId).orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
        this.artifactRepository.deleteById(artifactId);
    }

    /**
     * Returns a summary of the existing artifacts. This method is responsible for preparing the AiChatRequest and parsing the AiChatResponse.
     *
     * @param artifactDtos a list of artifact dtos to be summarized
     * @return a summary of the existing artifacts
     * @throws JsonProcessingException
     */
    public String summarize(List<ArtifactDto> artifactDtos) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(artifactDtos);

        // Prepare the messages for summarizing.
        List<Message> messages = List.of(
                new Message("system", "Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description, and the ownership information. Don't mention that the summary is from a given JSON array."),
                new Message("user", jsonArray)
        );

        ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", messages);

        ChatResponse chatResponse = this.chatClient.generate(chatRequest); // Tell chatClient to generate a text summary based on the given chatRequest.

        // Retrieve the AI-generated text and return to the controller.
        return chatResponse.choices().get(0).message().content();
    }

    public Page<Artifact> findAllPagination(Pageable pageable) {
        return this.artifactRepository.findAll(pageable);
    }

    /**
     * Finds artifacts based on the provided search criteria and returns a paginated result.
     * The method dynamically builds a JPA Specification based on the search criteria map.
     * Supported criteria keys:
     * - "id": Filters artifacts by their ID.
     * - "name": Filters artifacts by a partial match in their name (case-insensitive).
     * - "description": Filters artifacts by a partial match in their description (case-insensitive).
     * - "ownerName": Filters artifacts by the owner's name (case-insensitive).
     *
     * @param searchCriteria A map containing the search criteria as key-value pairs.
     * @param pageable       Pagination information.
     * @return A paginated list of artifacts matching the search criteria.
     */
    public Page<Artifact> findByCriteria(Map<String, String> searchCriteria, Pageable pageable) {
        Specification<Artifact> specification = Specification.where(null);

        if(StringUtils.hasLength(searchCriteria.get("id"))) {
            specification = specification.and(ArtifactSpecs.hasId(searchCriteria.get("id")));
        }

        if(StringUtils.hasLength(searchCriteria.get("name"))) {
            specification = specification.and(ArtifactSpecs.containsName(searchCriteria.get("name")));
        }

        if(StringUtils.hasLength(searchCriteria.get("description"))) {
            specification = specification.and(ArtifactSpecs.containsDescription(searchCriteria.get("description")));
        }

        if(StringUtils.hasLength(searchCriteria.get("ownerName"))) {
            specification = specification.and(ArtifactSpecs.hasOwnerName(searchCriteria.get("ownerName")));
        }
        return this.artifactRepository.findAll(specification, pageable);
    }
}
