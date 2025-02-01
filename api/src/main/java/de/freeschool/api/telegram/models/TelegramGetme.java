package de.freeschool.api.telegram.models;

public class TelegramGetme {
    private boolean ok;
    private Result result;

    public TelegramGetme() {
    }

    public TelegramGetme(boolean ok, Result result) {
        this.ok = ok;
        this.result = result;
    }

    public boolean isOk() {
        return ok;
    }

    public Result getResult() {
        return result;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private long id;
        private boolean is_bot;
        private String first_name;
        private String username;
        private boolean can_join_groups;
        private boolean can_read_all_group_messages;
        private boolean supports_inline_queries;


        public Result() {
        }

        public Result(int id, boolean is_bot, String first_name, String username, boolean can_join_groups,
                      boolean can_read_all_group_messages, boolean supports_inline_queries) {
            this.id = id;
            this.is_bot = is_bot;
            this.first_name = first_name;
            this.username = username;
            this.can_join_groups = can_join_groups;
            this.can_read_all_group_messages = can_read_all_group_messages;
            this.supports_inline_queries = supports_inline_queries;
        }

        public long getId() {
            return id;
        }

        public boolean isIs_bot() {
            return is_bot;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getUsername() {
            return username;
        }

        public boolean can_join_groups() {
            return can_join_groups;
        }

        public boolean can_read_all_group_messages() {
            return can_read_all_group_messages;
        }

        public boolean supports_inline_queries() {
            return supports_inline_queries;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setIs_bot(boolean is_bot) {
            this.is_bot = is_bot;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setCan_join_groups(boolean can_join_groups) {
            this.can_join_groups = can_join_groups;
        }

        public void setCan_read_all_group_messages(boolean can_read_all_group_messages) {
            this.can_read_all_group_messages = can_read_all_group_messages;
        }

        public void setSupports_inline_queries(boolean supports_inline_queries) {
            this.supports_inline_queries = supports_inline_queries;
        }


    }
}
