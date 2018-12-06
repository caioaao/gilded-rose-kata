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

(defn item-gen []
  (gen/fmap #(update % :quality min (item/quality-limit %)) (s/gen ::item/item)))

(defn regular-item-gen []
  (->> (item-gen)
       (gen/such-that #(not (contains? item/special-items (:name %))))))

(defn specific-item-gen [name]
  (gen/fmap #(assoc % :item name)
            (item-gen)))

(defspec regular-item-quality-decreases-by-one-before-sell-in-date
  (prop/for-all [item (gen/such-that #(-> % :sell-in pos?) (regular-item-gen) 30)]
                (is (= (max 0 (dec (:quality item)))
                       (:quality (item/on-next-day item))))))

(defspec regular-item-quality-decreases-by-two-after-sell-in-date
  (prop/for-all [item (gen/such-that #(<= (:sell-in %) 0) (regular-item-gen) 30)]
                (is (= (max 0 (- (:quality item) 2))
                       (:quality (item/on-next-day item))))))

(defspec sell-in-decreases-by-one
  (prop/for-all [item (item-gen)]
                (is (= (dec (:sell-in item))
                       (:sell-in (item/on-next-day item))))))

(defspec quality-cant-be-negative
  (prop/for-all [item (item-gen)]
                (is (nat-int? (:quality (item/on-next-day item))))))

(defspec quality-limit-is-always-50
  (prop/for-all [item (s/gen ::item/item)]
                (is (= 50 (item/quality-limit item)))))

(defspec quality-is-never-above-threshold
  (prop/for-all [item (item-gen)]
                (is (<= (:quality (item/on-next-day item))
                        (item/quality-limit item)))))

(defspec aged-brie-quality-increases-over-time
  (prop/for-all [item (specific-item-gen "Aged Brie")]
                (is (or (< (:quality item) (:quality (item/on-next-day item)))
                        (= (:quality item) 50)))))


