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

(defn update-sell-in [item]
  (if (not (legendary? item))
    (update item :sell-in dec)
    item))

(defn update-quality [item]
  (-> (cond
        (and (< (:sell-in item) 0) (backstage-pass? item))
        (assoc item :quality 0)

        (aged? item)
        (if (< (:quality item) 50)
          (merge item {:quality (inc (:quality item))})
          item)

        (backstage-pass? item)
        (-> (cond
              (< (:sell-in item) 5)  (update item :quality + 3)
              (< (:sell-in item) 10) (update item :quality + 2)
              :else                  (update item :quality + 1))
            (update :quality min 50))

        (< (:sell-in item) 0)
        (if (regular? item)
          (merge item {:quality (- (:quality item) 2)})
          item)

        (regular? item)
        (update item :quality dec)

        :else item)
      (update :quality max 0)))

(defn update-item [item]
  (-> item
      update-sell-in
      update-quality))

#_(defn update-quality [items]
  (->> items
       (map (fn [item]
              ))
       (map
        (fn[item] ))))

;; (defprotocol Item
;;   (on-next-day [this])
;;   (quality [this]))

;; (defn new-quality [previous-quality sell-in]
;;   (if (< sell-in 0)
;;     (max (- previous-quality 2) 0)
;;     (dec previous-quality)))

;; (defrecord RegularItem [description sell-in quality]
;;   Item
;;   (on-next-day [this]
;;     (let [new-sell-in (dec sell-in)]
;;       (-> (assoc this :sell-in new-sell-in)
;;           (update :quality new-quality new-sell-in)))))

;; (defrecord LegendaryItem []
;;   Item
;;   (quality [this] this))

;; (defrecord AgedItem [description sell-in]
;;   Item
;;   (quality [this]))

;; (defrecord BackstagePass [sell-in]
;;   Item
;;   (quality [this]))

;; (defn item->conjured [item]
;;   (reify Item
;;     (quality [_] (let [new-quality (quality item)]))))

;; (defn ->item [category, description, sell-in, quality]
;;   {:category    category
;;    :description description
;;    :sell-in     sell-in
;;    :quality     quality})

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
  (assoc item :category :conjured))

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
