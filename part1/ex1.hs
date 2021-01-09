module Ex1
(
    ListBag(LB),
    wf,
    empty,
    singleton,
    fromList,
    isEmpty,
    mul,
    toList,
    sumBag
) where

data ListBag a = LB [(a, Int)] 
    deriving (Show, Eq)


wf :: Eq a => ListBag a -> Bool
wf (LB []) = True
wf (LB ((x, y):xs)) = count x (toList (LB xs)) == 0

empty :: ListBag a
empty = LB []

singleton :: a -> ListBag a
singleton v = LB [(v, 1)]

count :: (Foldable t, Eq a1, Num a2) => a1 -> t a1 -> a2
count e = foldr (\x acc -> if x == e then acc + 1 else acc) 0 

toTupleList :: (Num b, Eq a) => [a] -> [(a, b)]
toTupleList [] = []
toTupleList (x:xs) = ((x, 1 + count x xs):(toTupleList (filter (/= x) xs)))

fromList :: Eq a => [a] -> ListBag a
fromList lst = LB(toTupleList lst)

isEmpty :: ListBag a -> Bool
isEmpty (LB []) = True
isEmpty (LB (x:xs)) = False

mul :: Eq a => a -> ListBag a -> Int
mul v (LB []) = 0
mul v (LB ((x, y):xs))
    | x == v = y
    | x /= v = mul v (LB xs)

toList :: ListBag a -> [a]
toList (LB []) = []
toList (LB ((x, y):xs)) = (replicate y x) ++ (toList (LB xs))

sumBag :: Eq a => ListBag a -> ListBag a -> ListBag a
sumBag bag bag' = fromList ((toList bag) ++ (toList bag'))