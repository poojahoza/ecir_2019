package main.java.containers;

public class EntityContainer
{
    private String entityVal;
    private String entityId;

    public EntityContainer(String ent, String entityId)
    {
        this.entityVal=ent;
        this.entityId=entityId;
    }

    public String getEntityVal()
    {
        return entityVal;
    }
    public String getEntityId()
    {
        return entityId;
    }
}
