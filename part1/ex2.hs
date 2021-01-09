module Ex2(mapLB) where

import Ex1

foldrListBag :: (a -> t -> t) -> t -> ListBag a -> t
foldrListBag f acc (LB []) = acc 
foldrListBag f acc (LB ((x, y):xs)) = f x (foldrListBag f acc (LB xs))

instance Foldable ListBag where
    foldr = foldrListBag

mapLB :: Eq a1 => (a2 -> a1) -> ListBag a2 -> ListBag a1
mapLB f bag = fromList (map f (toList bag))

