package main.java.indexer;

/*Lucene imports*/
import main.java.util.IndexUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/*TREC tools imports*/
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.Data;

/*Project specific imports*/
import main.java.util.constants;

/*Java specific imports*/
import java.io.*;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;


public class indexBuilder
{
    private final IndexWriter indexWriter;

    public indexBuilder(String indexDir) { indexWriter = IndexUtils.createIndexWriter(indexDir); }


    /**
     * Prepares the indexwriter for use in searching later.
     * @throws IOException
     */
    public void performIndex(String cborLoc) throws IOException {

        int counter = 0;
        for (Data.Paragraph p : IndexUtils.createParagraphIterator(cborLoc)) {
            Document doc = new Document();
            doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
            doc.add(new TextField("text", p.getTextOnly(), Field.Store.YES));
            indexWriter.addDocument(doc);
            counter++;
            if (counter % 20 == 0) {
                System.out.println("Commited data");
                indexWriter.commit();
            }
        }
        closeIndexWriter();
    }

    /**
     * Closes the indexwriter so that we can use it in searching
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