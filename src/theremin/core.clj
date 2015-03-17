(ns theremin.core
  (:require [overtone.inst.drum :as drum])
  (:use [overtone.live]))


(definst foo [] (saw 220))

(definst trem [freq 440 depth 10 rate 6 length 3]
  (* 0.3
     (line:kr 0 1 length o/FREE)
     (saw (+ freq (* depth (sin-osc:kr rate))))))

(definst bass [freq 110 vol 0.5 length 0.2]
  (* (/ vol 2)
     (line:kr 1 0 length FREE)
     (+ (sin freq) (* 0.4 (+ (saw freq) (saw (/ freq 2)))))))


(defn play-tune [click start-time fun notes]
  (dorun
   (map
    (fn [[n note [ & dur]]]
      (at (click (+ start-time n))
          (fun note 0.9 (or dur 0.3))))
    notes)))

(defn incessantly [click n fun]
  (at (click n) (fun))
  (let [n1 (inc n)] (apply-by (click n1) #'incessantly click n1 fun [])))


(let [metro (metronome 140)
      metro2 (metronome 280)
      play (partial play-tune metro2)
      bassline [[0 100]
                [1 100 0.4]
                [2 100]
                [3 200]]]
  (dorun (incessantly metro2 0 #(drum/closed-hat 0.2)))
  (dorun (incessantly metro 0 #(drum/dub-kick 60)))
  (dorun (play 0 bass bassline)))

(stop)
