public class Message {
    private long id;
    private String author;
    private String content;
    private long replyToId;
    private boolean republished;

    public Message(long id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public long getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(long replyToId) {
        this.replyToId = replyToId;
    }

    public boolean isRepublished() {
        return republished;
    }

    public void setRepublished(boolean republished) {
        this.republished = republished;
    }
}

