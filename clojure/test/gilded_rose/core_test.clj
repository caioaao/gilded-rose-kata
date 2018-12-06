(ns gilded-rose.core-test
  (:require [gilded-rose.core :as gilded-rose]
            [gilded-rose.item :as item]
            [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(deftest update-conforms-to-spec
  (is (match? {:result true :pass? true}
              (-> (stest/check `gilded-rose/update-quality)
                  first
                  :clojure.spec.test.check/ret))))

;; TODO remove after refactor
(defn deprecated-update-quality [items]
  (map
   (fn[item] (cond
              (and (< (:sell-in item) 0) (= "Backstage passes to a TAFKAL80ETC concert" (:name item)))
              (merge item {:quality 0})
              (or (= (:name item) "Aged Brie") (= (:name item) "Backstage passes to a TAFKAL80ETC concert"))
              (if (and (= (:name item) "Backstage passes to a TAFKAL80ETC concert") (>= (:sell-in item) 5) (< (:sell-in item) 10))
                (merge item {:quality (inc (inc (:quality item)))})
                (if (and (= (:name item) "Backstage passes to a TAFKAL80ETC concert") (>= (:sell-in item) 0) (< (:sell-in item) 5))
                  (merge item {:quality (inc (inc (inc (:quality item))))})
                  (if (< (:quality item) 50)
                    (merge item {:quality (inc (:quality item))})
                    item)))
              (< (:sell-in item) 0)
              (if (= "Backstage passes to a TAFKAL80ETC concert" (:name item))
                (merge item {:quality 0})
                (if (or (= "+5 Dexterity Vest" (:name item)) (= "Elixir of the Mongoose" (:name item)))
                  (merge item {:quality (- (:quality item) 2)})
                  item))
              (or (= "+5 Dexterity Vest" (:name item)) (= "Elixir of the Mongoose" (:name item)))
              (merge item {:quality (dec (:quality item))})
              :else item))
   (map (fn [item]
          (if (not= "Sulfuras, Hand of Ragnaros" (:name item))
            (merge item {:sell-in (dec (:sell-in item))})
            item))
        items)))

(defspec old-behavior-is-maintained-after-refactor
  (prop/for-all [inventory (s/gen ::gilded-rose/inventory)]
                (is (match? (deprecated-update-quality inventory)
                            (gilded-rose/update-quality inventory)))))
