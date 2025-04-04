package edu.cs.hogwartsartifactsonline.artifact.dto;

import edu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
import jakarta.validation.constraints.NotEmpty;

//this was created to avoid infinite recursion for artifactId = 1250808601744904191
public record ArtifactDto(
        String id,

        @NotEmpty(message = "Name is required.")
        String name,

        @NotEmpty(message = "Description is required.")
        String description,

        @NotEmpty(message = "ImageUrl is required.")
        String imageUrl,

        WizardDto owner
) {
}
