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

(defspec quality-decreases-by-one-before-sell-in-date
  (prop/for-all [item (gen/such-that #(-> % :sell-in pos?) (s/gen ::item/item) 30)]
                (is (= (dec (:quality item))
                       (:quality (item/on-next-day item))))))

(defspec quality-decreases-by-two-after-sell-in-date
  (prop/for-all [item (gen/such-that #(<= (:sell-in %) 0) (s/gen ::item/item) 30)]
                (is (= (- (:quality item) 2)
                       (:quality (item/on-next-day item))))))

(defspec sell-in-decreases-by-one
  (prop/for-all [item (s/gen ::item/item)]
                (is (= (dec (:sell-in item))
                       (:sell-in (item/on-next-day item))))))

(defspec quality-cant-be-negative
  (prop/for-all [item (s/gen ::item/item)]
                (is (nat-int? (:quality (item/on-next-day item))))))
