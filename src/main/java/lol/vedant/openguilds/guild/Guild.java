package lol.vedant.openguilds.guild;

import java.util.List;

public class Guild {

    private final int id;
    private String displayName;
    private String description;
    private String tag;
    private List<GuildPlayer> members;
    private GuildPlayer leader;

    public Guild(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GuildPlayer> getMembers() {
        return members;
    }

    public void setMembers(List<GuildPlayer> members) {
        this.members = members;
    }

    public GuildPlayer getLeader() {
        return leader;
    }

    public void setLeader(GuildPlayer leader) {
        this.leader = leader;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
