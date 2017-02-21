--Luke Woodbury
--COSC 3015
--HW#12
--11/16/16


module LeftRec where

import Data.List

data Part = Epsilon | T String | NT String deriving (Eq, Ord)

-- this instantiation pretty-prints Parts. Comment it out and
--  add Show tot eh deriving clause for Part if you want to see
--  the raw form for debugging.
instance Show Part where
   show Epsilon = "#"
   show (T s) = s
   show (NT s) = s

is_nonterminal Epsilon = False
is_nonterminal (T _) = False
is_nonterminal (NT _) = True

name_of Epsilon = ""
name_of (T s) = s
name_of (NT s) = s

-- replace character c with string s in a list.  Used to help pretty-print grammars.
replace (c,s) [] = []
replace (c,s) (x:xs) = if c == x then s ++ replace (c,s) xs else (x: replace (c,s) xs)
decomma s = replace (',' , " ") (unwrap 1 s)


-- unwrap: deletes j elements from either end of a list.
--         used to pretty-print grammars.
unwrap j s = let k = length s in drop j (take (k-j) s)

data Production = P String [[Part]] deriving (Eq)

-- This instantiation pretty-prints Productions.  Comment it out
--  and add Show to the deriving clause for Production if you need to
--  see it in raw form for debugging.
instance Show Production where
  show (P name parts) =
    let s = replace (',' , " | ") (unwrap 1 (replace ('\"', "") (show (map (decomma . show) parts)))) in
      name  ++ " => " ++ s ++ "\n"

production_name (P name _) = name
names_in_prod (P name prods) = nub $ concatMap (map name_of) prods

-- is_left_recursive: returns true if a Production is left recursive.

is_left_recursive (P name prods) = some (\ps -> take 1 ps == [NT name]) prods
       where some p [] = False
             some p (x:xs) = p x || some p xs


-- thin gets rid of empty productions
thin (P name prods) = P name (filter (not . null) prods)



-- Some Assumptions about Productions -
--    Each list of parts in a production is unique - i.e. they are all different.
-- example : let p1 = P "E" [[T "Nat"],[NT "E", T "+", NT "E"], [NT "E", T "*", NT "E"]]

data Grammar = G String [Production] 

-- This instantiation pretty-prints Grammars.  Comment it out and add
--  Show to the deriving clause for Grammar if you need to see it
--  in raw form for debugging.

instance Show Grammar where
    show (G name productions) =
      let prods = unwrap 1 (replace (',', "") (show productions)) in
         "Grammar " ++ name ++ "\n" ++ prods

names_of_grammar (G _ productions) = nub $ concatMap names_in_prod productions


-- remove_all: takes a list of elements to be removed from the second list.
remove_all  xs [] = []
remove_all xs (y:ys) = if y `elem` xs then (remove_all xs ys) else (y : remove_all xs ys)


-- cleanup: This function deletes empty Productions and deletes all
--    references to them, also gets rid of right hand sides that are empty. 
cleanup (G name productions) =
   let productions' = map thin productions in
   let (empty, nonempty)  = partition (\(P name prods) -> null prods) productions' in
   let bad_nonterminals = map (NT . production_name) empty in
   let fixed = map (\ (P name prods) -> thin (P name ( nub (map (remove_all bad_nonterminals) prods)))) nonempty in
      (G name fixed)


-- new_name: used to get a string not included in "used_names" you get to suggest a name.

new_name proposed_name used_names =
   if proposed_name `elem` used_names then
      new_name (proposed_name ++ "'") used_names
   else
      proposed_name

-- interleave: interleaves elements from two lists.  Useful for crating the A and A' production sets.
interleave [] _ = []
interleave _ [] = []
interleave (x:xs) (y:ys) = x : y : (interleave xs ys)


-- Some assumptions about Grammars
--    Each non-terminal is associated with exactly one production
-- example: let g1 = G "Expression" [p1]


eliminate_left_recursion g @ (G gname productions) =
    let used_names = names_of_grammar g in
    let process_productions already_processed to_be_processed used_names =
          if (null to_be_processed) then
              already_processed
          else
              let (P name parts') = process_a_i already_processed (head to_be_processed) in
              let (p', new_prods, used_names') =
                              eliminate_direct_left_recursion (P name parts') used_names in
              let (already_processed', to_be_processed') =
                        if is_left_recursive p' then
                             (already_processed, [p'] ++ new_prods ++ to_be_processed)
                        else
                             (already_processed ++ [p'], new_prods ++ (drop 1 to_be_processed)) in
                  if null to_be_processed' then
                     already_processed'
                  else
                     process_productions already_processed' to_be_processed' used_names' in
    let productions' = process_productions [] productions used_names in                
      cleanup (G gname productions')



-- here's a newer simpler bit of code that also works if elimite_direct_left_recursion is correct ...
-- eliminate_left_recursion g @ (G gname productions) =
--     let used_names = names_of_grammar g in
--     let process_productions already_processed to_be_processed used_names =
--           if (null to_be_processed) then
--               already_processed
--           else
--               let (P name parts') = process_a_i already_processed (head to_be_processed) in
--               let (p', new_prods, used_names') = eliminate_direct_left_recursion (P name parts') used_names in
--                      process_productions (already_processed ++ [p']) (new_prods ++ (drop 1 to_be_processed)) used_names' in
--       cleanup (G gname ( process_productions [] productions used_names) )

         
eliminate_direct_left_recursion :: Production -> [String] -> (Production, [Production], [[Char]])
-- eliminate_direct_left_recursion: this is the function you must implement.  The first argument is the
--  production to eliminate direct left recurson from.  The second is a list fo names used so far.  Your
-- function should return a triple (p,ps',used_names') where p is the modified production that was passed in.
-- ps' is hte list of auxlilary productions built by your algorithm and used_names' is the list of used names
-- that were passed in together with any new names you may have created.  I have inserted a dummy body so the
-- file type checks.  Implement the algorithm described on page 260 of the paper "Removing Left Recursion from
-- Context-Free Grammars" by Robert Moore, included as a pdf with the assignment.  The simpler algorithm that
-- introduces Epsilon complicates the removal of indirect left recursion.

-- MY CODE -- MY CODE -- MY CODE -- MY CODE
eliminate_direct_left_recursion (p @ (P name parts)) used_names =
    if is_left_recursive p then
        let (left_rec , non_left_rec) = partition (\x -> (take 1 x) == [NT name]) parts in
        let name' = new_name name used_names in
        let alphas = map tail left_rec in
        let appended_betas = map (++ [NT name']) non_left_rec in
        let appended_alphas = map (++ [NT name']) alphas in
        let new_name_parts = interleave non_left_rec appended_betas in
        let new_name'_parts = interleave alphas appended_alphas in
             ((P name new_name_parts) , [(P name' new_name'_parts)] , (used_names ++ [name']))
    else 
         (p,[],used_names)
-- END MY CODE

process_a_i already_processed_prods (P a_i rsides) =
                 foldr foreach_a_j (P a_i rsides) already_processed_prods

foreach_a_j  (P a_j rsides_j) (P a_i rsides_i)  =
    let (left_corners, others) =
           partition (\parts -> (take 1 parts) == [NT a_j]) rsides_i in
    let left_corners' = map (\ (_ : alpha) -> (map (\beta -> beta ++ alpha) rsides_j)) left_corners in
            (P a_i (concat left_corners' ++ others))

-- a few test cases which are pink and green

test0 = G "lambda term no direct left recursion" [term, term']
  where term = P "TERM" [[T "var", NT "TERM'"],[T "Lambda", T "var", T "->", NT "TERM", NT "TERM'"]]
        term' = P "TERM'" [[NT "TERM", NT "TERM'"],[Epsilon]]

test1 = G "Lambda Terms" [P "TERM" [[T "var"],[NT "TERM", NT "TERM"],[T "Lambda", T "var", T "->", NT "TERM"]]]

test2 = G "A" [a,b]
  where a = P "A" [[NT "A", NT "A", NT "A"],[NT "B", T "foo"], [T "boo"]]
        b = P "B" [[NT "A"], [T "goo", NT "A"]]

