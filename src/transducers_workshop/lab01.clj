(ns transducers-workshop.lab01
  (:require [clojure.edn :as edn]
            [transducers-workshop.xf :as xf])
  (:import java.util.Date))

(defn load-data
  "Load example feed from disk."
  []
  (edn/read-string (slurp "feed.edn")))

; Data in the feed are stored as the following group of keys. The
; :product key contains many other keys that are not displayed here
; for brevity. Our goal is to change the shape of this input map and be
; able to filter only the products we are interested. Have a look at
; the shape of the incoming data below:

; INPUT shape
; [{:fee-attributes []
;   :product {:key1 "val1" :key2 "val2"}
;   :created-at 111111}
;  {:fee-attributes []
;   :product {}
;   :created-at 111111}]

; If you want, also take a look at the actual feed in ./feed.edn.
; You'll see all the keys inside a product.
; We want to merge everything into the :product key, so after a few
; transformations, this is the expected shape of the output list.
; key1, key2 etc, are the keys previously contained inside the :product map
; that we are getting rid of:

; OUTPUT shape:
; [{:key1 "val1"
;   :key2 "val2"
;   :fee-attributes [1 2 3]
;   :created-at java.util.Date}
;  {:key1 "val1"
;   :key2 "val2"
;   :fee-attributes [1 2 3]
;   :created-at java.util.Date}]

; Task 1: prepare the data.
; Write a combination of transducers to perform the following:
; 1 Merge :fee-attributes and :create-at into the :product map.
; 2 Transform :created-at date from long into java.util.Date
; Place the transducers into the "xform" function. Test it using the
; "products" function.

(def xform
  ;; ... compose transducers here
  (comp))

;; tests your progress with:
(defn products [feed]
  (eduction xform feed))

; Is the map in the expected shape?
(first (products (load-data)))

; Task 2: now add filtering to perform the following:
; 1. Only show a product if it is :visible and :online (they should be true to be present in the list)
; 2. If the search params contain a :company-id, then filter for that company ID
; 3. If the search params contain a :repayment-method key (with a value :payment-method-repayment
;    for example) then only keep those products where that value (as a key) is true in the feed.
;    A product could have ":payment-method-repayment false" and in that case we don't want to see it.
; 4. If the search params contain a :loan-amount, only show products where
;    :min-loan-amount <= loan-amout <= :max-loan-amount

; Bonus: can you answer why wea are using eduction here? Why not sequence or transduce?

(defn xform [params]
  ;; ... add your filters to the comp.
  ;; They depend on the content of params.
  (comp))

;; tests your progress with:
(defn products [params feed]
  (eduction (xform params) feed))

(def example-params
  {:repayment-method :payment-method-repayment
   :loan-amount 1500000})

(def xs (products example-params (load-data)))
(count (seq xs))
; You should see 117 products with those filters.

; Task 3: store searches for company-id 46 and company-id 50.
; Next task is about creating defs for two very frequent searches
; (say that we notice 80% of our traffic comes from the same parameters
; pattern). Assuming we can accept to have them in memory all the time,
; this is boosting 80% of our traffic. The function "create-search"
; creates those searches. What would you use, eduction or sequence? Why?

(defn create-search [params]
  ;; ... create sequence here.
  )

(def company1 (create-search {:company-id 46}))
(def company2 (create-search {:company-id 50}))

; You should see something like the following:
(map :name company1)
; ("Green Professional Credit Intermediary 1.5% Buyer AA123 3 0.75% Legals"
;  "AAA132 IO A130 BBB124 AAA125 Older Self / only) Year"
;  "(DMS) Clear128 Fixed Intrinsic Part/Part Switcher AA130 BiR Tracker Reward")
(map :name company2)
; ("Loan Monthly AAA124 C/A Product A126 1% Tier 10 Starter")

; Bonus: what should I do if company-id 50 launches a new product in the feed?
