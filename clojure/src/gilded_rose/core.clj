(ns gilded-rose.core)

(defn update-sell-in [item]
  (if (not= "Sulfuras, Hand of Ragnaros" (:name item))
    (merge item {:sell-in (dec (:sell-in item))})
    item))

(defn update-quality [item]
  (-> (cond
        (and (< (:sell-in item) 0) (= "Backstage passes to a TAFKAL80ETC concert" (:name item)))
        (merge item {:quality 0})

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
        (if (or (= "+5 Dexterity Vest" (:name item)) (= "Elixir of the Mongoose" (:name item)))
          (merge item {:quality (- (:quality item) 2)})
          item)

        (or (= "+5 Dexterity Vest" (:name item)) (= "Elixir of the Mongoose" (:name item)))
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

(defn update-current-inventory[]
  (let [inventory [(item "+5 Dexterity Vest" 10 20)
                   (item "Aged Brie" 2 0)
                   (item "Elixir of the Mongoose" 5 7)
                   (item "Sulfuras, Hand Of Ragnaros" 0 80)
                   (item "Backstage passes to a TAFKAL80ETC concert" 15 20)]]
    (map update-quality inventory)))
