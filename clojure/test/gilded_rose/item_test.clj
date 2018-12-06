(ns gilded-rose.item-test
  (:require [gilded-rose.core :as gilded-rose]
            [gilded-rose.item :as item]
            [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [clojure.spec.alpha :as s]))

(defn sanitize-quality [item]
  (update item :quality min (item/quality-limit item)))

(defn item-gen []
  (gen/fmap sanitize-quality (s/gen ::item/item)))

(defn exclude-items [item-gen items]
  (gen/such-that #(not (contains? items (:name %))) item-gen))

(defn within-items [item-gen items]
  (->> (gen/tuple (gen/elements items) item-gen)
       (gen/fmap (fn [[name item]] (assoc item :name name)))))

(defspec regular-item-quality-decreases-by-one-before-sell-in-date
  (prop/for-all [item (gen/such-that #(some-> % :sell-in pos?)
                                     (exclude-items (item-gen) item/aged-items)
                                     30)]
                (is (= (max 0 (dec (:quality item)))
                       (:quality (item/on-next-day item))))))

(defspec regular-item-quality-decreases-by-two-after-sell-in-date
  (prop/for-all [item (gen/such-that #(some-> % :sell-in (<= 0))
                                     (exclude-items (item-gen) item/aged-items)
                                     30)]
                (is (= (max 0 (- (:quality item) 2))
                       (:quality (item/on-next-day item))))))

(defspec sell-in-decreases-by-one
  (prop/for-all [item (-> (gen/such-that :sell-in (exclude-items (item-gen) item/legendary-items)))]
                (is (= (dec (:sell-in item))
                       (:sell-in (item/on-next-day item))))))

(defspec quality-cant-be-negative
  (prop/for-all [item (item-gen)]
                (is (nat-int? (:quality (item/on-next-day item))))))

(defspec quality-limit-is-50-unless-is-legendary
  (prop/for-all [item (gen/such-that #(not (contains? item/legendary-items (:name %)))
                                     (s/gen ::item/item))]
                (is (= 50 (item/quality-limit item)))))

(defspec quality-limit-is-80-for-sulfuras
  (prop/for-all [item (gen/fmap #(assoc  % :name "Sulfuras, Hand Of Ragnaros")
                                (s/gen ::item/item))]
                (is (= 80 (item/quality-limit item)))))

(defspec quality-is-never-above-threshold
  (prop/for-all [item (item-gen)]
                (is (<= (:quality (item/on-next-day item))
                        (item/quality-limit item)))))

(defspec aged-brie-quality-increases-over-time
  (prop/for-all [item (gen/such-that #(and (-> % :quality (< 50))
                                           (:sell-in %))
                                     (within-items (item-gen) ["Aged Brie"])
                                     50)]
                (is (< (:quality item) (:quality (item/on-next-day item))))))

(defspec sulfuras-never-changes
  (prop/for-all [item (-> (item-gen)
                          (within-items #{"Sulfuras, Hand Of Ragnaros"}))]
                (is (match? item (item/on-next-day item)))))


;;TODO test backstage passes
(defspec backstage-passes)
