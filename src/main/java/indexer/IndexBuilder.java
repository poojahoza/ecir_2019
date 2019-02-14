package main.java.indexer;

/*Lucene imports*/
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexOptions;

/*TREC tools imports*/
import edu.unh.cs.treccar_v2.Data;

/*Project specific imports*/
import main.java.utils.IndexUtils;

/*Java specific imports*/
import java.io.*;
import java.util.List;
import java.util.stream.StreamSupport;


public class IndexBuilder
{
    private final IndexWriter indexWriter;
    private static int increment=0;

    public IndexBuilder(String indexDir) throws IOException {
        indexWriter = IndexUtils.createIndexWriter(indexDir);
    }

    private void writePara(Data.Paragraph p,IndexWriter i)
    {
        int incrementFactor=10000;
        System.out.println("Indexing "+ p.getParaId());
        Document doc = new Document();

        FieldType contentType = new FieldType();
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        contentType.setStored(true);
        contentType.setTokenized(true);
        contentType.setStoreTermVectors(true);

        List<String> entity = p.getEntitiesOnly();

        String entityString = String.join(" ",entity);
        //Entity string delimited with a spaces
        //Then we add the paragraph id and the paragraph body for searching
        doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
        doc.add(new Field("text", p.getTextOnly(), contentType));
        doc.add(new StringField("entities",entityString,Field.Store.YES));

        try {
            indexWriter.addDocument(doc);
            increment++;

            //commit the Data after incrementFactorVariable paragraph

            if(increment % incrementFactor ==0)
            {
                indexWriter.commit();
            }
        }catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Create an index of the cbor file passed as a parameter.
     */

    public void performIndex(String cborLoc) throws IOException
    {
        Iterable<Data.Paragraph> para = IndexUtils.createParagraphIterator(cborLoc);
        StreamSupport.stream(para.spliterator(), true)
                .forEach(paragraph ->
                {
                    writePara(paragraph,indexWriter);

                });
        closeIndexWriter();
    }


    /**
     * Closes the indexwriter so that we can use it in searching.
     * @throws IOException
     */
    private void closeIndexWriter()
    {
        if (indexWriter != null)
        {
            try
            {
                indexWriter.close();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

}