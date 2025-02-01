package de.freeschool.api.models.response;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * MetaInfoResponse
 */

public class MetaInfoResponse {

    private String schoolName;

    private String schoolUrl;


    public MetaInfoResponse schoolName(String schoolName) {
        this.schoolName = schoolName;
        return this;
    }

    /**
     * Name of this \"bank\"
     *
     * @return bankName
     */

    @Schema(name = "schoolName", description = "Name of this \"school\"", requiredMode =
            Schema.RequiredMode.NOT_REQUIRED)
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public MetaInfoResponse schoolUrl(String schoolUrl) {
        this.schoolUrl = schoolUrl;
        return this;
    }

    /**
     * School routing URL of this instance
     *
     * @return schoolUrl
     */

    @Schema(name = "schoolUrl", description = "School routing URL of this instance", requiredMode =
            Schema.RequiredMode.NOT_REQUIRED)
    public String getSchoolUrl() {
        return schoolUrl;
    }

    public void setSchoolUrl(String schoolUrl) {
        this.schoolUrl = schoolUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetaInfoResponse metaInfoResponse = (MetaInfoResponse) o;
        return Objects.equals(this.schoolName, metaInfoResponse.schoolName) &&
                Objects.equals(this.schoolUrl, metaInfoResponse.schoolUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schoolName, schoolUrl);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MetaInfoResponse {\n");
        sb.append("    bankName: ").append(toIndentedString(schoolName)).append("\n");
        sb.append("    bankUrl: ").append(toIndentedString(schoolUrl)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

