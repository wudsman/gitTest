-- Lucas Woodbury
-- COSC 4015
-- HW #4
-- 9/23/16

pull _ [] = error "empty list"
pull n (x:xs) = if n <= 0 then [] 
                else if n > length (x:xs) then (x:xs) 
                else [x] ++ pull (n-1) xs

chop _ [] = error "empty list"
chop n (x:xs) = if n <= 0 then (x:xs)
                else if n > length (x:xs) then []
                else chop (n-1) xs

get _ [] = error "empty list"
get k (x:xs) = if k <= 0 then x
               else get (k-1) xs  



test1 k xs = pull k xs == xs -- iff k>= length xs

test2 k xs = pull k xs == [] -- iff k<=0

test3 k xs = (pull k xs ++ chop k xs) == xs

test4 k xs = get 0 xs == head xs

test5 k xs = get k xs == head (chop k xs)

test6 k xs = get k xs == get (length xs - (k+1)) (reverse xs) --iff 0 <= k < length xs


