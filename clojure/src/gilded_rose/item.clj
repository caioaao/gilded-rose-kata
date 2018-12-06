(ns gilded-rose.item
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::sell-in int?)

(s/def ::quality nat-int?)

(s/def ::item (s/keys :req-un [::name ::sell-in ::quality]))

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
