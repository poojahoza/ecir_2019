package main.java.containers;

public class Container
{
    private int  docID;
    private float scoreVal;
    private Integer ranking;
    private EntityContainer entity;

    public Container(float scoreVal,Integer ranking,int docID)
    {
        this.docID=docID;
        this.scoreVal=scoreVal;
        this.ranking=ranking;
    }

    public void addEntityContainer(EntityContainer e)
    {
        this.entity = e;
    }

    public int  getDocID() {return docID;}
    public float getScore(){return scoreVal;}
    public Integer getRanking() {return ranking;};
    public String getEntity() { return entity.getEntityVal();
    }
}
