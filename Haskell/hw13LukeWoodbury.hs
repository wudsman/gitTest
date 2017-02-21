--Lucas Woodbury
--COSC 3015
--HW #13
--11/28/16
import System.Directory
import System.IO


cp :: FilePath -> FilePath -> IO ()
cp infile outfile = do { text <- readFile infile;
                        writeFile outfile text}
                         
pwd :: IO FilePath
pwd = getCurrentDirectory

ls :: IO [FilePath]
ls = do { wd <- pwd;
        getDirectoryContents wd}
        
cat:: FilePath -> IO ()
cat file = do { text <- readFile file;
              putStr text }
              




