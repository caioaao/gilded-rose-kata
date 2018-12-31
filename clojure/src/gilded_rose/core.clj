(ns gilded-rose.core)

(defn regular? [item]
  (= (:category item) :regular))

(defn legendary? [item]
  (= (:category item) :legendary))

(defn update-sell-in [item]
  (if (not (legendary? item))
    (update item :sell-in dec)
    item))

(defn update-quality [item]
  (-> (cond
        (and (< (:sell-in item) 0) (= "Backstage passes to a TAFKAL80ETC concert" (:name item)))
        (assoc item :quality 0)

        (or (= (:name item) "Aged Brie") (= (:name item) "Backstage passes to a TAFKAL80ETC concert"))
        (if (and (= (:name item) "Backstage passes to a TAFKAL80ETC concert") (>= (:sell-in item) 5) (< (:sell-in item) 10))
          (merge item {:quality (inc (inc (:quality item)))})
          (if (and (= (:name item) "Backstage passes to a TAFKAL80ETC concert")
                   (>= (:sell-in item) 0)
                   (< (:sell-in item) 5))
            (merge item {:quality (inc (inc (inc (:quality item))))})
            (if (< (:quality item) 50)
              (merge item {:quality (inc (:quality item))})
              item)))

        (< (:sell-in item) 0)
        (if (regular? item)
          (merge item {:quality (- (:quality item) 2)})
          item)

        (regular? item)
        (merge item {:quality (dec (:quality item))})

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

(defn item [category item-name sell-in quality]
  {:category category
   :name     item-name
   :sell-in  sell-in
   :quality  quality})

(defn sulfuras []
  (item :legendary "Sulfuras, Hand Of Ragnaros" 0 80))

(defn update-current-inventory[]
  (let [inventory [(item "+5 Dexterity Vest" 10 20)
                   (item "Aged Brie" 2 0)
                   (item "Elixir of the Mongoose" 5 7)
                   (item "Sulfuras, Hand Of Ragnaros" 0 80)
                   (item "Backstage passes to a TAFKAL80ETC concert" 15 20)]]
    (map update-quality inventory)))
