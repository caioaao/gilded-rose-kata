(ns gilded-rose.core)

(defn of-category? [item category]
  (= (:category item) category))

(defn regular? [item]
  (or (not (:category item))
      (of-category? item :regular)))

(defn legendary? [item]
  (of-category? item :legendary))

(defn aged? [item]
  (of-category? item :aged))

(defn backstage-pass? [item]
  (of-category? item :backstage-pass))

(defn conjured? [item]
  (:conjured? item))

(defn update-sell-in [item]
  (if (not (legendary? item))
    (update item :sell-in dec)
    item))

(defn double-quality-decrease [new-item old-item]
  (let [quality-delta (- (:quality new-item)
                         (:quality old-item))]
    (if (< quality-delta 0)
      (update new-item :quality + quality-delta)
      new-item)))

(defmulti cat-update-quality :category)

(defmethod cat-update-quality :default
  [item]
  (if (< (:sell-in item) 0)
    (update item :quality - 2)
    (update item :quality dec)))

(defmethod cat-update-quality :legendary
  [item]
  item)

(defmethod cat-update-quality :aged
  [item]
  (if (< (:quality item) 50)
    (update item :quality inc)
    item))

(defmethod cat-update-quality :backstage-pass
  [item]
  (-> (condp <= (:sell-in item)
        10    (update item :quality + 1)
        5     (update item :quality + 2)
        0     (update item :quality + 3)
        (assoc item :quality 0))
      (update :quality min 50)))

(defn update-quality [item]
  (cond-> (cat-update-quality item)
    (conjured? item) (double-quality-decrease item)
    :default         (update :quality max 0)))

(defn update-item [item]
  (-> item
      update-sell-in
      update-quality))

(defn item [item-name sell-in quality]
  {:name     item-name
   :sell-in  sell-in
   :quality  quality})

(defn as-legendary [item]
  (assoc item :category :legendary))

(defn as-regular [item]
  (assoc item :category :regular))

(defn as-aged [item]
  (assoc item :category :aged))

(defn as-conjured [item]
  (assoc item :conjured? true))

(defn sulfuras []
  (as-legendary
   (item "Sulfuras, Hand Of Ragnaros" 0 80)))

(defn aged-brie [sell-in quality]
  (as-aged (item "Aged Brie" sell-in quality)))

(defn backstage-pass [description sell-in quality]
  (-> (item description sell-in quality)
      (assoc :category :backstage-pass)))

(defn update-current-inventory[]
  (let [inventory [(item "+5 Dexterity Vest" 10 20)
                   (item "Aged Brie" 2 0)
                   (item "Elixir of the Mongoose" 5 7)
                   (item "Sulfuras, Hand Of Ragnaros" 0 80)
                   (item "Backstage passes to a TAFKAL80ETC concert" 15 20)]]
    (map update-quality inventory)))
