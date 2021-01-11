module Ex2(mapLB) where

import Ex1

foldrListBag :: (a -> t -> t) -> t -> ListBag a -> t
foldrListBag f acc (LB []) = acc 
foldrListBag f acc (LB ((x, y):xs)) = f x (foldrListBag f acc (LB xs))

instance Foldable ListBag where
    foldr = foldrListBag

mapLB :: Eq a1 => (a2 -> a1) -> ListBag a2 -> ListBag a1
mapLB f bag = fromList (map f (toList bag))


{-
    instance Functor ListBag where
    fmap = mapLB

    No instance for (Eq b) arising from a use of `mapLB'
      Possible fix:
        add (Eq b) to the context of
          the type signature for:
            fmap :: forall a b. (a -> b) -> ListBag a -> ListBag b
    * In the expression: mapLB
      In an equation for `fmap': fmap = mapLB
      In the instance declaration for `Functor ListBag'


    This error is caused by the additional constraint (a1 of type Eq) in mapLB, needed in order to use the function fromList. 
    Instead, the fmap method, with type fmap :: (a -> b) -> f a -> f b, doesn't impose any constraint on the type a of the elements 
    that are inside the container
-}