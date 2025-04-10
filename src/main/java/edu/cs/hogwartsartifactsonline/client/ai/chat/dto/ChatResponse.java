package edu.cs.hogwartsartifactsonline.client.ai.chat.dto;

import java.util.List;

/**
 * The ChatResponse record holds a chat completion returned by an AI model, in response to a provided prompt.
 * A response may have multiple choices. Each choice represents a possible output generated by the AI model from a single prompt.
 * Multiple choices are offered primarily to provide diversity and a range of options for the user.
 * This feature is particularly useful in scenarios where there isn't a single correct answer or where different
 * perspectives or styles of responses might be valuable.
 * By default, there is only one choice and the generated text can be extracted with:
 * chatResponse.choices().get(0).message().content().
 *
 * @param choices a list of chat completion choices
 */
public record ChatResponse(List<Choice> choices) {
}