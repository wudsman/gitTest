-- Lucas Woodbury
-- COSC 3015
-- HW 10
-- 10/26/16

import Parser

data BinOp = Add | Times
	deriving Show

data Exp = Constant Int | BinExp BinOp Exp Exp
	deriving Show
    
negint :: Parser Int
negint =  do symbol "("
             symbol "-"
             n <- natural
             symbol ")"
             return (-n)
                        
intp :: Parser Int
intp = (natural +++ negint)
    
    
expr :: Parser Exp
expr = do t <- term
          do symbol "+"
             e <- expr
             return (BinExp Add t e)
            +++ return t 
                 
term :: Parser Exp
term = do f <- factor
          do symbol "*"
             t <- term
             return (BinExp Times f t) 
            +++ return f     
     
factor :: Parser Exp
factor = do symbol "("
            e <- expr
            symbol ")"
            return e
           +++ (do n <- natural
                   return (Constant n))
    
    





