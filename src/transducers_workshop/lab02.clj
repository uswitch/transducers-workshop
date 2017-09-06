(ns transducers-workshop.lab02)

; Welcome to lab2: creating your own transducers. We are going through a couple of examples, a stateless and a stateful transducers.

; Step1: create a logging transducer.

; The logging transducer can be placed anywhere in the transducer chain. When enabled (for example with a parameter), it prints the current step of the computation on the screen. Replace the "nil" in the function below with the correct behavior.

(defn log
  [enabled]
  (fn [rf]
    (fn
      ([] nil)
      ([result] nil)
      ([result input] nil))))

(sequence (comp (log false) (filter odd?) (map inc)) (range 10))
;; (2 4 6 8 10)
(transduce (comp (log true) (filter odd?) (map inc)) + (range 5))
;; acc 0 el 0
;; acc 0 el 1
;; acc 2 el 2
;; acc 2 el 3
;; acc 6 el 4
;; 6

; What happens if you move the log transducer further up or down the chain? What do you see?

;; ####### STEP 2: creating a moving average stateful transducer.

; A moving average is the concept of updating the average of a series of number as soon as a new one enters the set. For example if the starting point is (0 1 2) the average is (/ (+ 0 1 2) 3) = 1. If now we add the number 5 to the set, we get (/ (+ 0 1 2 5) 4) = 2 as the new average. Given a collection of numbers, we want to calculate all the average values as we fetch the next element from the collection. In order to do that, we need to keep at least 2 numbers around: the sum of the elements seen so far and their number. This is the only way to calculate a new average given the next element.

; In other words, the transducer needs to remember something about the past, so we need to keep some for of state available for the reducing function. A typical pattern is to use a "volatile!" variable, a recently introduced mutable value which is ideal in situation where you need to "close over" with your function. Try to complete the skeleton below. Also check for the example outputs:

(defn moving-average
  ([]
   (fn [rf]
     (let [acc (volatile! [0 0])]
       (fn
         ([] "complete here")
         ([result] "complete here")
         ([prev el]
          (vswap! acc (fn [[sum cnt]] "complete here"))
          "return something here")))))
  ([coll]
   (sequence (moving-average) coll)))

(sequence (comp (map inc) (moving-average)) (range 10))
;; (1 3/2 2 5/2 3 7/2 4 9/2 5 11/2)

(def avgs
  (sequence
    (comp (map dec)
          (mapcat range)
          (filter odd?)
          (moving-average)
          (map double))
    (range)))

(take 10 avgs)
;; (1.0 1.0 1.0 1.5 1.4 1.666666666666667 1.571428571428571 1.75 2.111111111111111 2.0)
