--Lucas Woodbury
--COSC 3015
--HW #7
--10/6/16 

data Tree a = Leaf | Node (Tree a) a (Tree a) deriving (Eq,Ord,Show)

--Exercise 0.2
preorder:: Tree a -> [a]
preorder Leaf = []
preorder (Node t1 a t2) = a:(preorder t1 ++ preorder t2)

inorder::Tree a -> [a]
inorder Leaf = []
inorder (Node t1 a t2) = inorder t1 ++ (a:inorder t2)

postorder::Tree a -> [a]
postorder Leaf = []
postorder (Node t1 a t2) = inorder t1 ++ inorder t2 ++ [a]

--Exercise 0.3
reconstruct::Eq a => [a] -> [a] -> Tree a
reconstruct [] _ = Leaf
reconstruct (x:xs) ys = let yl = (takeWhile (\z -> not (z == x)) ys) in
                        let yr = if (dropWhile (\z -> not (z == x)) ys) == [] then []
                                 else tail (dropWhile (\z -> not (z == x)) ys) in
                        let xl = take (length yl) xs in
                        let xr = drop (length yl) xs in
                        Node (reconstruct xl yl) x (reconstruct xr yr)

