(ns gilded-rose.item
  (:require [clojure.spec.alpha :as s]))

(def special-items #{"Aged Brie" "Sulfuras, Hand Of Ragnaros"})

(s/def ::name (s/or :special-items special-items :regular-items string?))

(s/def ::sell-in int?)

(s/def ::quality nat-int?)

(s/def ::item (s/keys :req-un [::name ::sell-in ::quality]))

(def default-quality-limit 50)

(def quality-limits {"Sulfuras, Hand Of Ragnaros" 80})

(defn quality-limit [item]
  (get quality-limits (:name item) default-quality-limit))

(s/fdef item
  :args (s/cat :name ::name
               :sell-in ::sell-in
               :quality ::quality)
  :ret ::item)
(defn item [item-name, sell-in, quality]
  {:name item-name, :sell-in sell-in, :quality quality})

(defmulti on-next-day :item)

(defmethod on-next-day :default
  [{:keys [sell-in] :as item}]
  (let [dec-quality (if (> sell-in 0) dec #(- % 2))]
    (-> item
        (update :sell-in dec)
        (update :quality #(max (dec-quality %) 0)))))

(defmethod on-next-day "Aged Brie"
  [{:keys [sell-in] :as item}]
  (-> item
      (update :sell-in dec)
      (update :quality #(min (inc %) default-quality-limit))))
