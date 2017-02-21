module Parser where
    import Data.Char
    import Control.Applicative hiding (some,many)
    import Control.Monad

    newtype Parser a  = MkP(String ->  [(a,String)])
    
    parse :: Parser a -> String -> [(a,String)]
    parse (MkP f) i = f i

    fullParse :: Parser a -> String -> [a]
    fullParse p input  = let m = filter (\(_,out) -> null out) (parse  p input) in
                            if null m then
                                error "no sucessful parse"
                            else
                                fmap fst m

    instance Functor Parser where
      fmap f p = MkP (\s -> [(f v, rest) | (v, rest) <- parse p s])
  
    instance Applicative Parser where
       pure v = MkP(\ i -> [(v,i)])
       p1 <*> p2 = MkP (\s -> [(f v, rest') | (f, rest) <- parse p1 s, (v, rest') <- parse p2 rest])
 
    instance Monad Parser where

    -- (>>=) :: Parser a -> ( a -> Parser b) -> Parser b
        p >>= q = MkP f 
           where f s = case  parse p s of
                    [] -> []
                    m -> concat $ fmap (\(v,out) -> parse (q v) out) m
--                    [(v,out)] -> parse (q v) out

     -- return :: a -> Parser a
        return = pure     

    failure :: Parser a
    failure = MkP(\i -> [])
    zero = failure
    
    item :: Parser Char
    item = MkP(\i -> case i of
                     [] -> []
                     (x:xs) -> [(x,xs)])
                         
    p :: Parser (Char,Char)
    
    p =  do x <- item 
            item 
            y <- item
            return (x,y)

    (+++) :: Parser a -> Parser a -> Parser a
    p +++ q = MkP(\i -> case parse p i of
                        [] -> parse q i
                        m -> m)



    sat :: (Char -> Bool) -> Parser Char
    sat p = do x <- item
               if p x then return x else failure

    digit  :: Parser Char
    digit  = sat (\c -> c `elem` ['0'..'9'])

    lower  :: Parser Char
    lower  =  sat (\c -> c `elem` ['a'..'z'])

    upper  :: Parser Char
    upper  = sat (\c -> c `elem` ['A'..'Z'])

    letter :: Parser Char
    letter = (lower +++ upper)

    alphanum :: Parser Char
    alphanum = (letter +++ digit)

    char      :: Char -> Parser Char
    char x = sat (==x)

    string :: String -> Parser String
    string [] = return []
    string (x:xs) = do char x
                       string xs
                       return (x:xs)

    many :: Parser a -> Parser [a]
    many p = some p +++ return []
 
    some :: Parser a -> Parser [a]
    some p = do v <- p
                vs <- many p
                return (v:vs)

    ident :: Parser String
    ident = do x <- lower
               xs <- many alphanum
               return (x:xs)

    nat :: Parser Int
    nat = do xs <- some digit
             return (read xs)
 
    space :: Parser ()
    space = do many (char ' ')
               return ()

    token :: Parser a -> Parser a
    token p = do space
                 v <- p
                 space
                 return v

    identifier :: Parser String
    identifier = token ident

    natural :: Parser Int
    natural = token nat
  
    symbol :: String -> Parser String
    symbol xs = token (string xs)


    natlist :: Parser [Int]
    natlist = do symbol "["
                 n <- natural
                 ns <- many (do symbol ","
                                natural)
                 symbol "]"
                 return (n:ns)

    identifierList :: Parser [String]
    identifierList = do symbol "("
                        i <- identifier
                        is <- many (do symbol ","
                                       identifier)
                        symbol ")"
                        return (i:is)
                        
    

    
              
