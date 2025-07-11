package ch.ubs.juniorlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "uploaded_files")
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", length = 100, nullable = false)
    private String filename;

    @Column(name = "content_type", length = 50, nullable = false)
    private String contentType;

    @Lob
    @Column(name = "data", columnDefinition = "VARBINARY(MAX)", nullable = false)
    private byte[] data;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
