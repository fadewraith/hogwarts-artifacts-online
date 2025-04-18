package edu.cs.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.cs.hogwartsartifactsonline.artifact.converter.ArtifactDtoToArtifactConverter;
import edu.cs.hogwartsartifactsonline.artifact.converter.ArtifactToArtifactDtoConverter;
import edu.cs.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import edu.cs.hogwartsartifactsonline.client.imagestorage.ImageStorageClient;
import edu.cs.hogwartsartifactsonline.system.Result;
import edu.cs.hogwartsartifactsonline.system.StatusCode;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;
    private final MeterRegistry meterRegistry;
    private final ImageStorageClient imageStorageClient;

    public ArtifactController(ArtifactService artifactService, ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter, ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter, MeterRegistry meterRegistry, ImageStorageClient imageStorageClient) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
        this.meterRegistry = meterRegistry;
        this.imageStorageClient = imageStorageClient;
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable("artifactId") String artifactId) {
        Artifact artifact = this.artifactService.findById(artifactId);
        meterRegistry.counter("artifact.id." + artifactId).increment();
        ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(artifact);
        return new Result(true, StatusCode.SUCCESS, "Find one success", artifactDto);
    }

    @GetMapping
    public Result findAllArtifacts() {
        List<Artifact> foundArtifacts = this.artifactService.findAll();
//        Convert foundArtifacts to a list of artifactDtos
//        List<ArtifactDto> artifactDtos = foundArtifacts.stream().map(foundArtifact -> artifactToArtifactDtoConverter.convert(foundArtifact))
//                .collect(Collectors.toList());
        List<ArtifactDto> artifactDtos = foundArtifacts.stream().map(this.artifactToArtifactDtoConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Find All Success", artifactDtos);
    }

    @PostMapping
    public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
        Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = this.artifactService.save(newArtifact);
        ArtifactDto savedArtifactDto = this.artifactToArtifactDtoConverter.convert(savedArtifact);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedArtifactDto);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(@PathVariable("artifactId") String artifactId, @Valid @RequestBody ArtifactDto artifactDto) {
        Artifact convertedToArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact updatedArtifact = this.artifactService.update(artifactId, convertedToArtifact);
        ArtifactDto updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updatedArtifact);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedArtifactDto);
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable("artifactId") String artifactId) {
        this.artifactService.delete(artifactId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

    @GetMapping("/summary")
    public Result summarizeArtifacts() throws JsonProcessingException {
        List<Artifact> foundArtifacts = this.artifactService.findAll();
        // Convert foundArtifacts to a list of artifactDtos
        List<ArtifactDto> artifactDtos = foundArtifacts.stream()
                .map(this.artifactToArtifactDtoConverter::convert)
                .collect(Collectors.toList());
        String artifactSummary = this.artifactService.summarize(artifactDtos);
        return new Result(true, StatusCode.SUCCESS, "Summarize Success", artifactSummary);
    }

    @GetMapping("/pagination")
    public Result findAllArtifactsPagination(Pageable pageable) {
        Page<Artifact> artifactPage = this.artifactService.findAllPagination(pageable);
        Page<ArtifactDto> artifactDtoPage = artifactPage.map(this.artifactToArtifactDtoConverter::convert);
        return new Result(true, StatusCode.SUCCESS, "Find All Success", artifactDtoPage);
    }

   @PostMapping("/search")
   public Result findArtifactsByCriteria(@RequestBody Map<String, String> searchCriteria, Pageable pageable) {
        Page<Artifact> artifactPage = this.artifactService.findByCriteria(searchCriteria, pageable);
        Page<ArtifactDto> artifactDtoPage = artifactPage.map(this.artifactToArtifactDtoConverter::convert);
        return new Result(true, StatusCode.SUCCESS, "Search Success", artifactDtoPage);
   }

   @PostMapping("/images")
    public Result uploadImages(@RequestParam("containerName") String containerName, @RequestParam("file") MultipartFile file) throws IOException {
          try(InputStream inputStream = file.getInputStream()) {
              String imageUrl = this.imageStorageClient.uploadImage(containerName, file.getOriginalFilename(), inputStream, file.getSize());
              return new Result(true, StatusCode.SUCCESS, imageUrl);
          }
    }

}
