package io.collap.cache;

public class Fragment {

    /**
     * This corresponds to the head value in Response.
     */
    private String head;

    /**
     * This corresponds to the content value in Response.
     */
    private String content;

    public Fragment (String head, String content) {
        this.head = head;
        this.content = content;
    }

    public String getHead () {
        return head;
    }

    public String getContent () {
        return content;
    }

}
