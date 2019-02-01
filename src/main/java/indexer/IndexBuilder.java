package main.java.indexer;

/*Lucene imports*/
import main.java.utils.IndexUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexOptions;

/*TREC tools imports*/
import edu.unh.cs.treccar_v2.Data;

/*Project specific imports*/

/*Java specific imports*/
import java.io.*;


public class IndexBuilder
{
    private final IndexWriter indexWriter;

    public IndexBuilder(String indexDir) throws IOException { indexWriter = IndexUtils.createIndexWriter(indexDir); }


    /**
     * Create an index of the cbor file passed as a parameter.
     * @throws IOException
     */
    public void performIndex(String cborLoc) throws IOException {

        int increment = 0;
        for (Data.Paragraph p : IndexUtils.createParagraphIterator(cborLoc)) {
            System.out.println("Indexing "+ p.getParaId());
            Document doc = new Document();

            FieldType contentType = new FieldType();
            contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            contentType.setStored(true);
            contentType.setTokenized(true);
            contentType.setStoreTermVectors(true);

            // Then we add the paragraph id and the paragraph body for searching.
            doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
            doc.add(new Field("text", p.getTextOnly(), contentType));
            indexWriter.addDocument(doc);
            increment++;
            if (increment % 50 == 0) {
                indexWriter.commit();
            }
        }
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