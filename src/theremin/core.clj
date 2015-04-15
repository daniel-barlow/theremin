(ns theremin.core
  (:require [overtone.inst.drum :as drum]
            [clojure.core.async :as async :refer [>! <! >!! <!!
                                                  chan go go-loop alts!!]])
  (:use [overtone.live]))



(definst trem [freq 440 depth 10 rate 6 length 3]
  (* 0.3
     (line:kr 0 1 length FREE)
     (saw (+ freq (* depth (sin-osc:kr rate))))))

(definst bass [freq 110 length 0.2 vol 0.5]
  (* vol
     (line:kr 1 0 length FREE)
     (+ (sin freq) (* 0.4 (+ (saw freq) (saw (/ freq 2)))))))



;; requirements:

;; 1) controller gestures should affect the music in a timely fashion,
;; when it's about volume, filters, etc
;; 2) there is a general pattern of 16 bar blocks.  Changes that affect the
;; melody or overall composition happen at block boundaries




(defn compose-block []
  (concat
   (map #(vector (* % 4) 'drum/dub-kick 60) (range 0 16))
   (map #(vector (* % 2) 'bass (+ 110 (* 65 (mod % 4)))) (range 0 32))))


(defn play-block [click block]
  (map (fn [[tick inst & params]]
         (prn inst)
         (at (click tick) (apply (eval inst) params)))
       block))

(play-block (metronome (* 4 140)) (compose-block))






#_
(stop)
