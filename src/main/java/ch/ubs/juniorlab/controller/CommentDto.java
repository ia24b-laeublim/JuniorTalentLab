package ch.ubs.juniorlab.controller; // Oder ch.ubs.juniorlab.dto

public class CommentDto {

    private String content;
    private String authorName; // Wir senden nur den Namen des Autors, nicht das ganze Objekt

    // Konstruktor
    public CommentDto(String content, String authorName) {
        this.content = content;
        this.authorName = authorName;
    }

    // Getter
    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return authorName;
    }
}