(ns pong.logic
  (:require [pong.controls :refer [paddle-movement]]
            [pong.defs :refer [field-size up-to-wins]]
            [pong.game-objects :refer [paddle-depth paddle-height paddle-width]]
            [pong.utils :refer [new-vector normalize v-scalar-* v+]]))

(def paddle-speed 2)
(def ball-min-speed 2)
(def ball-max-speed 5)
(def ball-angle-alteration 0.6)
(def ball-speed-alteration 0.4)

(defn -paddle-x [pos]
  (let [border-dist (/ (:width field-size) 2)]
    (if (= pos :left)
      (+ (- border-dist) paddle-width)
      (- border-dist paddle-width))))

(defn -create-paddle [side]
  {:position (new-vector (-paddle-x side) 0 paddle-depth)
   :direction nil
   :side side
   :collide false})

(defn -create-ball [pointing-to]
  (let [vx (if (= pointing-to :player-1) -1 1)]
    {:position (new-vector 0 0)
     :direction (normalize (new-vector vx (rand-nth [2 1 -1 -2]))) ; velocity's direction
     :speed 3
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
    (-> paddle
        (assoc :direction direction)
        (update-in [:position :y] op paddle-speed))))

(defn -paddle-within-boundaries [{:keys [y]}]
  (let [half-boundary (* (:height field-size) 0.45)]
    (and (< y half-boundary)
         (> y (- half-boundary)))))

(defn -control-paddle [{:keys [side] :as paddle}]
  (if-let [direction (get @paddle-movement side)]
    (let [new-paddle (-move-paddle paddle direction)]
      (if (-paddle-within-boundaries (:position new-paddle))
        new-paddle
        (-alert-invalid-move-for-paddle paddle)))
    (assoc paddle :direction nil)))

(defn -ai-opponent-paddle
  [{{py :y} :position :as paddle}  ; paddle
   {{by :y} :position}             ; ball
   reflexes]
  "Creates a super simple Lerp-ed AI to play against. It uses `reflexes` to set
  its difficulty: 0 easiest; 1 hardest. If calculated `new-y` is higher than
  permitted `paddle-speed` we clamp it to avoid cheating."
  (let [new-y (* (- by py) reflexes)]
    (if (= new-y 0)
      (assoc paddle :direction nil)
      (let [clamp-op (if (> new-y 0) max min)
            direction (if (> new-y 0) :up :down)]
        (-> paddle
            (update-in [:position :y] + (clamp-op paddle-speed new-y))
            (update :direction direction))))))

;;
;; Ball Physics
;; -----------------------------------------------------------------------------

(defn -scored? [x]
  (> (js/Math.abs x) (/ (:width field-size) 2)))

(defn -reset-ball []
  (-create-ball (first @scores)))

(defn -boundary-collision-direction-alteration [{{y :y} :position :as ball}]
  "If the ball collides with the world boundaries (y-axis), vy gets inverted"
  (let [half-boundary (/ (:height field-size) 2)]
    (if (or (<= y (- half-boundary))
            (>= y half-boundary))
      (update-in ball [:direction :y] -)
      ball)))

(defn -slice-ball-speed
  [{{dy :y} :direction speed :speed :as ball}  ; ball
   {:keys [direction]}]                        ; paddle
  "Returns a new speed scalar when the ball hits the given paddle. If the ball
  and the paddle were going on the same y-axis direction, speed gets increased,
  if not, it gets decreased. This method checks speed don't go off a
  `max-speed`-`min-speed` boundaries."
  (if direction
    (let [ndy (/ dy (js/Math.abs dy))  ; normalized vy
          paddle-scalar-direction (if (= direction :up) 1 -1)
          alteration (* ndy paddle-scalar-direction ball-speed-alteration)]
      (-> ball
          (update-in [:direction :y] #(- % (* paddle-scalar-direction ball-angle-alteration)))
          (update :direction normalize)
          (update :speed #(-> %
                              (+ alteration)
                              (min ball-max-speed)
                              (max ball-min-speed)))))
    ball))

(defn -paddle-collision-velocity-alteration
  [{{bpx :x bpy :y} :position {bdx :x} :direction :as ball}
   {{ppx :x ppy :y} :position side :side :as paddle}]
  "If the ball collides with paddles, vx gets inverted. If the ball was sliced,
  speed gets altered and vy's angle might change slightly."
  (let [op (if (= side :left) < >)
        ball-towards-paddle (op bdx 0)
        half-paddle-height (/ paddle-height 2)]
    (if (and ball-towards-paddle
             (<= bpx (+ ppx paddle-width))
             (>= bpx ppx)                            ; Hitting paddle in x-axis
             (<= bpy (+ ppy half-paddle-height))
             (>= bpy (- ppy half-paddle-height)))    ; Ball and paddle vertically aligned y-axis
      (-> ball
          (assoc :hit true)
          (update-in [:direction :x] -)
          (-slice-ball-speed paddle))
      ball)))

(defn -alter-velocity [ball paddle-1 paddle-2]
  "Returns a new ball with its velocity altered"
  (-> ball
      -boundary-collision-direction-alteration
      (-paddle-collision-velocity-alteration paddle-1)
      (-paddle-collision-velocity-alteration paddle-2)))

(defn -update-ball [{{old-ball-x :x} :position :as old-ball} paddle-1 paddle-2]
  (if (-scored? old-ball-x)
    (-reset-ball)
    (let [new-ball (-alter-velocity old-ball paddle-1 paddle-2)
          {:keys [direction speed]} new-ball
          velocity (v-scalar-* direction speed)]
      (update new-ball :position v+ velocity))))


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
    ;; {:paddle-1 (-control-paddle paddle-1)
    ;; :paddle-2 (-control-paddle paddle-2)
    {:paddle-1 (-control-paddle paddle-1)
     :paddle-2 (-ai-opponent-paddle paddle-2 ball 0.1)
     :ball (-update-ball ball paddle-1 paddle-2)}
    elems))

(defn match-score [{{x :x} :position}]
  (if (and (not (game-over?)) (-scored? x))
    (let [scorer (if (> x 0) :player-1 :player-2)]
      (swap! scores conj scorer))))
