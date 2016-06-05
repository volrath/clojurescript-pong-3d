(ns pong.logic
  (:require [pong.controls :refer [paddle-movement]]
            [pong.defs :refer [field-size up-to-wins]]
            [pong.game-objects :refer [paddle-depth paddle-height paddle-width]]))

(def paddle-speed 2)

(defn -paddle-x [pos]
  (let [border-dist (/ (:width field-size) 2)]
    (if (= pos :left)
      (+ (- border-dist) paddle-width)
      (- border-dist paddle-width))))

(defn -create-paddle [side]
  {:position {:x (-paddle-x side) :y 0 :z paddle-depth}
   :side side
   :collide false})

(defn -create-ball [pointing-to]
  (let [vx (if (= pointing-to :player-1) -1 1)]
    {:position {:x 0 :y 0 :z 0}
     :velocity {:x vx :y (rand-nth [1 -1])}
     :hit false}))

;;
;; Game Elements
;; -----------------------------------------------------------------------------

(def elements
  {:ball (-create-ball (rand-nth [:player-1 :player-2]))
   :paddle-1 (-create-paddle :left)
   :paddle-2 (-create-paddle :right)})

(def scores (atom '()))  ; Stack of scorers, maybe later I'll add 'scoring time'


;;
;; Paddle Physics
;; -----------------------------------------------------------------------------

(defn -alert-invalid-move-for-paddle [{:keys [collide] :as paddle}]
  ;; (update curr-position :z #(* (- 10 %) 0.2))
  (println "boundary reached" (:side paddle))
  (assoc paddle :collide true))  ; Still missing turning it back to false.

(defn -move-paddle [paddle direction]
  (let [op (if (= direction :up) + -)]
    (update-in paddle [:position :y] op paddle-speed)))

(defn -paddle-within-boundaries [{:keys [y]}]
  (let [half-boundary (* (:height field-size) 0.45)]
    (and (< y half-boundary)
         (> y (- half-boundary)))))

(defn -update-paddle [{:keys [side] :as paddle}]
  (if-let [direction (get @paddle-movement side)]
    (let [new-paddle (-move-paddle paddle direction)]
      (if (-paddle-within-boundaries (:position new-paddle))
        new-paddle
        (-alert-invalid-move-for-paddle paddle)))
    paddle))

;;
;; Ball Physics
;; -----------------------------------------------------------------------------

(defn -scored? [x]
  (> (js/Math.abs x) (/ (:width field-size) 2)))

(defn -reset-ball []
  (-create-ball (first @scores)))

(defn -boundary-collision-velocity-alteration [{{y :y} :position :as ball}]
  "If the ball collides with the world boundaries (y-axis), vy gets inverted"
  (let [half-boundary (/ (:height field-size) 2)]
    (if (or (<= y (- half-boundary))
            (>= y half-boundary))
      (update-in ball [:velocity :y] -)
      ball)))

(defn -paddle-collision-velocity-alteration
  [{{bx :x by :y} :position ball-velocity :velocity :as ball}
   {{px :x py :y} :position side :side}]
  "If the ball collides with paddles, vx gets inverted and vy gets slightly
  modified"
  (let [op (if (= side :left) < >)
        ball-towards-paddle (op (:x ball-velocity) 0)
        half-paddle-height (/ paddle-height 2)]
    (if (and ball-towards-paddle
             (<= bx (+ px paddle-width))
             (>= bx px)                            ; Hitting paddle in x-axis
             (<= by (+ py half-paddle-height))
             (>= by (- py half-paddle-height)))    ; Ball and paddle vertically aligned y-axis
      (-> ball
          (assoc :hit true)
          (update-in [:velocity :x] -))            ; TODO: make y more aggresive
      ball)))

(defn -alter-velocity [ball paddle-1 paddle-2]
  "Returns a new ball with its velocity altered"
  (-> ball
      -boundary-collision-velocity-alteration
      (-paddle-collision-velocity-alteration paddle-1)
      (-paddle-collision-velocity-alteration paddle-2)))

(defn -update-ball [ball paddle-1 paddle-2]
  (let [{{ball-x :x} :position} ball
        update-position (fn [b axis]
                          (update-in b [:position axis] + (-> b :velocity axis)))]
    (if (-scored? ball-x)
      (-reset-ball)
      (-> ball
          (-alter-velocity paddle-1 paddle-2)
          (update-position :x)
          (update-position :y)))))


;;
;; General Logic
;; -----------------------------------------------------------------------------

(defn player-score [player scores]
  (count (filter #{player} scores)))

(defn game-over? []
  (or (= (player-score :player-1 @scores) up-to-wins)
      (= (player-score :player-2 @scores) up-to-wins)))

(defn update-movement [{:keys [paddle-1 paddle-2 ball] :as elems}]
  (if (not (game-over?))
    {:paddle-1 (-update-paddle paddle-1)
     :paddle-2 (-update-paddle paddle-2)
     :ball (-update-ball ball paddle-1 paddle-2)}
    elems))

(defn match-score [{{x :x} :position}]
  (if (and (not (game-over?)) (-scored? x))
    (let [scorer (if (> x 0) :player-1 :player-2)]
      (swap! scores conj scorer))))
