package main.java.containers;


public class DBContainer {

    private String leadtext = null;
    private String outlinksIds = null;
    private String inlinksIds = null;
    private String entitiesId = null;

    public DBContainer(String leadtext,
                       String outlinksIds,
                       String inlinksIds,
                       String entities_id){
        this.entitiesId = entities_id;
        this.inlinksIds = inlinksIds;
        this.outlinksIds = outlinksIds;
        this.leadtext = leadtext;
    }

    public String getLeadtext(){
        return this.leadtext;
    }

    public String getOutlinksIds(){
        return this.outlinksIds;
    }

    public String getInlinksIds(){
        return this.inlinksIds;
    }

    public String getEntities_id(){
        return this.entitiesId;
    }
}
