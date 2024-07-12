package lol.vedant.openguilds.level;

public class GuildLevel {

    private long exp;
    private long requiredExp;
    private String name;

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getRequiredExp() {
        return requiredExp;
    }

    public void setRequiredExp(long requiredExp) {
        this.requiredExp = requiredExp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
