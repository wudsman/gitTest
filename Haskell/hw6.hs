-- Lucas Woodbury
-- COSC 3015
-- HW 6
-- 9/30/16

-- Problem 1.1
filter_acc::(a -> Bool) -> [a] -> [a] -> [a]
filter_acc p [] ys = ys
filter_acc p (x:xs) ys = if (p x) then filter_acc p xs (ys++[x]) else filter_acc p xs ys

-- Problem 1.2
rev_acc::[a] -> [a] -> [a]
rev_acc [] ys = ys
rev_acc (x:xs) ys = rev_acc xs (x:ys)

-- Problem 1.3
mapr f = foldr (\y ys -> (f y):ys) []

filterr f = foldr (\y ys -> if (f y) then y:ys else ys) []
