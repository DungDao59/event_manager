package group_3.model;

/**
 * @author Group 3
 *
 * SessionMaterial represents educational or supplementary material associated with a session.
 * Maps to the session_material table in the database.
 * 
 * Supports both binary content (content_data) and URL-based content (content_url).
 *
 */
public class SessionMaterial {
    private int materialId;
    private int sessionId;
    private String title;
    private String description;
    private String fileType;
    private byte[] contentData;  // Binary content (BYTEA in PostgreSQL)
    private String contentUrl;   // URL to external content

    /**
     * Default constructor.
     */
    public SessionMaterial() {
    }

    /**
     * Constructor for URL-based material.
     * 
     * @param materialId unique identifier for the material
     * @param sessionId  the session this material belongs to
     * @param title      title of the material
     * @param description description of the material
     * @param fileType   type of file (e.g., "pdf", "video", "slides")
     * @param contentUrl URL to the external content
     */
    public SessionMaterial(int materialId, int sessionId, String title, 
                          String description, String fileType, String contentUrl) {
        this.materialId = materialId;
        this.sessionId = sessionId;
        this.title = title;
        this.description = description;
        this.fileType = fileType;
        this.contentUrl = contentUrl;
    }

    /**
     * Full constructor with all attributes including binary content.
     * 
     * @param materialId  unique identifier for the material
     * @param sessionId   the session this material belongs to
     * @param title       title of the material
     * @param description description of the material
     * @param fileType    type of file (e.g., "pdf", "video", "slides")
     * @param contentData binary content data
     * @param contentUrl  URL to external content (alternative to binary)
     */
    public SessionMaterial(int materialId, int sessionId, String title, 
                          String description, String fileType, 
                          byte[] contentData, String contentUrl) {
        this.materialId = materialId;
        this.sessionId = sessionId;
        this.title = title;
        this.description = description;
        this.fileType = fileType;
        this.contentData = contentData;
        this.contentUrl = contentUrl;
    }

    // Getters and Setters

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getContentData() {
        return contentData;
    }

    public void setContentData(byte[] contentData) {
        this.contentData = contentData;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    /**
     * Check if this material has binary content.
     * 
     * @return true if content_data is present
     */
    public boolean hasBinaryContent() {
        return contentData != null && contentData.length > 0;
    }

    /**
     * Check if this material has URL-based content.
     * 
     * @return true if content_url is present
     */
    public boolean hasUrlContent() {
        return contentUrl != null && !contentUrl.isEmpty();
    }

   
}
