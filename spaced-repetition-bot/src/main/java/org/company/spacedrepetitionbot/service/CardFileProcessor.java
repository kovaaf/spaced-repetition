package org.company.spacedrepetitionbot.service;

import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.Deck;

import java.nio.file.Path;
import java.util.List;

public interface CardFileProcessor {
    List<Card> parseMarkdownFile(Path file);
    void processFile(Deck deck, Path filePath);
}
