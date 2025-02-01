package de.freeschool.api.messaging;

public class EmailBuilder {
    private StringBuilder mail;

    public EmailBuilder() {
        mail = new StringBuilder();
        mail.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3" +
                ".org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"); // Doctype
        mail.append("<meta http-equiv=\"Content-Type\" content=\"text/html charset=UTF-8\" />"); // UTF-8
        mail.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">"); // HTML
        mail.append("<body>");
    }

    public EmailBuilder append(String text) {
        mail.append(text);
        return this;
    }

    public EmailBuilder appendHeadline(String text, int size) {
        mail.append("<h" + size + ">");
        mail.append(text);
        mail.append("</h" + size + ">");
        return this;
    }

    public String toString() {
        mail.append("</body></html>");
        return mail.toString();
    }
}
