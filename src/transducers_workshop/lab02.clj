(ns transducers-workshop.lab02)

; Welcome to lab2 "creating your own transducers".
; We are going through a couple of examples, a stateless and a stateful transducers.

; Step1: create a stateless logging transducer.

; The logging transducer can be placed anywhere in the transducer chain.
; When enabled (for example with a parameter), it prints the
; value of the current step of the computation to standard output.
; Here's the prototypical transducer triple-arity function. There are 3 "nils"
; that you are requested to change into something else.
; zero-arity: called at reduction initialization. It needs to (at least)
;             propagate the call to other transducers in the chain (and ultimately
;             the reduce call that seats at the bottom of the chain).
;             "rf" (the transforming reducing function) represents your "view"
;             of the rest of the transducer chain). "(rf)" was called before
;             the "log" transducer to enter the zero-arity call. Do the same for your
;             transducer to initialize the transducer that follows.
; one-arity:  Called at reduction termination. Tear-down any state here.
;             Should always propagate down to other transducers and reduce call.
;             Again, "rf" is the view of the transducers (or reduce) coming after you.
; two-arity:  This is the reducing step of the reduction. Your are given the results so far
;             as "result" and the next item in the input as "input". This is a logging transducer,
;             so you are expected to print the results so far and the current element.
;             Remember to use "rf" to propagate the same call down the transducer after you.
; The enabled parameter is a boolean that you can use in a condition to enable or disable printing
; to the standard output. Please make sure to print only when "enabled" is "true".

(defn log
  [enabled]
  (fn [rf]
    (fn
      ([] nil)
      ([result] nil)
      ([result input] nil))))

;; When "enabled" is false, nothing is printed on screen:
(sequence (comp (log false) (filter odd?) (map inc)) (range 10))
;; (2 4 6 8 10)

; But when "enabled" is "true" we can see both the results so far as well as
; the current element at each step during the reduction process.
(transduce (comp (log true) (filter odd?) (map inc)) + (range 5))
;; acc 0 el 0
;; acc 0 el 1
;; acc 2 el 2
;; acc 2 el 3
;; acc 6 el 4
;; 6

; What happens if you move the log transducer after the filter or after the map?
; What do you see?

;; ####### STEP 2: creating a moving average stateful transducer.

; A moving average is the concept of updating the average of a series of number
; as soon as a new one enters the sequence. For example if the starting point is:

; (0 1 2)

; the average is
;
; (/ (+ 0 1 2) 3) ;= 1
;
; If now we add the number 5 to the sequence, we get
;
; (/ (+ 0 1 2 5) 4) ;= 2
;
; as the new average. Given a collection of numbers, we want to calculate all the average values
; as we fetch the next element from the sequence. In order to do that, we need to
; keep at least 2 numbers around: the sum of the elements seen so far and their count.
; This is the only way to calculate a new average given the next element.
; In other words, the transducer needs to remember something about the past,
; so we need to keep some for of state available for the reducing function.
; A typical pattern is to use a "volatile!" variable, a recently introduced mutable value
; which is ideal in situation where you need to "close over" with your function. An "atom"
; would be also possible although it would be unnecessarily heavy (with its compare and swap
; semantic) for the use case we have.
; Try to complete the skeleton below where the string "complete here" is.
; Also check for the example outputs:

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

; This is what you should see for a simple example:
(sequence (comp (map inc) (moving-average)) (range 10))
;; (1 3/2 2 5/2 3 7/2 4 9/2 5 11/2)

; The following adds a little more complexity and size to the problem
; by using mapcat to generate much more input.
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
