package main.java.commandparser;

public class ValidateCommands
{

    public static class ValidateIndexCommands
    {
        RegisterCommands.CommandIndex indexParser=null;
        public ValidateIndexCommands(RegisterCommands.CommandIndex indexParser)
        {
            this.indexParser = indexParser;
        }
    }



    public static class ValidateSearchCommands
    {

        private RegisterCommands.CommandSearch searchParser = null;

        public ValidateSearchCommands(RegisterCommands.CommandSearch searchParser)
        {
            this.searchParser = searchParser;
        }

        public void ValidateReRank()
        {
            if(searchParser.getWordEmbeddingFile()== null)
            {
                System.out.println("Please pass the word Embeddings file");
                System.exit(-1);
            }
        }



    }

}
