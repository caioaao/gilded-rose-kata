(ns gilded-rose.item
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def aged-items #{"Aged Brie"})
(def legendary-items #{"Sulfuras, Hand Of Ragnaros"})
(defn backstage-passes? [name] (str/starts-with? name "Backstage passes"))

(s/def ::name string?)
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

(defn- backstage-passes-on-next-day
  [{:keys [sell-in quality] :as item}]
  (let [new-quality (cond
                      (<= sell-in 0)  0
                      (<= sell-in 5)  (+ quality 3)
                      (<= sell-in 10) (+ quality 2)
                      :default        (inc quality))]
    (-> item
        (update :sell-in #(and % (dec %)))
        (assoc :quality (min new-quality default-quality-limit)))))

(defmulti on-next-day :name)

(defmethod on-next-day :default
  [{:keys [name sell-in] :as item}]
  (if (backstage-passes? name)
    (backstage-passes-on-next-day item)
    (let [dec-quality (if (>= sell-in 0) dec #(- % 2))]
      (-> item
          (update :sell-in dec)
          (update :quality #(max (dec-quality %) 0))))))

(defmethod on-next-day "Aged Brie"
  [{:keys [sell-in] :as item}]
  (-> item
      (update :sell-in #(and % (dec %)))
      (update :quality #(min (inc %) default-quality-limit))))

(defmethod on-next-day "Aged Brie"
  [{:keys [sell-in] :as item}]
  (-> item
      (update :sell-in #(and % (dec %)))
      (update :quality #(min (inc %) default-quality-limit))))

(defmethod on-next-day "Sulfuras, Hand Of Ragnaros" [item] item)
