package main.java.containers;

import org.jetbrains.annotations.NotNull;

public class Container implements Comparable
{
    private int  docID;
    private Double scoreVal;
    private EntityContainer entity;

    public Container(Double scoreVal,int docID)
    {
        this.docID=docID;
        this.scoreVal=scoreVal;
    }

    public void addEntityContainer(EntityContainer e)
    {
        this.entity = e;
    }

    public int  getDocID() {return docID;}
    public Double getScore(){return scoreVal;}

    public EntityContainer getEntity() { return entity;}
    public void setScoreVal(Double scoreVal)
    {
        this.scoreVal=scoreVal;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return this.scoreVal.compareTo(((Container)o).scoreVal);
    }
}
