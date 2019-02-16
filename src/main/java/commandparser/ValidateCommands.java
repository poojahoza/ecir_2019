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

        private void CALLEXIT(int status)
        {
            System.exit(status);
        }

    }


    public static class ValidateSearchCommands
    {

        private RegisterCommands.CommandSearch searchParser = null;

        public ValidateSearchCommands(RegisterCommands.CommandSearch searchParser)
        {
            this.searchParser = searchParser;
        }


        private void CALLEXIT(int status)
        {
            System.exit(status);
        }

        public void ValidateReRank()
        {
            if(searchParser.getWordEmbeddingFile()== null)
            {
                System.out.println("Please pass the word Embeddings file");
                CALLEXIT(-1);
            }

            if(searchParser.getDimension()==0)
            {
                System.out.println("Please pass the dimension of the word vectors");
                CALLEXIT(-1);
            }

        }

    }

    public static class ValidateIndexHamSpamCommands
    {
        private final RegisterCommands.IndexHamSpam indexHamSpamParser;

        public ValidateIndexHamSpamCommands(RegisterCommands.IndexHamSpam indexHamSpamParser)
        {
            this.indexHamSpamParser = indexHamSpamParser;
        }

        private void CALLEXIT(int status)
        {
            System.exit(status);
        }

    }

    public static class ValidateFilterCommands
    {
        private final RegisterCommands.CommandFilter filterParser;

        public ValidateFilterCommands(RegisterCommands.CommandFilter filterParser)
        {

            this.filterParser = filterParser;
        }

        private void CALLEXIT(int status)
        {

            System.exit(status);
        }

    }

}
