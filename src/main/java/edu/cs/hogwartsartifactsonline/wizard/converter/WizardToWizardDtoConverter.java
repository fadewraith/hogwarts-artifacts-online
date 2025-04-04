package edu.cs.hogwartsartifactsonline.wizard.converter;

import edu.cs.hogwartsartifactsonline.wizard.Wizard;
import edu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {

    @Override
    public WizardDto convert(Wizard source) {
//        size calculation should belong to Wizard class, not to WizardDto
        WizardDto wizardDto = new WizardDto(
                source.getId(),
                source.getName(),
                source.getNumberOfArtifacts()
        );
        return wizardDto;
    }
}
