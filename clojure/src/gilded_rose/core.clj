(ns gilded-rose.core
  (:require [gilded-rose.item :as item]
            [clojure.spec.alpha :as s]))

(s/def ::inventory (s/coll-of ::item/item))

(s/fdef update-quality
  :args (s/cat :items ::inventory)
  :ret ::inventory)

(defn update-quality [items]
  (map item/on-next-day items))

(defn update-current-inventory []
  (let [inventory
        [(item/item "+5 Dexterity Vest" 10 20)
         (item/item "Aged Brie" 2 0)
         (item/item "Elixir of the Mongoose" 5 7)
         (item/item "Sulfuras, Hand Of Ragnaros" 0 80)
         (item/item "Backstage passes to a TAFKAL80ETC concert" 15 20)]]
    (update-quality inventory)))
