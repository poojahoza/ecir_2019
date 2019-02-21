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

        public void ValidateIndex()
        {
            if(indexParser.getIndexPath()==null)
            {
                CALLEXIT(-1);
            }
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

        public void ValidateQE() {
            if (searchParser.getNumberOfReturnedEntity() == 0) {
                System.out.println("Please pass the number of entity to be returned");
                CALLEXIT(-1);
            }
        }

        public void ValidateEntityDegree()
        {
            if(searchParser.getEntityIndLoc() == null)
            {
                System.out.println("Please pass the entity index location path");
                CALLEXIT(-1);
            }
        }

    }
    public static class ValidateClassifyCommands
    {
        private final RegisterCommands.CommandClassify classifyParser;

        public ValidateClassifyCommands(RegisterCommands.CommandClassify classifyParser)
        {
            this.classifyParser = classifyParser;
        }

        private void CALLEXIT(int status)
        {
            System.exit(status);
        }

    }

}
