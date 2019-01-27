package main.java.indexer;

/*Lucene imports*/
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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
    private IndexWriter indexWriter;
    private static int increment=0;

    public indexBuilder()
    {
        indexWriter = null;
    }

    /**
     * Prepares the indexwriter for use in searching later
     * 		 if its hasn't been created we parse the paragraph and pass back
     * @throws IOException
     */

    public void performIndex() throws IOException {

        //If we haven't created and indexwriter yet
        if (indexWriter == null)
        {

            //Get the path of the index
            Directory indexDir = FSDirectory.open(Paths.get(constants.DIRECTORY_NAME));

            //Create the configuration for the index
            IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            //Create the IndexWriter
            indexWriter = new IndexWriter(indexDir, config);

            //Parse the paragraphs and return the indexwriter with the corpus indexed
            parseParagraph(indexWriter);

        }
    }

    private void writePara(Data.Paragraph p,IndexWriter i)
    {

        System.out.println("Indexing "+ p.getParaId());
        Document doc = new Document();

        FieldType contentType = new FieldType();
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        contentType.setStored(true);
        contentType.setTokenized(true);
        contentType.setStoreTermVectors(true);

        //Then we add the paragraph id and the paragraph body for searching
        doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
        doc.add(new Field("body", p.getTextOnly(), contentType));

        //From here we add the document to the indexwriter

        try {
            indexWriter.addDocument(doc);
            increment++;

            //commit the Data after 50 paragraph

            if(increment % 50 ==0)
            {
                indexWriter.commit();
            }
        }catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Actually parses the paragraph from the mode parameters
     * @param indexWriter generated indexwriter to add doc to
     * @return indexwriter with docs added
     */
    private void parseParagraph(IndexWriter indexWriter)
    {

        BufferedInputStream bStream =null;

        try {
            bStream = new BufferedInputStream(new FileInputStream(new File(constants.FILE_NAME)));
        }catch(FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());

        }

        Iterable<Data.Paragraph> para = DeserializeData.iterableParagraphs(bStream);
        StreamSupport.stream(para.spliterator(), true)
                .forEach(paragraph ->
                {
                    writePara(paragraph,indexWriter);

                });


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